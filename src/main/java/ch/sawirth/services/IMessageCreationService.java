package ch.sawirth.services;

import ch.sawirth.model.purano.FieldModifier;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface IMessageCreationService {
    String createFieldModifierMessage(FieldModifier fieldModifier, MethodDeclaration methodDeclaration);
}
