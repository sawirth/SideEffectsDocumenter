package ch.sawirth.services;

import ch.sawirth.model.purano.ArgumentModifier;
import com.github.javaparser.ast.body.Parameter;

import java.util.List;

public interface IMessageCreationService {
    List<String> createArgumentModifierMessage(List<ArgumentModifier> argumentModifiers, List<Parameter> parameters);
}
