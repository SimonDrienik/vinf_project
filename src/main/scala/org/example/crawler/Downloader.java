package org.example.crawler;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The type Downloader.
 */
public class Downloader {

    /**
     * Instantiates a new Downloader.
     */
    public Downloader(){

    }

    /**
     * Download content.
     *
     * @param url1 is url for downlaoding content of html source...<p> There are applied some cleaning techniques and controlling if valid url is provided...<p> suburl: is varaible where is saved suburl containing after <a href="https://en.wikipedia.or/wiki/">...</a>, that is used as name of downloaded file, that is unique..<p>. creates file with suburl name in ../contents/ folder where is stored downloaded content...<p> If url is valid, then content of url is downloaded with JSOUP and with BufferWritter saved to file...
     */
    public void downloadContent(String url1){
        String url = "";
        if (url1.length() > 3)
             url = url1.substring(1, url1.length() - 1);
        System.out.println(url);
        if (url.startsWith("https://en.wikipedia.org/wiki/")) {
            try {
                String suburl = url.substring(30);
                suburl = suburl.replaceAll("[^a-zA-Z0-9]", "_");
                File file = new File(new File (System.getProperty("user.dir")).getParent() + "/contents/" + suburl + ".html");
                file.createNewFile();
                String[] schemes = {"https"};
                UrlValidator urlValidator = new UrlValidator(schemes);
                if (urlValidator.isValid(url)) {

                    Connection.Response response = Jsoup.connect(url).execute();
                    String html = response.body();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File (System.getProperty("user.dir")).getParent() + "/contents/" + suburl + ".html"));
                    writer.write(html);
                    writer.close();

                }

            } catch (IOException e) {
                System.out.println("Download error: " + e.getMessage());
            }
        }
    }
}
