package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.model.purano.MethodRepresentation;
import ch.sawirth.services.IMatchingService;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchingService implements IMatchingService {
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

        CompilationUnit parentNode = (CompilationUnit)classDeclaration.getParentNode().get();
        PackageDeclaration packageDeclaration = parentNode.getPackageDeclaration().get();

        filtered = filtered.stream()
                .filter(cp -> cp.fullName.contains(packageDeclaration.getNameAsString()))
                .collect(Collectors.toList());

        if (filtered.size() == 1) {
            return filtered.get(0);
        }

        return null;
    }

    public MethodRepresentation findMatchingMethodRepresentation(
            MethodDeclaration methodDeclaration,
            Set<MethodRepresentation> methodRepresentations)
    {
        List<MethodRepresentation> filtered = methodRepresentations.stream()
                .filter(m -> m.name.endsWith(methodDeclaration.getNameAsString()))
                .collect(Collectors.toList());

        if (filtered.size() == 1) {
            return filtered.get(0);
        }

        //TODO filter by method parameter names
        List<String> parameterNames = new ArrayList<>();
        for (Parameter parameter : methodDeclaration.getParameters()) {

        }

        return null;
    }
}
