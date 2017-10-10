package ch.sawirth.services;

import java.nio.file.Path;

public interface IFileWriterService {
    void writeToFile(Path path, String content);
}
