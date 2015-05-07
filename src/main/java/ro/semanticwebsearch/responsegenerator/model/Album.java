package ro.semanticwebsearch.responsegenerator.model;

import java.util.ArrayList;

/**
 * Created by Spac on 5/3/2015.
 */
public class Album {
    private String name;
    private ArrayList<String> thumbnails;
    private ArrayList<StringPair> trackList;
    private String wikiPageExternalLink;
    private String description;
    private String length;
    private String releaseDate;
    private String genre;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThumbnails(ArrayList<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public ArrayList<String> getThumbnails() {
        return thumbnails;
    }

    public void setWikiPageExternalLink(String wikiPageExternalLink) {
        this.wikiPageExternalLink = wikiPageExternalLink;
    }

    public String getWikiPageExternalLink() {
        return wikiPageExternalLink;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public ArrayList<StringPair> getTrackList() {
        return trackList;
    }

    public void setTrackList(ArrayList<StringPair> trackList) {
        this.trackList = trackList;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }


}
