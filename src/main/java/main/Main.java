package main;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception{

        String path = "C:\\Users\\Sandro\\Documents\\GitHub\\SideEffectsDocumenter\\src\\main\\java\\junk\\";
        String filename = "JavaParserTest.java";
        String outputFileName = "JavaParserTest.java";
        FileInputStream is = new FileInputStream(path + filename);

        CompilationUnit cu = JavaParser.parse(is);
        changeMethods(cu);

        Files.write(Paths.get(path + outputFileName), cu.toString().getBytes());
    }

    private static void changeMethods(CompilationUnit cu) {
        // Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    changeMethod(method);
                }
            }
        }
    }

    private static void changeMethod(MethodDeclaration n) {

        List<Parameter> intParams =  n.getParameters().stream()
                .filter(p -> Objects.equals(p.getType().getElementType().asString(), "int"))
                .collect(Collectors.toList());

        if (!n.hasJavaDocComment()) {
            Comment comment = new JavadocComment("<b>New Comment</b><br>\n");
            n.setComment(comment);
        }
        else
        {
            Optional<JavadocComment> comment = n.getJavadocComment();
            if (comment.isPresent()) {
                String newContent = "<b>Add purity</b><br>" + comment.get().getContent();
                Comment newComment = new JavadocComment(newContent);
                n.setComment(newComment);
            }
        }
    }
}
