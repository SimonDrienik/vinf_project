package org.example.crawler;

import java.io.File;
import java.io.FileFilter;

public class HtmlFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.getName().toLowerCase().endsWith("d");
    }
}
