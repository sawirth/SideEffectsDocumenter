package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.ArgumentModifier;
import ch.sawirth.model.purano.FieldDependency;
import ch.sawirth.model.purano.FieldModifier;
import ch.sawirth.model.purano.ReturnDependency;
import ch.sawirth.services.IMessageCreationService;
import com.github.javaparser.ast.body.Parameter;
import org.apache.commons.lang3.StringUtils;;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageCreationService implements IMessageCreationService {
    @Override
    public List<String> createArgumentModifierMessage(List<ArgumentModifier> argumentModifiers, List<Parameter> parameters) {
        List<String> result = new ArrayList<>();
        result.add("Modifies the following arguments:");

        for (ArgumentModifier modifier : argumentModifiers) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', 4));
            String name = parameters.get(modifier.argumentIndex).getNameAsString();
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
            sb.append(StringUtils.repeat(' ', 4));
            sb.append(String.format("%s.%s (%s)" , getShortOwner(modifier.owner), modifier.name, modifier.type));
            result.add(sb.toString());
        }

        return result;
    }

    private String getShortOwner(String owner) {
        return StringUtils.substringAfterLast(owner, "/");
    }

    @Override
    public List<String> createReturnDependencyMessage(ReturnDependency returnDependency, List<Parameter> parameters) {
        List<String> result = new ArrayList<>();
        result.add("Return value depends on the following:");

        for (Integer integer : returnDependency.indexOfDependentArguments) {
            String argumentType = parameters.get(integer - 1).getType().toString();
            String name = parameters.get(integer - 1).getNameAsString();

            FieldDependency correspondingFieldDep = getCorrespondingFieldDependencyOrNull(argumentType,
                                                                                          returnDependency.fieldDependencies);

            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', 4));
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

    private String createFieldDependencyMessage(FieldDependency fieldDependency, boolean isStaticField) {
        String title = isStaticField ? "Static Field" : "Field";
        String owner = isStaticField ? getShortOwner(fieldDependency.owner) : "this";
        return StringUtils.repeat(' ', 4) +
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
