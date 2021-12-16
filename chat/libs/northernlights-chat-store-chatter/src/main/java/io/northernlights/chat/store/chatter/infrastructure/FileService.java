package io.northernlights.chat.store.chatter.infrastructure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public interface FileService {

    boolean deleteIfExists(Path path) throws IOException;

    Path createFile(Path path, FileAttribute<?>... attrs) throws IOException;

    Path createDirectories(Path dir, FileAttribute<?>... attrs) throws IOException;

    String getTempDir();

    Path resolve(Path path, String other);

    Path get(String first, String... more);

    File toFile(Path file);
}
