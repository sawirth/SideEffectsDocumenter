package ch.sawirth.services.implementation;

import ch.sawirth.services.IFileWriterService;
import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class FileWriterService implements IFileWriterService {
    private final Logger logger;

    @Inject
    public FileWriterService(Logger logger) {
        this.logger = logger;
    }

    public void writeToFile(Path path, String content) {
        if (Files.exists(path)) {
            String fileName = path.toString().replace(".java", "_purity.java");
            try {
                Files.write(Paths.get(fileName), content.getBytes());
            } catch (IOException e) {
                logger.warning(e.toString());
            }
        }
    }
}
