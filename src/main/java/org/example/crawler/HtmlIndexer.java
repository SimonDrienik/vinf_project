package org.example.crawler;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlIndexer {
    private static StandardAnalyzer analyzer = new StandardAnalyzer();
    private IndexWriter writer;
    private List queue = new ArrayList();
    private String url = null;

    public HtmlIndexer(String indexesLocation, String url) throws IOException {
        FSDirectory directory = FSDirectory.open(new File(indexesLocation).toPath());
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
        this.url = url;
        System.out.println("test");
    }

    public void  doIndex(String fileToIndex) throws IOException {
        String indexLocation = null;
        HtmlIndexer indexer = null;
        try {
            indexLocation = "/Users/simondrienik/Documents/GitHub/vinf_project/indexes/";
            indexer = new HtmlIndexer("/Users/simondrienik/Documents/GitHub/vinf_project/indexes/", this.url);
            System.out.println(indexer.toString());
        } catch (Exception e) {
            System.out.println("Cannot create index..." + e.getMessage());
            System.exit(-1);
        }
        try {
            indexer.doIndexFile(fileToIndex);
            //indexer.closeIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void doIndexFile(String fileToIndex) throws IOException {
        FileReader fr =  null;
        try {
            File f = new File(fileToIndex);
            Document doc = new Document();
            fr = new FileReader(f);

            doc.add(new StringField("url", url, Field.Store.YES));
            doc.add(new StringField("filename", f.getName(), Field.Store.YES));
            doc.add(new StringField("path", f.getAbsolutePath(), Field.Store.YES));

            writer.addDocument(doc);
            writer.close();
        } catch (Exception e){
            System.out.println("Could not add: " + fileToIndex);
        } finally {
            assert fr != null;
            fr.close();
        }
    }

    public void closeIndex() throws IOException {
        writer.close();
    }


}
