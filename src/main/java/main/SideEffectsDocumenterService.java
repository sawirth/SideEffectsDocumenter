package main;

import ch.sawirth.model.*;
import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.model.purano.MethodRepresentation;
import ch.sawirth.services.*;
import com.github.javaparser.ast.body.*;
import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SideEffectsDocumenterService {
    private final IDeserializationService deserializationService;
    private final IJavaParserService parserService;
    private final IDocumentationService documentationService;
    private final IFileReaderService fileReaderService;
    private final IMatchingService matchingService;
    private final Logger logger;
    private final IFileWriterService fileWriterService;

    @Inject
    public SideEffectsDocumenterService(IDeserializationService deserializationService,
                                        IJavaParserService parserService,
                                        IDocumentationService documentationService,
                                        IFileReaderService fileReaderService,
                                        IMatchingService matchingService,
                                        Logger logger, IFileWriterService fileWriterService)
    {
        this.deserializationService = deserializationService;
        this.parserService = parserService;
        this.documentationService = documentationService;
        this.fileReaderService = fileReaderService;
        this.matchingService = matchingService;
        this.logger = logger;
        this.fileWriterService = fileWriterService;
    }

    public Set<ClassRepresentation> importPuranoResult(String filePath) {
        String json = fileReaderService.readFromFile(filePath);
        return deserializationService.deserializePuranoResult(json);
    }

    public Set<JavaParserResult> parseJavaFiles(String rootPath) {
        return parserService.parseFilesFromPath(rootPath);
    }

    public Set<CallableDeclaration> createPurityDocumentations(
            Set<ClassRepresentation> classRepresentations,
            Set<JavaParserResult> javaParserResults)
    {
        Set<ClassOrInterfaceDeclaration> classDeclarations = getClassOrInterfaceDeclarations(javaParserResults);
        logger.info(String.format("Imported %d ClassDeclarations", classDeclarations.size()));
        Set<ClassAndPurityResultPair> classAndPurityResultPairs = getClassAndPurityResultPairs(classRepresentations,
                                                                                               classDeclarations);

        Set<MethodAndPurityResultPair> methodAndPurityResultPairs = getMethodAndPurityResultPairs(classAndPurityResultPairs);
        Set<CallableDeclaration> documentationResults = methodAndPurityResultPairs.stream()
                .map(documentationService::createDocumentation)
                .collect(Collectors.toSet());

        return documentationResults;
    }

    private Set<MethodAndPurityResultPair> getMethodAndPurityResultPairs(Set<ClassAndPurityResultPair> classAndPurityResultPairs) {
        Set<MethodAndPurityResultPair> methodAndPurityResultPairs = new HashSet<>();
        for (ClassAndPurityResultPair pair : classAndPurityResultPairs) {
            Set<CallableDeclaration> methodDeclarations = pair.classDeclaration.getMembers().stream()
                    .filter(m -> m instanceof CallableDeclaration)
                    .map(CallableDeclaration.class::cast)
                    .collect(Collectors.toSet());

            for (CallableDeclaration methodDeclaration : methodDeclarations) {
                MethodRepresentation methodRepresentation = matchingService.findMatchingMethodRepresentation(
                        methodDeclaration, pair.classRepresentation.methodMap);

                if (methodRepresentation != null) {
                    methodAndPurityResultPairs.add(new MethodAndPurityResultPair(methodRepresentation, methodDeclaration));
                }
                else
                {
                    logger.warning(String.format("No MethodDeclaration found for method %s.%s",
                                                 pair.classDeclaration.getNameAsString(),
                                                 methodDeclaration.getNameAsString()));
                }
            }

            Set<ConstructorDeclaration> constructorDeclarations = pair.classDeclaration.getMembers().stream()
                    .filter(m -> m instanceof ConstructorDeclaration)
                    .map(ConstructorDeclaration.class::cast)
                    .collect(Collectors.toSet());
        }

        return methodAndPurityResultPairs;
    }

    private Set<ClassOrInterfaceDeclaration> getClassOrInterfaceDeclarations(Set<JavaParserResult> parserResults) {
        Set<ClassOrInterfaceDeclaration> classDeclarations = new HashSet<>();
        for (JavaParserResult parserResult : parserResults) {
            for (TypeDeclaration<?> typeDeclaration : parserResult.compilationUnit.getTypes()) {
                if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                    classDeclarations.add((ClassOrInterfaceDeclaration) typeDeclaration);
                    classDeclarations.addAll(getInnerClassesRecursive((ClassOrInterfaceDeclaration) typeDeclaration));
                }
            }
        }

        return classDeclarations;
    }

    private Set<ClassAndPurityResultPair> getClassAndPurityResultPairs(Set<ClassRepresentation> classRepresentations,
                                                                       Set<ClassOrInterfaceDeclaration> classDeclarations)
    {
        Set<ClassAndPurityResultPair> classAndPurityResultPairs = new HashSet<>();
        for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
            ClassRepresentation classRepresentation = matchingService.findMatchingClassRepresentation(
                    classDeclaration, classRepresentations);

            if (classRepresentation != null) {
                classAndPurityResultPairs.add(new ClassAndPurityResultPair(classRepresentation, classDeclaration));
            } else {
                logger.warning(String.format("No ClassRepresentation found for class %s", classDeclaration.getNameAsString()));
            }
        }
        return classAndPurityResultPairs;
    }

    private Set<ClassOrInterfaceDeclaration> getInnerClassesRecursive(ClassOrInterfaceDeclaration classDeclaration) {
        Set<ClassOrInterfaceDeclaration> innerClassDeclarations = new HashSet<>();
        for (BodyDeclaration<?> bodyDeclaration : classDeclaration.getMembers()) {
            if (bodyDeclaration instanceof ClassOrInterfaceDeclaration) {
                innerClassDeclarations.addAll(getInnerClassesRecursive((ClassOrInterfaceDeclaration) bodyDeclaration));
                innerClassDeclarations.add((ClassOrInterfaceDeclaration) bodyDeclaration);
            }
        }

        return innerClassDeclarations;
    }

    public void createFilesForModifiedCompiliationUnits(Set<JavaParserResult> javaParserResults) {
        for (JavaParserResult result : javaParserResults) {
            fileWriterService.writeToFile(result.pathToFile, result.compilationUnit.toString());
        }
    }
}
