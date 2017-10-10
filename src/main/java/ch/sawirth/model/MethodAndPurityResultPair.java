package ch.sawirth.model;

import ch.sawirth.model.purano.MethodRepresentation;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodAndPurityResultPair {
    public final MethodRepresentation methodRepresentation;
    public final MethodDeclaration methodDeclaration;

    public MethodAndPurityResultPair(MethodRepresentation methodRepresentation,
                                     MethodDeclaration methodDeclaration) {
        this.methodRepresentation = methodRepresentation;
        this.methodDeclaration = methodDeclaration;
    }
}
