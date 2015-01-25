package rest.model.output;

/**
 * Created by Spac on 25 Ian 2015.
 */
public enum ResponseType {
    TEXT("text"),
    MAP("map"),
    PICTURE("picture"),
    VIDEO("video");

    private String value;

    ResponseType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
