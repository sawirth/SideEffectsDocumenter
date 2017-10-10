package ch.sawirth.services.implementation;

import ch.sawirth.services.IFileReaderService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReaderService implements IFileReaderService {
    @Override
    public String readFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        Path path = Paths.get(filePath);
        try {
            Files.lines(path).forEach(sb::append);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
