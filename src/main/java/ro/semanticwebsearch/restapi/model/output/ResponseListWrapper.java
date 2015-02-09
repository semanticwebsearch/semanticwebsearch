package ro.semanticwebsearch.restapi.model.output;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.Collection;

/**
 * Created by valentin.spac on 2/9/2015.
 */
@JsonRootName(value = "responses")
public class ResponseListWrapper {
    @JacksonXmlElementWrapper(useWrapping = false)
    private Collection<Response> response;

    public ResponseListWrapper(Collection<Response> responses) {
        this.response = responses;
    }

    public Collection<Response> getResponse() {
        return response;
    }

    public void setResponse(Collection<Response> response) {
        this.response = response;
    }
}
