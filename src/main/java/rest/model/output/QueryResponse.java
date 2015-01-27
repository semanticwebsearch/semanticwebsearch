package rest.model.output;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Spac on 25 Ian 2015.
 */
@XmlRootElement
public class QueryResponse {
    private int id;
    private ResponseType type;
    private String content;
    private String link;
    private String itemType; //gen metadata

    public QueryResponse(int id, ResponseType type, String content, String link) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.link = link;
    }

    public QueryResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
