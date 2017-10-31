package ch.sawirth.utils;

import com.google.common.collect.Sets;
import java.util.Set;

public class IODetectionHelper {

    private static Set<String> ioLibraries = Sets.newHashSet(
            "java.io", "org.apache.commons.io", "java.sql");

    public static boolean isPossibleIOClass(String fullQualifierName) {
        return ioLibraries.stream()
                    .anyMatch(fullQualifierName::contains);
    }
}
