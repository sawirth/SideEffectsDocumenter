package ch.sawirth.utils;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class IODetectionHelper {
    private Set<String> ioLibraries;
    private static IODetectionHelper instance = null;

    private IODetectionHelper() {
        ioLibraries = Sets.newHashSet(
                "java.io", "org.apache.commons.io", "java.sql");
    }

    public void loadBlacklistTypes(String pathToBlacklist) {
        if (pathToBlacklist.isEmpty()) {
            return;
        }

        Set<String> lines = new HashSet<>();
        try (Stream<String> stream = Files.lines(Paths.get(pathToBlacklist))) {
            stream.filter(s -> !s.startsWith("#")).forEach(lines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ioLibraries = lines;
    }

    public static IODetectionHelper getInstance() {
        if (instance == null) {
            instance = new IODetectionHelper();
        }

        return instance;
    }

    public boolean isPossibleIO(String fullQualifierName) {
        return this.ioLibraries.stream()
                    .anyMatch(fullQualifierName::contains);
    }
}
