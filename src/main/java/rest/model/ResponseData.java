package rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Spac on 08 Ian 2015.
 */
@XmlRootElement
public class ResponseData {
    // region Private Field

    private int id;
    private String type;
    private String content;//obj content type
    private String metadata; //obj metadata type
    //endregion

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
