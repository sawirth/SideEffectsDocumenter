package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.*;
import ch.sawirth.services.IMessageCreationService;
import ch.sawirth.utils.IODetectionHelper;
import com.github.javaparser.ast.body.Parameter;
import org.apache.commons.lang3.StringUtils;;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageCreationService implements IMessageCreationService {

    private final int indentionSpaces = 4;

    @Override
    public List<String> createArgumentModifierMessage(List<ArgumentModifier> argumentModifiers, List<Parameter> parameters) {
        List<String> result = new ArrayList<>();
        result.add("Modifies the following arguments:");

        for (ArgumentModifier modifier : argumentModifiers) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', indentionSpaces));
            String name = "";
            try {
                name = parameters.get(modifier.argumentIndex).getNameAsString();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                continue;
            }

            sb.append(name);
            result.add(sb.toString());
        }

        return result;
    }

    @Override
    public List<String> createStaticModifiersMessage(List<FieldModifier> staticFieldModifiers) {
        List<String> result = new ArrayList<>();
        result.add("Modifies the following static fields:");

        for (FieldModifier modifier : staticFieldModifiers) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', indentionSpaces));
            sb.append(String.format("%s.%s (%s)" ,
                                    getShortOwner(modifier.owner, "/"),
                                    modifier.name,
                                    getShortOwner(modifier.type, "/")));

            result.add(sb.toString());
        }

        return result;
    }

    @Override
    public List<String> createReturnDependencyMessage(ReturnDependency returnDependency, List<Parameter> parameters) {
        List<String> result = new ArrayList<>();
        result.add("Return value depends on the following:");

        for (Integer integer : returnDependency.indexOfDependentArguments) {
            String argumentType = "";
            try {
                argumentType = parameters.get(integer - 1).getType().toString();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                continue;
            }

            String name = parameters.get(integer - 1).getNameAsString();
            FieldDependency correspondingFieldDep = getCorrespondingFieldDependencyOrNull(argumentType,
                                                                                          returnDependency.fieldDependencies);

            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', indentionSpaces));
            sb.append("Argument: ");
            if (correspondingFieldDep != null) {
                sb.append(String.format("%s.%s (%s)", name, correspondingFieldDep.name, correspondingFieldDep.desc));

                //It has to be removed otherwise there would be duplicated information
                returnDependency.fieldDependencies.remove(correspondingFieldDep);
            } else {
                sb.append(String.format("%s (%s)", name, argumentType));
            }

            result.add(sb.toString());
        }

        for (FieldDependency fieldDependency : returnDependency.staticFieldDependencies) {
            result.add(createFieldDependencyMessage(fieldDependency, true));
        }

        for (FieldDependency fieldDependency : returnDependency.fieldDependencies) {
            result.add(createFieldDependencyMessage(fieldDependency, false));
        }

        return result;
    }

    @Override
    public List<String> createNativeEffectsMessage(Set<NativeEffect> nativeEffectSet) {
        List<String> result = new ArrayList<>();
        result.add("The method calls native code:");

        for (NativeEffect effect : nativeEffectSet) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', indentionSpaces));
            sb.append(String.format("%s.%s (origin: %s.%s",
                            getShortOwner(effect.owner, "."),
                            effect.name,
                            getShortOwner(effect.originOwner, "."),
                            effect.originName));

            if (IODetectionHelper.isPossibleIOClass(effect.owner) || IODetectionHelper.isPossibleIOClass(effect.originOwner)) {
                sb.append(" - Possible I/O)");
            } else {
                sb.append(")");
            }

            result.add(sb.toString());
        }

        return result;
    }

    private String createFieldDependencyMessage(FieldDependency fieldDependency, boolean isStaticField) {
        String title = isStaticField ? "Static Field" : "Field";
        String owner = isStaticField ? getShortOwner(fieldDependency.owner, "/") : "this";
        return StringUtils.repeat(' ', indentionSpaces) +
                title +
                ": " +
                String.format("%s.%s (%s)",
                              owner,
                              fieldDependency.name,
                              fieldDependency.desc);
    }

    private FieldDependency getCorrespondingFieldDependencyOrNull(String ownerName, Set<FieldDependency> fieldDependencies) {
        List<FieldDependency> correspondingFieldDeps = fieldDependencies.stream()
                .filter(d -> d.owner.contains(ownerName))
                .collect(Collectors.toList());

        if (correspondingFieldDeps.size() == 1) {
            return correspondingFieldDeps.get(0);
        }

        return null;
    }

    private String getShortOwner(String owner, String separator) {
        return StringUtils.substringAfterLast(owner, separator);
    }

    /**
     *
     * @param a
     * @return
     *
     * Modifies the following arguments
     *     test
     *     test
     *     test
     *
     * Modifies the following static fields
     *     test
     *     test
     *     test
     *
     * Return value depends on
     *     Static field: Class.test (int)
     *     Argument: param (int)
     *     Argument: rectangle.height (int)
     *     Field: this.field
     *     Field:
     */
    private int example(int a) {
        return 5;
    }
}
