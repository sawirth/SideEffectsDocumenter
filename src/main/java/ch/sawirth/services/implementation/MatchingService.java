package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.model.purano.MethodRepresentation;
import ch.sawirth.services.IMatchingService;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MatchingService implements IMatchingService {
    private final Logger logger;

    @Inject
    public MatchingService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ClassRepresentation findMatchingClassRepresentation(
            ClassOrInterfaceDeclaration classDeclaration,
            Set<ClassRepresentation> puranoClassRepresentations)
    {
        List<ClassRepresentation> filtered = puranoClassRepresentations.stream()
                .filter(cp -> cp.fullName.endsWith("." + classDeclaration.getNameAsString())
                        || cp.fullName.endsWith("$" + classDeclaration.getNameAsString()))
                .collect(Collectors.toList());

        if (filtered.size() == 1) {
            return filtered.get(0);
        }

        try {
            CompilationUnit parentNode = (CompilationUnit)classDeclaration.getParentNode().get();
            PackageDeclaration packageDeclaration = parentNode.getPackageDeclaration().get();

            filtered = filtered.stream()
                    .filter(cp -> cp.fullName.contains(packageDeclaration.getNameAsString()))
                    .collect(Collectors.toList());

            if (filtered.size() == 1) {
                return filtered.get(0);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString());
        }

        return null;
    }

    public MethodRepresentation findMatchingMethodRepresentation(
            CallableDeclaration methodDeclaration,
            Set<MethodRepresentation> methodRepresentations)
    {
        List<MethodRepresentation> filtered = methodRepresentations.stream()
                .filter(m -> m.name.replace(".<init>", "").endsWith(methodDeclaration.getNameAsString()))
                .collect(Collectors.toList());

        if (filtered.size() == 1) {
            return filtered.get(0);
        }

        //Filter by number of arguments
        filtered = filtered.stream()
                .filter(m -> m.methodArguments.size() == methodDeclaration.getParameters().size())
                .collect(Collectors.toList());

        if (filtered.size() == 1) {
            return filtered.get(0);
        }

        //Filter by type names
        List<MethodRepresentation> filteredByParameterNames = new ArrayList<>();
        for (MethodRepresentation methodRepresentation : filtered) {
            if (hasParametersWithSameTypes(methodRepresentation, methodDeclaration)) {
                filteredByParameterNames.add(methodRepresentation);
            }
        }

        if (filteredByParameterNames.size() == 1) {
            return filteredByParameterNames.get(0);
        }

        //TODO overloaded methods where method has same names but different types (who the hell even writes such code ¯\_(ツ)_/¯)
        return null;
    }

    private boolean hasParametersWithSameTypes(MethodRepresentation methodRepresentation, CallableDeclaration methodDeclaration) {
        List<String> methodRepNames = methodRepresentation.methodArguments.stream()
                .map(argument -> argument.contains(".")
                        ? StringUtils.substringAfterLast(argument, ".")
                        : argument)
                .collect(Collectors.toList());

        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        List<String> methodDeclarationNames = parameters.stream()
                .map(p -> p.getType().asString())
                .collect(Collectors.toList());

        methodRepNames.removeAll(methodDeclarationNames);

        return methodRepNames.size() == 0;
    }
}
