package ch.sawirth.services.implementation;

import ch.sawirth.services.IJavadocCommentService;
import com.github.javaparser.ast.comments.JavadocComment;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

public class JavadocCommentService implements IJavadocCommentService {
    @Override
    public JavadocComment appendToComment(JavadocComment comment, List<String> additionalLines) {
        int column = comment.getRange().get().begin.column;
        String additionalContent = buildStringFromLines(additionalLines, column);
        String existingContent = comment.getContent();
        comment.setContent(existingContent + additionalContent);
        return comment.clone();
    }

    @Override
    public JavadocComment createNewComment(List<String> lines, int column) {
        String content = System.lineSeparator() + buildStringFromLines(lines, column);
        return new JavadocComment(content);
    }

    private String buildStringFromLines(List<String> lines, int column) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append(StringUtils.repeat(' ', column));
            }

            sb.append("* ");
            sb.append(lines.get(i));
            sb.append(System.lineSeparator());
        }

        sb.append(StringUtils.repeat(' ', column));
        return sb.toString();
    }
}
