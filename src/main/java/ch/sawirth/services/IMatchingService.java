package ch.sawirth.services;

import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.model.purano.MethodRepresentation;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Set;

public interface IMatchingService {
    ClassRepresentation findMatchingClassRepresentation(
            ClassOrInterfaceDeclaration classDeclaration,
            Set<ClassRepresentation> puranoClassRepresentations);

    MethodRepresentation findMatchingMethodRepresentation(
            MethodDeclaration methodDeclaration,
            Set<MethodRepresentation> methodRepresentations);
}
