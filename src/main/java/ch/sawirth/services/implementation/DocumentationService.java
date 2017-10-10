package ch.sawirth.services.implementation;

import ch.sawirth.model.MethodAndPurityResultPair;
import ch.sawirth.services.IDocumentationService;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;

public class DocumentationService implements IDocumentationService{

    @Override
    public MethodDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair) {
        createAndAddNewJavaDocComment(methodAndPurityResultPair.methodDeclaration, String.format("<b>Purity: %s</b>",
                                                    methodAndPurityResultPair.methodRepresentation.purityType));

        return methodAndPurityResultPair.methodDeclaration;
    }

    private void createAndAddNewJavaDocComment(MethodDeclaration methodDeclaration, String message) {
        Comment comment = new JavadocComment(message);
        methodDeclaration.setComment(comment);
    }
}
