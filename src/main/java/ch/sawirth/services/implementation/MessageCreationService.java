package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.FieldModifier;
import ch.sawirth.services.IMessageCreationService;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class MessageCreationService implements IMessageCreationService {
    @Override
    public String createFieldModifierMessage(FieldModifier fieldModifier, MethodDeclaration methodDeclaration) {
        StringBuilder sb = new StringBuilder();
        String access = fieldModifier.hasDirectAccess ? "directly" : "indirectly";
        sb.append(String.format("Modifies the field '%s' %s", fieldModifier.name, access));

        if (hasNoDependencies(fieldModifier)) {
            sb.append(" without any dependencies)");
            return sb.toString();
        }

        sb.append("depending on ");
        if (!fieldModifier.dependsOnParameterFromIndex.isEmpty()) {
            if (fieldModifier.dependsOnParameterFromIndex.size() > 1) {
                sb.append("the parameters");
            } else {
                sb.append("the parameter");
            }

            List<String> parameterNames = new ArrayList<>();
            for (Integer index : fieldModifier.dependsOnParameterFromIndex) {
                parameterNames.add("'" + methodDeclaration.getParameter(index).getNameAsString() + "'");
            }

            sb.append(String.format(" %s", StringUtils.join(parameterNames, ", ")));
        }

        return sb.toString();
    }

    private boolean hasNoDependencies(FieldModifier fieldModifier) {
        return fieldModifier.dependsOnParameterFromIndex.isEmpty()
                && fieldModifier.fieldDependencies.isEmpty()
                && fieldModifier.staticFieldDependencies.isEmpty();
    }
}
