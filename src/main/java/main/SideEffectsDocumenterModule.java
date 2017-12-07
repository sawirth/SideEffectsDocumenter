package main;

import ch.sawirth.services.*;
import ch.sawirth.services.implementation.*;
import com.google.inject.AbstractModule;
import main.binding.DoCreateLinks;

public class SideEffectsDocumenterModule extends AbstractModule {

    private boolean doExtendedDocumentation;
    private boolean doCreateLinks;

    public SideEffectsDocumenterModule(boolean doExtendedDocumentation, boolean doCreateLinks) {
        this.doExtendedDocumentation = doExtendedDocumentation;
        this.doCreateLinks = doCreateLinks;
    }

    @Override
    protected void configure() {
        if (doExtendedDocumentation) {
            bind(IDocumentationService.class).to(ExtendedDocumentationService.class);
        } else {
            bind(IDocumentationService.class).to(DocumentationService.class);
        }

        bindConstant().annotatedWith(DoCreateLinks.class).to(doCreateLinks);
        bind(IJavaParserService.class).to(JavaParserService.class);
        bind(IDeserializationService.class).to(DeserializationService.class);
        bind(IFileReaderService.class).to(FileReaderService.class);
        bind(IMatchingService.class).to(MatchingService.class);
        bind(IFileWriterService.class).to(FileWriterService.class);
        bind(IMessageCreationService.class).to(MessageCreationService.class);
        bind(IJavadocCommentService.class).to(JavadocCommentService.class);
    }
}
