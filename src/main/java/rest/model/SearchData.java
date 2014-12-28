package rest.model;

import javax.ws.rs.FormParam;

public class SearchData {

    // region Private Field
    @FormParam("query")
    private String query;

    @FormParam("text")
    private boolean isText;

    @FormParam("map")
    private boolean isMap;

    @FormParam("video")
    private boolean isVideo;

    @FormParam("picture")
    private boolean isPicture;
    //endregion

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean isText) {
        this.isText = isText;
    }

    public boolean isMap() {
        return isMap;
    }

    public void setMap(boolean isMap) {
        this.isMap = isMap;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public void setPicture(boolean isPicture) {
        this.isPicture = isPicture;
    }
}
