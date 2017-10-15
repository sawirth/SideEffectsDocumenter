package ch.sawirth.services.implementation;

import ch.sawirth.model.MethodAndPurityResultPair;
import ch.sawirth.services.IDocumentationService;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentationService implements IDocumentationService{

    private static final String PURITY_REGEX = "[*]+\\s<b>Purity:[a-zA-Z0-9 ]+</b><br>";


    @Override
    public MethodDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair) {
        createAndAddNewJavaDocComment(methodAndPurityResultPair.methodDeclaration,
                                      methodAndPurityResultPair.methodRepresentation.purityType);

        return methodAndPurityResultPair.methodDeclaration;
    }

    private void createAndAddNewJavaDocComment(MethodDeclaration methodDeclaration, String purityType) {
        int column = 0;
        if (methodDeclaration.getRange().isPresent()) {
            column = methodDeclaration.getRange().get().begin.column;
        }

        if (methodDeclaration.getJavadocComment().isPresent()) {
            String content = methodDeclaration.getJavadocComment().get().getContent();
            Pattern pattern = Pattern.compile(PURITY_REGEX);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                //The purity info has to be replaced
                content = matcher.replaceAll(createPurityMessage(purityType, column, false));
            } else {
                //The purity info has to be added to the existing content
                content = createPurityMessage(purityType, column, false) + content;
            }

            content = content.replaceAll("\r\n\\s+\r\n", "\r\n");
            methodDeclaration.getJavadocComment().get().setContent(content);
        } else {
            JavadocComment comment = new JavadocComment(createPurityMessage(purityType, column, true));
            methodDeclaration.setJavadocComment(comment);
        }
    }

    private static String createPurityMessage(String purityType, int whitespaces, boolean isNewComment) {
        if (isNewComment) {
            return String.format(System.lineSeparator() + StringUtils.repeat(" ", whitespaces) +
                                         "* <b>Purity: %s</b><br>" + System.lineSeparator()
                                         + StringUtils.repeat(" ", whitespaces), purityType);
        }

        return String.format(System.lineSeparator() + StringUtils.repeat(" ", whitespaces) +
                                     "* <b>Purity: %s</b><br>", purityType);
    }
}
