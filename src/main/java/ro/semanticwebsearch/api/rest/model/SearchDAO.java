package ro.semanticwebsearch.api.rest.model;

import javax.ws.rs.QueryParam;

public class SearchDAO {

    // region Private Field
    @QueryParam("q")
    private String query;

    //endregion

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SearchData: \n\tQuery string: ").append(query);

        return sb.toString();
    }
}
