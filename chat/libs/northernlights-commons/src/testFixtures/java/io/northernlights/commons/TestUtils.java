package io.northernlights.commons;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestUtils {

    private TestUtils() {
    }

    public static String readFile(String filepath) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(getTestFile(filepath), writer, UTF_8);
            return writer.toString();
        }
    }

    public static Stream<String[]> readCsvFile(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getTestFile(filepath)))) {
            return br.lines()
                .skip(1) // header
                .map(line -> Arrays.stream(line.split(";"))
                    .map(TestUtils::clearQuotes)
                    .toArray(String[]::new))
                .collect(Collectors.toList())
                .stream();
        }
    }

    public static String clearQuotes(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
//
//    @SneakyThrows(JsonProcessingException.class)
//    public static <T> T readJsonOrThrow(String pimProductJson, Class<T> targetClass) {
//        return new ObjectMapper().readValue(pimProductJson, targetClass);
//    }

    public static String readJsonFile(String filepath) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(getTestFile(filepath), writer, UTF_8);
            return writer.toString();
        }
    }

    public static InputStream getTestFile(final String filepath) {
        try {
            return new ClassPathResource(filepath).getInputStream();
        } catch (IOException e) {
            throw new TestInitializationException(filepath, e);
        }
    }

    private static class TestInitializationException extends RuntimeException {
        public TestInitializationException(String filepath, IOException e) {
            super("Test file error : " + filepath, e);
        }
    }
}
