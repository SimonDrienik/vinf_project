import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.example.crawler.Crawler;
import org.example.crawler.Downloader;
import org.example.crawler.Queries;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CrawlerTest {


    /**
     * this test attemps to download and safe file that contains special character. if it was downloaded correctly and saved without special character then test passes
     * @throws IOException
     */
    @Test
    public void testDownload() throws IOException {
        Downloader downloader = new Downloader();
        downloader.downloadContent("https://en.wikipedia.org/wiki/Special:SpecialPages");
        File f = new File( new File (System.getProperty("user.dir")).getParent() + "/contents/Special_SpecialPages.html");
        System.out.println(f.getAbsolutePath());
        assert(f.exists() && !f.isDirectory());
    }

    /**
     * we know, that our dataset contains or should contain Microsoft.html page. If it is here and under category company, then test passes
     * @throws ParseException
     * @throws IOException
     */
    @Test
    public void queryTest() throws ParseException, IOException {
        Queries queries = new Queries("company");
        List<Document> documents = queries.searchCategory();
        ArrayList<String> docs = new ArrayList<>();
        for (Document document : documents) {
            docs.add(document.get("name"));
        }
        assert (docs.contains("Microsoft.html"));
    }


}
