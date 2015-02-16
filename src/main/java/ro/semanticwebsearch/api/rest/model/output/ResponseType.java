package ro.semanticwebsearch.api.rest.model.output;


import com.fasterxml.jackson.annotation.JsonValue;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Created by Spac on 25 Ian 2015.
 */
public enum ResponseType {
    @XmlEnumValue("text")
    TEXT("text"),
    @XmlEnumValue("map")
    MAP("map"),
    @XmlEnumValue("image")
    IMAGE("image"),
    @XmlEnumValue("video")
    VIDEO("video");

    private String value;

    ResponseType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
