package ch.sawirth.services;

import ch.sawirth.services.implementation.JavadocCommentService;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.comments.JavadocComment;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavadocCommentServiceTest {
    private JavadocComment existingComment;
    private JavadocComment newComment;

    private List<String> linesToInsertOrAppend;

    @Test
    public void createNewComment_oneLine_commentExtendsOverThreeLines() {
        GivenContentToAppendOrInsert("just one line");
        WhenCreatingNewComment();
        ThenCommentExtendsOver(3);
    }

    @Test
    public void createNewComment_threeLinesOfText_commentHas5Lines() {
        GivenContentToAppendOrInsert("first line", "second line", "third line");
        WhenCreatingNewComment();
        ThenCommentExtendsOver(5);
    }

    @Test
    public void appendToComment_commentHasOneLineOfText_appendThreeLines_commentExtendsOverSixLines() {
        GivenExistingComment("does some things");
        GivenContentToAppendOrInsert("first line", "second line", "third line");
        WhenAppendingToExistingComment();
        ThenCommentExtendsOver(6);
    }

    private void GivenContentToAppendOrInsert(String... lines) {
        linesToInsertOrAppend = Arrays.stream(lines).collect(Collectors.toList());
    }

    private void GivenExistingComment(String text) {
        existingComment = new JavadocComment(System.lineSeparator() + "* " + text + System.lineSeparator());
        int rangeOfLines = 3;
        Position begin = new Position(0, 1);
        Position end = new Position(rangeOfLines - 1, 3);
        Range range = new Range(begin, end);
        existingComment.setRange(range);
    }

    private void WhenCreatingNewComment() {
        JavadocCommentService service = new JavadocCommentService();
        newComment = service.createNewComment(linesToInsertOrAppend, 4);
    }

    private void WhenAppendingToExistingComment() {
        JavadocCommentService service = new JavadocCommentService();
        newComment = service.appendToComment(existingComment, linesToInsertOrAppend);
    }

    private void  ThenCommentExtendsOver(int expectedNumberOfLines) {
        String content = newComment.getContent();
        String[] lines = content.split("\r\n");
        Assert.assertEquals(expectedNumberOfLines, lines.length);
    }
}
