package rest.example.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TodoModel {

    private String summary;
    private String description;

    public TodoModel() {
    }

    public String getSummary() {
        return summary;
    }


    public void setSummary(String summary) {
        this.summary = summary;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
