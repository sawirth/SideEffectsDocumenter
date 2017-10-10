package ch.sawirth.services.implementation;

import ch.sawirth.model.JavaParserResult;
import ch.sawirth.services.IJavaParserService;
import com.github.javaparser.JavaParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class JavaParserService implements IJavaParserService {
    public Set<JavaParserResult> parseFilesFromPath(String rootPath) {
        Set<Path> filePaths = getAllFilePaths(rootPath);

        Set<JavaParserResult> javaParserResults = new HashSet<>();
        filePaths.forEach(path -> {
            try {
                javaParserResults.add(new JavaParserResult(path, JavaParser.parse(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return javaParserResults;
    }

    private Set<Path> getAllFilePaths(String rootPath) {
        Set<Path> paths = new HashSet<>();
        try {
            Files.walk(Paths.get(rootPath))
                    .filter(path -> path.getFileName().toString().endsWith(".java")
                            && !path.getFileName().toString().endsWith("_purity.java"))
                    .forEach(paths::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return paths;
    }
}
