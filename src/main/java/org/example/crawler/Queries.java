package org.example.crawler;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.lucene.store.FSDirectory.open;

public class Queries {

    String queryString = "";
    public Queries(String query){
        this.queryString = query;
    }

    public List<Document> searchCategory() throws ParseException, IOException {
        Directory indexDirectory = open(new File("src/main/resources/indexes").toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Query query = new QueryParser("category", analyzer).parse(queryString);
        System.out.println(queryString);
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 200);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        return documents;
    }
}
