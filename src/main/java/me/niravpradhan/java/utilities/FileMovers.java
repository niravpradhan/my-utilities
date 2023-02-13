package me.niravpradhan.java.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;

public class FileMovers {
    private AtomicInteger counter;

    private String sourceStr;
    private int startValue;
    private Path targetDir;

    public String getSourceStr() {
        return sourceStr;
    }

    public void setSourceStr(String sourceStr) {
        this.sourceStr = sourceStr;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public void init() {
        counter = new AtomicInteger(startValue);
        Path sourceDir = Paths.get(sourceStr);
        targetDir = Path.of(sourceDir.toString(), "target");
        if (Files.notExists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTargetFile(Path source) {
        if (source.toFile().isFile()) {
            String sourceStr = source.toString();
            String extension = sourceStr.substring(sourceStr.lastIndexOf(".") + 1);
            int newFileName = counter.incrementAndGet();
            Path target = Path.of(targetDir.toString(), newFileName + "." + extension);
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMoving() {
        try {
            Files.list(Paths.get(sourceStr)).forEach(this::createTargetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: FileMovers <Path To Folder> <Start Value>");
        }

        FileMovers fileMovers = new FileMovers();
        fileMovers.setSourceStr(args[0]);
        fileMovers.setStartValue(Integer.parseInt(args[1]));
        fileMovers.init();
        fileMovers.startMoving();
    }
}
