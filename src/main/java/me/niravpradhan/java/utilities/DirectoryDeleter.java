package me.niravpradhan.java.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class DirectoryDeleter {

    private static String targetDir;
    private static String targetPath;

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java DirectoryDeleter <Target Path> <Target Directory To Be Deleted>");
        }

        targetPath = args[0];
        targetDir = args[1];
        try(Stream<Path> paths = Files.list(Paths.get(targetPath));) {
            paths.forEach(DirectoryDeleter::process);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void process(Path path) {
        if (Files.isDirectory(path) && path.getFileName().toString().contains(targetDir)) {
            deleteTargetDirectory(path);
        } else if(Files.isDirectory(path)) {
            File[] files = path.toFile().listFiles();
            Arrays.stream(files).map(File::toPath).forEach(DirectoryDeleter::process);
        }
    }

    private static void deleteTargetDirectory(Path path) {
        File[] files = path.toFile().listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                file.delete();
            } else {
                deleteTargetDirectory(file.toPath());
            }
        }
        path.toFile().delete();
    }
}
