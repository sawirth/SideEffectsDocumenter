package ch.sawirth.services;
import ch.sawirth.model.MethodAndPurityResultPair;
import com.github.javaparser.ast.body.CallableDeclaration;

public interface IDocumentationService {
    CallableDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair);
}
