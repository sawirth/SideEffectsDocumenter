package ch.sawirth.services;
import ch.sawirth.model.MethodAndPurityResultPair;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface IDocumentationService {
    MethodDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair);
}
