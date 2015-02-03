package rest.model.output;

import rest.model.output.content.Content;

/**
 * Created by Spac on 25 Ian 2015.
 */

public class Response {
    private int id;
    private ResponseType type;
    private Content content;
    private String link;
    private String itemType; //gen metadata

    public Response(int id, ResponseType type, Content content, String link) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.link = link;
    }

    public Response() {
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

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
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
