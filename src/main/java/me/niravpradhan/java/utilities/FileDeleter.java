package me.niravpradhan.java.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class FileDeleter {

    private static String fileName;
    private static String targetDir;

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java FileDeleter <Target Dir> <File Name>");
        }

        targetDir = args[0];
        fileName = args[1];
        try(Stream<Path> paths = Files.list(Paths.get(targetDir));) {
            paths.forEach(FileDeleter::processPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processPath(Path path) {
        if (path.toFile().isDirectory()) {
            File[] files = path.toFile().listFiles();
            Arrays.stream(files).map(File::toPath).forEach(FileDeleter::processPath);
        } else if (path.getFileName().toString().endsWith(fileName)) {
            path.toFile().delete();
        }
    }
}
