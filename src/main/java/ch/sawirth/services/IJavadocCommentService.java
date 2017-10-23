package ch.sawirth.services;

import com.github.javaparser.ast.comments.JavadocComment;

import java.util.List;

public interface IJavadocCommentService {

    /**
     * Appends the given lines to comment with the correct amount of whitespaces
     * @param comment The existing comment
     * @param additionalLines The lines to be added to the comment
     * @return New JavadocComment with the combined content
     */
    JavadocComment appendToComment(JavadocComment comment, List<String> additionalLines);

    /**
     * Creates a new JavadocComment with the provided lines
     * @param lines The lines to be added to the comment
     * @param column The amount of whitespaces needed to correctly align the comment with the method
     * @return New JavadocComment with the given lines as content
     */
    JavadocComment createNewComment(List<String> lines, int column);
}
