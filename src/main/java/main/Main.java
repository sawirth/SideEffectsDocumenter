package main;

import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.model.JavaParserResult;
import com.beust.jcommander.JCommander;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Arguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        Injector injector = Guice.createInjector(new SideEffectsDocumenterModule(arguments.doExtendedDocumentation));
        SideEffectsDocumenterService sideEffectsDocumenterService = injector.getInstance(SideEffectsDocumenterService.class);

        //The four steps of the program: Import Purano - Parse Java-Files - Documentation - File creation
        Set<ClassRepresentation> classRepresentations = sideEffectsDocumenterService.importPuranoResult(arguments.puranoFilePath);
        Set<JavaParserResult> javaParserResults = sideEffectsDocumenterService.parseJavaFiles(arguments.javaFilesRootFolder);
        sideEffectsDocumenterService.createPurityDocumentations(classRepresentations, javaParserResults);
        sideEffectsDocumenterService.createFilesForModifiedCompiliationUnits(javaParserResults);
    }
}
