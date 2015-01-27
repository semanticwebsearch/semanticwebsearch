package rest.model.output.content;

/**
 * Created by valentin.spac on 1/27/2015.
 */
public class Map implements Content {
    private String longitude;
    private String latitude;
    private String description;

    public Map() {
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
