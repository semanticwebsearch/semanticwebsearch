package rest.model;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

public class SearchData {

    // region Private Field
    @QueryParam("q")
    private String query;

    @QueryParam("text")
    private boolean isText;

    @QueryParam("map")
    private boolean isMap;

    @QueryParam("video")
    private boolean isVideo;

    @QueryParam("image")
    private boolean isImage;
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

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean isPicture) {
        this.isImage = isPicture;
    }
}
