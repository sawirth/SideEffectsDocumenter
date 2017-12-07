package ch.sawirth.services;

import ch.sawirth.model.purano.ArgumentModifier;
import ch.sawirth.model.purano.FieldModifier;
import ch.sawirth.model.purano.NativeEffect;
import ch.sawirth.model.purano.ReturnDependency;
import com.github.javaparser.ast.body.Parameter;
import java.util.List;
import java.util.Set;

public interface IMessageCreationService {
    List<String> createArgumentModifierMessage(List<ArgumentModifier> argumentModifiers, List<Parameter> parameters);
    List<String> createStaticModifiersMessage(List<FieldModifier> staticFieldModifiers);
    List<String> createReturnDependencyMessage(ReturnDependency returnDependency, List<Parameter> parameters, boolean isAbstract, boolean isInterfaceMethod);
    List<String> createNativeEffectsMessage(Set<NativeEffect> nativeEffectSet, Set<String> importAndPackageDeclarations);
}
