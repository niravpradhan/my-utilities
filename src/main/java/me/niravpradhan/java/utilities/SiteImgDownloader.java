package me.niravpradhan.java.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SiteImgDownloader {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: java SiteImgDownloader <Base Url> <Target Directory> <File Suffix>");
        }

        String baseUrl = args[0];
        String targetDirectory = args[1];
        String fileSuffix = args[2];
        String content = new String(Files.readAllBytes(Paths.get("webPage.html")));

        String fileSeperator = System.getProperty("file.separator");
        String fileExtension = ".png";

        Document document = Jsoup.parse(content);
        Elements imgElements = document.select("img");

        int totalImages = imgElements.size();
        int[] index = new int[1];
        AtomicInteger downloadedSoFar = new AtomicInteger(0);

        System.out.println("totalImages to download = " + totalImages);

        imgElements.forEach(e -> {
            String src = e.attr("src");
            if (src.isEmpty()) {
                src = e.attr("data-src");
            }
            String downloadUrl = (src.contains(" ")) ? baseUrl + src.replaceAll("\\s", "%20") : baseUrl + src;

            String fileName = targetDirectory + fileSeperator + fileSuffix + ++index[0] + fileExtension;

            CompletableFuture.runAsync(() -> download(downloadUrl, fileName, downloadedSoFar));
        });

        do {
            TimeUnit.SECONDS.sleep(5);
        } while (downloadedSoFar.get() <= totalImages);

        System.out.printf("Total Downloaded: %s%n", downloadedSoFar);
        System.out.println("Program completed");
    }

    private static void download(String dowloadUrl, String fileName, AtomicInteger downloadSoFar) {
        int tryCount = 0;
        boolean downloaded = false;
        try {
            URL url = new URL(dowloadUrl);
            do {
                try(FileOutputStream fos = new FileOutputStream(fileName);
                    InputStream is = url.openConnection().getInputStream();) {
                    is.transferTo(fos);
                    downloadSoFar.incrementAndGet();
                    System.out.printf("Created: %s ----> BaseUrl: %s%n", fileName, dowloadUrl);
                    downloaded = true;
                } catch (IOException e) {
                    System.out.printf("Downloaded failed: %s, tryCount: %d, BaseURL: %s%n", fileName, ++tryCount, dowloadUrl);
                }
            } while (!downloaded && ++tryCount <= 20);
        } catch (MalformedURLException e) {
            System.out.printf("Malrformed url: %s%n", dowloadUrl);
        }
    }


}
