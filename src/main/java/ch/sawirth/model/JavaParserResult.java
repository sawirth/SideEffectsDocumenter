package ch.sawirth.model;

import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Path;

public class JavaParserResult {
    public final Path pathToFile;
    public final CompilationUnit compilationUnit;

    public JavaParserResult(Path pathToFile, CompilationUnit compilationUnit) {
        this.pathToFile = pathToFile;
        this.compilationUnit = compilationUnit;
    }
}
