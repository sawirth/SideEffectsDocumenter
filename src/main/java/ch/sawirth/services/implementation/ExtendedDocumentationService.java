package ch.sawirth.services.implementation;

import ch.sawirth.model.MethodAndPurityResultPair;
import ch.sawirth.model.purano.MethodRepresentation;
import ch.sawirth.model.purano.ReturnDependency;
import ch.sawirth.services.IDocumentationService;
import ch.sawirth.services.IJavadocCommentService;
import ch.sawirth.services.IMessageCreationService;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.google.inject.Inject;
import main.binding.DoCreateHtmlTags;

import java.util.*;
import java.util.stream.Collectors;

public class ExtendedDocumentationService implements IDocumentationService {

    private final IMessageCreationService messageCreationService;
    private final IJavadocCommentService javadocCommentService;
    private final boolean doCreateHtmlTags;

    @Inject
    public ExtendedDocumentationService(IMessageCreationService messageCreationService,
                                        IJavadocCommentService javadocCommentService,
                                        @DoCreateHtmlTags boolean doCreateHtmlTags)
    {
        this.messageCreationService = messageCreationService;
        this.javadocCommentService = javadocCommentService;
        this.doCreateHtmlTags = doCreateHtmlTags;
    }

    @Override
    public CallableDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair) {
        CallableDeclaration methodDeclaration = methodAndPurityResultPair.methodDeclaration;
        MethodRepresentation methodRep = methodAndPurityResultPair.methodRepresentation;
        boolean hasAlreadyJavadoc = methodDeclaration.getJavadoc().isPresent();

        List<String> linesForComment = new ArrayList<>();
        linesForComment.add("Purity: " + methodRep.purityType + (doCreateHtmlTags ? "   <br>" : ""));

        if (!methodRep.argumentModifiers.isEmpty()) {
            linesForComment.add("");
            linesForComment.addAll(messageCreationService.createArgumentModifierMessage(methodRep.argumentModifiers,
                                                                                        methodDeclaration.getParameters()));
        }

        if (!methodRep.staticFieldModifiers.isEmpty()) {
            linesForComment.add("");
            linesForComment.addAll(messageCreationService.createStaticModifiersMessage(methodRep.staticFieldModifiers));
            linesForComment.add("");
        }

        Node parentNode = methodDeclaration.getParentNode().isPresent()
                ? methodDeclaration.getParentNode().get()
                : null;

        boolean isInterfaceMethod = false;
        if(parentNode != null && parentNode instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration parent = (ClassOrInterfaceDeclaration) parentNode;
            isInterfaceMethod = parent.isInterface();
        }

        if (hasReturnDependencyInfo(methodRep.returnDependency)) {
            linesForComment.add("");
            linesForComment.addAll(messageCreationService.createReturnDependencyMessage(methodRep.returnDependency,
                                                                                        methodDeclaration.getParameters(),
                                                                                        methodDeclaration.isAbstract(),
                                                                                        isInterfaceMethod));
        }

        if (!methodRep.nativeEffects.isEmpty()) {
            Set<String> importAndPackageDeclarations = getImportAndPackageDeclarations(methodDeclaration);
            linesForComment.add("");
            linesForComment.addAll(messageCreationService.createNativeEffectsMessage(methodRep.nativeEffects, importAndPackageDeclarations));
        }

        if (linesForComment.size() <= 0) {
            return methodDeclaration;
        }

        linesForComment = clearEmptyLines(linesForComment);

        JavadocComment comment;
        if (hasAlreadyJavadoc) {
            JavadocComment existingComment = (JavadocComment) methodDeclaration.getJavadocComment().get();
            comment = javadocCommentService.appendToComment(existingComment, linesForComment);
        } else {
            int column = 0;
            if (methodDeclaration.getRange().isPresent()) {
                column = methodDeclaration.getRange().get().begin.column;
            }

            comment = javadocCommentService.createNewComment(linesForComment, column);
        }

        methodDeclaration.setJavadocComment(comment);
        return methodDeclaration;
    }

    private Set<String> getImportAndPackageDeclarations(CallableDeclaration methodDeclaration) {
        Optional<Node> parentNode = Optional.ofNullable(findCompilationUnitParentNode(methodDeclaration.getParentNode().get()));
        if (parentNode.isPresent() && parentNode.get() instanceof CompilationUnit) {
            CompilationUnit compilationUnit = (CompilationUnit) parentNode.get();
            NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();

            Set<String> importAndPackageDeclarations = importDeclarations.stream()
                    .map(NodeWithName::getNameAsString)
                    .collect(Collectors.toSet());

            if (compilationUnit.getPackageDeclaration().isPresent()) {
                importAndPackageDeclarations.add(compilationUnit.getPackageDeclaration().get().getNameAsString());
            }

            return importAndPackageDeclarations;
        }

        return null;
    }

    private Node findCompilationUnitParentNode(Node node) {
        if (node != null && node instanceof CompilationUnit) {
            return node;
        }

        if (node != null && node.getParentNode().isPresent()) {
            return findCompilationUnitParentNode(node.getParentNode().get());
        }

        return null;
    }

    private List<String> clearEmptyLines(List<String> lines) {
        List<String> cleanedLines = new ArrayList<>();
        cleanedLines.add(lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i -1).equals("") && lines.get(i).equals("")) {
                continue;
            }

            cleanedLines.add(lines.get(i));
        }

        return cleanedLines;
    }

    private boolean hasReturnDependencyInfo(ReturnDependency returnDependency) {
        return !returnDependency.fieldDependencies.isEmpty()
                || !returnDependency.indexOfDependentArguments.isEmpty()
                || !returnDependency.staticFieldDependencies.isEmpty();
    }
}
