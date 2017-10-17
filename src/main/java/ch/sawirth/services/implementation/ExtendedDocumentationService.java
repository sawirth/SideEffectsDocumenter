package ch.sawirth.services.implementation;

import ch.sawirth.model.MethodAndPurityResultPair;
import ch.sawirth.services.IDocumentationService;
import ch.sawirth.services.IMessageCreationService;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.inject.Inject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ExtendedDocumentationService implements IDocumentationService {

    private final IMessageCreationService messageCreationService;

    @Inject
    public ExtendedDocumentationService(IMessageCreationService messageCreationService) {
        this.messageCreationService = messageCreationService;
    }

    @Override
    public MethodDeclaration createDocumentation(MethodAndPurityResultPair methodAndPurityResultPair) {
        //TODO implement extended documentation
        throw new NotImplementedException();
    }
}
