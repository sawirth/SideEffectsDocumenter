package ch.sawirth.services.implementation;

import ch.sawirth.model.MethodAndPurityResultPair;
import ch.sawirth.model.purano.MethodRepresentation;
import ch.sawirth.model.purano.ReturnDependency;
import ch.sawirth.services.IDocumentationService;
import ch.sawirth.services.IJavadocCommentService;
import ch.sawirth.services.IMessageCreationService;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ExtendedDocumentationService implements IDocumentationService {

    private final IMessageCreationService messageCreationService;
    private final IJavadocCommentService javadocCommentService;

    @Inject
    public ExtendedDocumentationService(IMessageCreationService messageCreationService,
                                        IJavadocCommentService javadocCommentService) {
        this.messageCreationService = messageCreationService;
        this.javadocCommentService = javadocCommentService;
    }

    @Override
    public MethodDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair) {
        MethodDeclaration methodDeclaration = methodAndPurityResultPair.methodDeclaration;
        MethodRepresentation methodRep = methodAndPurityResultPair.methodRepresentation;
        boolean hasAlreadyJavadoc = methodDeclaration.getJavadoc().isPresent();

        List<String> linesForComment = new ArrayList<>();
        linesForComment.add("Purity: " + methodRep.purityType);

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

        if (hasReturnDependencyInfo(methodRep.returnDependency)) {
            linesForComment.add("");
            linesForComment.addAll(messageCreationService.createReturnDependencyMessage(methodRep.returnDependency,
                                                                                        methodDeclaration.getParameters()));
        }

        //Todo Native

        if (linesForComment.size() <= 0) {
            return methodDeclaration;
        }

        linesForComment = clearEmptyLines(linesForComment);

        JavadocComment comment;
        if (hasAlreadyJavadoc) {
            comment = javadocCommentService.appendToComment(methodDeclaration.getJavadocComment().get(), linesForComment);
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
