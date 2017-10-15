package main;

import com.beust.jcommander.Parameter;

public class Arguments {
    @Parameter(description = "Path to root folder of .java files", required = true)
    String javaFilesRootFolder;

    @Parameter(names = {"-purano", "-p"}, description = "Path to Purano file", required = true)
    String puranoFilePath;

    @Parameter(names = {"-e", "-extended"}, description = "If set the documentation will contain extended information")
    boolean doExtendedDocumentation = false;

    @Parameter(names = {"-o", "-output"}, description = "The root of the output path for the modified files. " +
            "If nothing specified, the modified file will be created in the same location as the original file")
    String outputFilePath;

    @Parameter(names = {"-r", "-replace"}, description = "If set the original .java files will be overridden with the modified file. " +
            "Make sure you saved your original files and are using a VCS")
    boolean doOverrideOriginalFiles = false;
}
