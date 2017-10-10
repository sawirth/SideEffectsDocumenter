package ch.sawirth.services;

import ch.sawirth.model.JavaParserResult;

import java.util.Set;

public interface IJavaParserService {
    Set<JavaParserResult> parseFilesFromPath(String rootPath);
}
