package edu.mit.mitmobile2.libraries;

import java.util.List;

public class BookItem {
    public String id;
    public String title;
    public String image;
    public List<String> author;
    public List<String> year;
    public List<String> publisher;
    public List<String> isbn;
    
    public List<String> subjects;
    public List<String> lang;
    public List<String> extent;
    public List<String> summary;
    public List<String> address;
    public List<Holding> holdings;
    
    
    public static class Holding {
        public String library;
        public String address;
        public String url;
    }
    
}
