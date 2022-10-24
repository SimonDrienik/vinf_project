package org.example.crawler;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.print.Doc;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.lucene.store.FSDirectory.open;

public class Indexer {

    private IndexWriter writer;
    private String url, filePath;

    public Indexer(String fileToIndex, String url) throws IOException {
        Directory indexDirectory = open(new File("/Users/simondrienik/Documents/GitHub/vinf_project/indexes").toPath());
        writer = new IndexWriter(indexDirectory, new IndexWriterConfig());
        this.url = url;
    }

    public void close() throws IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();
        StringField url = new StringField("url", this.url, Field.Store.YES);
        StringField name = new StringField("name", file.getName(), Field.Store.YES);
        StringField path = new StringField("path", file.getAbsolutePath(), Field.Store.YES);
        StringField category = new StringField("category", "", Field.Store.YES);

        document.add(url);
        document.add(name);
        document.add(path);
        document.add(category);

        return document;
    }

    private boolean isPerson() {
        String name1 = null;
        String name2 = null;
        int delimeter1 = filePath.lastIndexOf("/");
        int delimeter2 = filePath.lastIndexOf(" ");
        if (delimeter1 != -1)
        {
            name1 = filePath.substring(delimeter1, filePath.length() -1);
        }
        if (delimeter2 != -1)
        {
            name2 = filePath.substring(delimeter2, filePath.length() -1);
        }
        Pattern pattern1 = Pattern.compile("^<p>"+name1+" was born on ", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("^<p>"+name2+" was born on ", Pattern.CASE_INSENSITIVE);

        Pattern pattern3 = Pattern.compile("^<p>"+name1+" was born on ", Pattern.CASE_INSENSITIVE);
        Pattern pattern4 = Pattern.compile("^<p>"+name2+" was born on ", Pattern.CASE_INSENSITIVE);

        StringBuilder contentBuilder = new StringBuilder();
        boolean matchFound = false;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = in.readLine()) != null) {
                Matcher matcher1 = pattern1.matcher(line);
                Matcher matcher2 = pattern2.matcher(line);
                matchFound = matcher1.find();
                if (matchFound) break;
                matchFound = matcher2.find();
                if (matchFound) break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return matchFound;
    }

    private void indexFile(File file) throws IOException {
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public void createIndex(String filepath) throws IOException {
        File f = new File(filepath);
        this.filePath = filepath;
        if (!f.isHidden() &&
            f.exists() &&
            f.canRead()) {
            indexFile(f);
            close();
        }
    }


}
