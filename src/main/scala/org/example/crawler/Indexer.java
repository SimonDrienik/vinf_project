package org.example.crawler;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.apache.lucene.store.FSDirectory.open;

public class Indexer {

    private final IndexWriter writer;
    private String url;
    private String filePath;
    private final ArrayList<Pattern> patterns = new ArrayList<>();
    private final Pattern patternPerson;
    private final Pattern patternCompanyEmployes;
    private final Pattern patternCompanyIndustry;
    private final Pattern patternCompanyNetIncome;
    private final Pattern patternCompanyFounded;
    private final Pattern patternLocalityContinentEurope;
    private final Pattern patternLocalityContinentNorthAmerica;
    private final Pattern patternLocalityContinentSouthAmerica;
    private final Pattern patternLocalityContinentAsia;
    private final Pattern patternLocalityContinentAfrica;
    private final Pattern patternLocalityContinentAustralia;
    private final Pattern patternLocalityContinentAntarctica;
    private final Pattern patternLocalityStateRepublic;
    private final Pattern patternLocalityStateAnthem;
    private final Pattern patternLocalityCityCity;
    private final Pattern patternLocalityCityCapitalCity;
    private final Pattern patternLocalityCityTown;
    private final Pattern patternLocalityMunicipality;
    private final Pattern patternLocalityRegion;
    private final Pattern patternLocalityRegion2;
    private final Pattern patternLocalityRegion3;
    private final Pattern patternLocalityDisctrict;

    public Indexer() throws IOException {

        Directory indexDirectory = open(new File("src/main/resources/indexes/").toPath());
        writer = new IndexWriter(indexDirectory, new IndexWriterConfig());

        /* Person paterns **/
        this.patternPerson = Pattern.compile(".*(<tr><th scope=\"row\" class=\"infobox-label\">Born</th>).*", Pattern.CASE_INSENSITIVE);

        /* Company paterns **/
        this.patternCompanyEmployes = Pattern.compile(".*(<tr><th scope=\"row\" class=\"infobox-label\" style=\"padding-right: 0.5em;\"><div style=\"display: inline-block; line-height: 1.2em; padding: .1em 0;\">Number of employees</div></th>).*", Pattern.CASE_INSENSITIVE);
        this.patternCompanyIndustry = Pattern.compile(".*(<tr><th scope=\"row\" class=\"infobox-label\" style=\"padding-right: 0.5em;\">Industry</th>).*", Pattern.CASE_INSENSITIVE);
        this.patternCompanyNetIncome = Pattern.compile(".*(<tr><th scope=\"row\" class=\"infobox-label\" style=\"padding-right: 0.5em;\"><div style=\"display: inline-block; line-height: 1.2em; padding: .1em 0;\"><a href=\"/wiki/Net_income\" title=\"Net income\">Net income</a></div></th>).*", Pattern.CASE_INSENSITIVE);
        this.patternCompanyFounded = Pattern.compile(".*(<tr><th scope=\"row\" class=\"infobox-label\" style=\"padding-right: 0.5em;\">Founded</th><td class=\"infobox-data\" style=\"line-height: 1.35em;\">).*", Pattern.CASE_INSENSITIVE);

        /* Continent paterns **/
        //Europe
        this.patternLocalityContinentEurope = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">Europe</span></h1>).*", Pattern.CASE_INSENSITIVE);
        //North America
        this.patternLocalityContinentNorthAmerica = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">North America</span></h1>).*", Pattern.CASE_INSENSITIVE);
        //South America
        this.patternLocalityContinentSouthAmerica = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">South America</span></h1>).*", Pattern.CASE_INSENSITIVE);
        //Asia
        this.patternLocalityContinentAsia = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">Asia</span></h1>).*", Pattern.CASE_INSENSITIVE);
        //Africa
        this.patternLocalityContinentAfrica = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">Africa</span></h1>).*", Pattern.CASE_INSENSITIVE);
        //Australia
        this.patternLocalityContinentAustralia = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">Australia</span></h1>).*", Pattern.CASE_INSENSITIVE);
        //Antarctica
        this.patternLocalityContinentAntarctica = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">Antarctica</span></h1>).*", Pattern.CASE_INSENSITIVE);

        /* State or Country paterns **/
        this.patternLocalityStateRepublic = Pattern.compile(".*(<h1 id=\"firstHeading\" class=\"firstHeading mw-first-heading\"><span class=\"mw-page-title-main\">).*(Republic).*", Pattern.CASE_INSENSITIVE);
        this.patternLocalityStateAnthem = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-full-data anthem\"><b>Anthem:</b>).*", Pattern.CASE_INSENSITIVE);

        /* City paterns **/
        this.patternLocalityCityCity = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(City).*", Pattern.CASE_INSENSITIVE);
        this.patternLocalityCityCapitalCity = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(Capital city).*", Pattern.CASE_INSENSITIVE);
        this.patternLocalityCityTown = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(Town).*", Pattern.CASE_INSENSITIVE);
        this.patternLocalityMunicipality = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(municipality).*", Pattern.CASE_INSENSITIVE);

        /* Region paterns **/
        this.patternLocalityRegion = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(region).*", Pattern.CASE_INSENSITIVE);
        this.patternLocalityRegion2 = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(state).*", Pattern.CASE_INSENSITIVE);
        this.patternLocalityRegion3 = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(province).*", Pattern.CASE_INSENSITIVE);

        /* District paterns **/
        this.patternLocalityDisctrict = Pattern.compile(".*(<tr><td colspan=\"2\" class=\"infobox-subheader\"><div class=\"category\">).*(district).*", Pattern.CASE_INSENSITIVE);

        patterns.add(patternPerson);
        patterns.add(patternCompanyEmployes);
        patterns.add(patternCompanyIndustry);
        patterns.add(patternCompanyNetIncome);
        patterns.add(patternCompanyFounded);
        patterns.add(patternLocalityContinentEurope);
        patterns.add(patternLocalityContinentNorthAmerica);
        patterns.add(patternLocalityContinentSouthAmerica);
        patterns.add(patternLocalityContinentAsia);
        patterns.add(patternLocalityContinentAfrica);
        patterns.add(patternLocalityContinentAustralia);
        patterns.add(patternLocalityContinentAntarctica);
        patterns.add(patternLocalityStateRepublic);
        patterns.add(patternLocalityStateAnthem);
        patterns.add(patternLocalityCityCity);
        patterns.add(patternLocalityCityCapitalCity);
        patterns.add(patternLocalityCityTown);
        patterns.add(patternLocalityMunicipality);
        patterns.add(patternLocalityRegion);
        patterns.add(patternLocalityRegion2);
        patterns.add(patternLocalityRegion3);
        patterns.add(patternLocalityDisctrict);
    }

    /**
     * @throws IOException
     * method for closing indexer...
     */
    public void close() throws IOException {
        writer.close();
    }

    /**
     * @param file is content file with html source...<p>
     * @return created index document...<p>
     * @throws IOException <p>
     * creating fields for index document... <p>
     * runs returnCategory() method that returns vategory of file with regex patterns applied on file...
     */
    private Document getDocument(File file) throws IOException {

        String categoryValue = returnCategory();

        Document document = new Document();
        StringField url = new StringField("url", this.url, Field.Store.YES);
        StringField name = new StringField("name", file.getName(), Field.Store.YES);
        StringField path = new StringField("path", file.getAbsolutePath(), Field.Store.YES);
        StringField category = new StringField("category", categoryValue, Field.Store.YES);

        document.add(url);
        document.add(name);
        document.add(path);
        document.add(category);

        return document;
    }

    /**
     * @return category of file...<p>
     * @throws IOException <p>
     * going trough every pattern in pattern list that contains patterns from this class and in findRegex(pattern) is this pattern applied to every line in html file and ...<p>
     * if there is a match: in if else conditions is discovering which pattern from list matches with that line from html and after that is proper category or type returned...
     *
     */
    private String returnCategory() throws IOException {

        String type = "other";

        for (Pattern pattern : patterns) {
            if (findRegex(pattern)){
                if (    pattern == patternCompanyEmployes ||
                        pattern == patternCompanyIndustry ||
                        pattern == patternCompanyNetIncome ||
                        pattern == patternCompanyFounded
                ){
                    type = "company";
                    break;
                }
                else if (pattern == patternPerson){
                    type = "person";
                    break;
                }
                else if (   pattern == patternLocalityContinentAfrica ||
                            pattern == patternLocalityContinentAntarctica ||
                            pattern == patternLocalityContinentAsia ||
                            pattern == patternLocalityContinentAustralia ||
                            pattern == patternLocalityContinentNorthAmerica ||
                            pattern == patternLocalityContinentSouthAmerica ||
                            pattern == patternLocalityContinentEurope
                ){
                    type = "localitycontinent";
                    break;
                }
                else if (   pattern == patternLocalityStateAnthem ||
                            pattern == patternLocalityStateRepublic
                ){
                    type = "localitycountry";
                    break;
                }
                else if (   pattern == patternLocalityCityCapitalCity ||
                            pattern == patternLocalityCityCity ||
                            pattern == patternLocalityCityTown ||
                            pattern == patternLocalityMunicipality
                ){
                    type = "localitycity";
                    break;
                }
                else if (   pattern == patternLocalityRegion ||
                            pattern == patternLocalityRegion2 ||
                            pattern == patternLocalityRegion3
                ){
                    type = "localityregion";
                    break;
                }
                else if ( pattern == patternLocalityDisctrict ){
                    type = "localitydistrict";
                    break;
                }
            }
        }

        return type;
    }

    /**
     *
     * @param pattern represents each pattern from pattern list defined in this class...<p>
     * @return boolean if pattern was found in html file...<p>
     * applying pattern to every line of source code html file...
     */
    private Boolean findRegex(Pattern pattern){

        final Boolean[] match = new Boolean[1];
        match[0] = false;
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.map(pattern::matcher)
                    .filter(Matcher::matches)
                    .findFirst()
                    .ifPresent(matcher -> match[0] = true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return match[0];
    }


    /**
     *
     * @param file represents html source file to be indexed...<p>
     * @throws IOException <p>
     * creating index document of html source file...
     */
    private void indexFile(File file) throws IOException {
        Document document = getDocument(file);
        writer.addDocument(document);
    }


    /**
     *
     * @param filepath is path to file to be indexed...<p>
     * @param url is url of that file. url is in index field...<p>
     * creating index of file with method indexFile(f) if file exists and is readable...
     */
    public void createIndex(String filepath,String url) throws IOException {
        this.url = url;
        File f = new File(filepath);
        this.filePath = filepath;
        if (!f.isHidden() &&
            f.exists() &&
            f.canRead()) {
            indexFile(f);
        }
    }


}
