package ch.sawirth.model.purano;

import java.util.List;

public class MethodRepresentation {
    public final String name;
    public final String purityType;
    public final List<MethodArgument> methodArguments;
    public final List<FieldModifier> fieldModifiers;

    public MethodRepresentation(String name,
                                String purityType,
                                List<MethodArgument> methodArguments,
                                List<FieldModifier> fieldModifiers) {
        this.name = name;
        this.purityType = purityType;
        this.methodArguments = methodArguments;
        this.fieldModifiers = fieldModifiers;
    }
}
