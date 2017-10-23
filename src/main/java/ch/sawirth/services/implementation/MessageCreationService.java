package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.ArgumentModifier;
import ch.sawirth.services.IMessageCreationService;
import com.github.javaparser.ast.body.Parameter;
import org.apache.commons.lang3.StringUtils;;
import java.util.ArrayList;
import java.util.List;

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
     *     test
     *     test
     *     test
     */
    private int example(int a) {
        return 5;
    }
}
