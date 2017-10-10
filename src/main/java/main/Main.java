package main;

import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.model.JavaParserResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new SideEffectsDocumenterModule());
        String puranoFilePath = "C:\\Users\\Sandro\\Documents\\GitHub\\SideEffectsDocumenter\\Purano-Result.json";
        SideEffectsDocumenterService sideEffectsDocumenterService = injector.getInstance(SideEffectsDocumenterService.class);

        String javaFilesRoot = "C:\\Users\\Sandro\\Documents\\GitHub\\purano\\src\\test";
        Set<ClassRepresentation> classRepresentations = sideEffectsDocumenterService.importPuranoResult(puranoFilePath);
        Set<JavaParserResult> javaParserResults = sideEffectsDocumenterService.parseJavaFiles(javaFilesRoot);

        Set<MethodDeclaration> modifiedFiles = sideEffectsDocumenterService.createPurityDocumentations(classRepresentations, javaParserResults);

        Path outputPath = Paths.get("\\modified");

        sideEffectsDocumenterService.createFilesForModifiedCompiliationUnits(javaParserResults);
    }
}
