package ch.sawirth.model;

import ch.sawirth.model.purano.ClassRepresentation;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ClassAndPurityResultPair {
    public final ClassRepresentation classRepresentation;
    public final ClassOrInterfaceDeclaration classDeclaration;

    public ClassAndPurityResultPair(ClassRepresentation classRepresentation,
                                    ClassOrInterfaceDeclaration classDeclaration) {
        this.classRepresentation = classRepresentation;
        this.classDeclaration = classDeclaration;
    }
}
