package ch.sawirth.model;

import ch.sawirth.model.purano.MethodRepresentation;
import com.github.javaparser.ast.body.CallableDeclaration;

public class MethodAndPurityResultPair {
    public final MethodRepresentation methodRepresentation;
    public final CallableDeclaration methodDeclaration;

    public MethodAndPurityResultPair(MethodRepresentation methodRepresentation,
                                     CallableDeclaration methodDeclaration) {
        this.methodRepresentation = methodRepresentation;
        this.methodDeclaration = methodDeclaration;
    }
}
