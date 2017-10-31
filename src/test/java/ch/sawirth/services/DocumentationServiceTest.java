package ch.sawirth.services;

import ch.sawirth.model.MethodAndPurityResultPair;
import ch.sawirth.model.purano.MethodRepresentation;
import ch.sawirth.services.implementation.DocumentationService;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.javadoc.Javadoc;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;

public class DocumentationServiceTest {

    private MethodAndPurityResultPair methodAndPurityResultPair;
    private CallableDeclaration methodDeclaration;

    @Test
    public void createDocumentation_MethodHasNoJavaDocComment_NewJavaDocCommentIsCreated() {
        GivenMethodAndPurityResultPair("Stateless", null);
        WhenCreatingDocumentation();
        ThenPurityInfoInJavaDocIs("Stateless");
    }

    @Test
    public void createDocumentation_MethodHasJavaDocCommentWithoutPurityInfo_PurityInfoIsAddedToJavaDocComment() {
        GivenMethodAndPurityResultPair("Stateless", "* no purity in javadoc");
        WhenCreatingDocumentation();
        ThenPurityInfoInJavaDocIs("Stateless");
    }

    @Test
    public void createDocumentation_MethodHasJavaDocCommentWithPurityInfoAndPurityNotChanged_JavaDocCommentRemainsUnchanged() {
        GivenMethodAndPurityResultPair("Stateless", "* <b>Purity: Stateless</b><br>");
        WhenCreatingDocumentation();
        ThenPurityInfoInJavaDocIs("Stateless");
        ThenPurityInfoIsNotDuplicated();
    }

    @Test
    public void createDocumentation_MethodHasJavaDocCommentWithPurityAndPurityHasChanged_JavaDocCommentIsModified() {
        GivenMethodAndPurityResultPair("Stateless", "* <b>Purity: Stateful</b><br>");
        WhenCreatingDocumentation();
        ThenPurityInfoIsNotDuplicated();
        ThenPurityInfoInJavaDocIs("Stateless");
    }

    private void GivenMethodAndPurityResultPair(String purityType, String javaDocContent) {
        MethodRepresentation methodRepresentation = new MethodRepresentation(
                "test",
                purityType,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.emptySet());

        MethodDeclaration methodDeclaration = new MethodDeclaration();
        if (javaDocContent != null) {
            JavadocComment comment = new JavadocComment(javaDocContent);
            methodDeclaration.setJavadocComment(comment);
        }

        methodAndPurityResultPair = new MethodAndPurityResultPair(methodRepresentation, methodDeclaration);
    }

    private void WhenCreatingDocumentation() {
        IDocumentationService documentationService = new DocumentationService();
        methodDeclaration = documentationService.createDocumentation(methodAndPurityResultPair);
    }

    private void ThenPurityInfoInJavaDocIs(String purityInfo) {
        JavadocComment comment = (JavadocComment) methodDeclaration.getJavadocComment().get();
        Assert.assertTrue(comment.getContent().contains(purityInfo));
    }

    private void ThenPurityInfoIsNotDuplicated() {
        JavadocComment comment = (JavadocComment) methodDeclaration.getJavadocComment().get();
        int count = StringUtils.countMatches(comment.getContent(), "<b>Purity:");
        Assert.assertEquals(1, count);
    }
}
