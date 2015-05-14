package edu.mit.mitmobile2.dining.model;

public class MITDiningVenueSnippet {
    private String name;
    private String id;

    public MITDiningVenueSnippet(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=\"" + id + "\"" +
                ", \"name\"=\"" + name + "\"" +
                '}';
    }
}
