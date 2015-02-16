package ro.semanticwebsearch.client.rest;

import com.hp.hpl.jena.query.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by valentin.spac on 2/4/2015.
 */
class DBPedia implements RestClient {

    private static String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";

    /**
     * Queries the DBPedia endpoint using the {@code queryString} given as parameter
     * @param queryString query to be executed against Freebase endpoint
     * @return a {@code String} object representing a JSON which contains the response
     */
    @Override
    public String GET(String queryString) {
        //needed as workaround to an error throw when outputting as JSON
        ARQ.getContext().setTrue(ARQ.useSAX) ;

        ParameterizedSparqlString qs = new ParameterizedSparqlString(queryString);
        QueryExecution exec = QueryExecutionFactory.sparqlService(DBPEDIA_ENDPOINT, qs.asQuery());

        return outputAsJsonString(exec.execSelect());
    }

    /**
     * Translates the {@code resultSet} given as parameter into a {@code String} object
     * representing a JSON which contains the response
     * @param resultSet the result set to be transformed
     * @return {@code String} object representing a JSON which contains the response
     */
    private String outputAsJsonString(ResultSet resultSet) {
        OutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, resultSet);

        return outputStream.toString();
    }
}
/*
ParameterizedSparqlString qs = new ParameterizedSparqlString();
qs.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        qs.setNsPrefix("dbpedia-owl", "http://dbpedia.org/ontology/");
        qs.append("SELECT DISTINCT ?museum ?label ?thumbnail");
        qs.append("  WHERE{ \n");
        qs.append("?museum ?p ?label.");
        qs.append("?museum a ?type.");
        qs.append("?museum dbpedia-owl:thumbnail ?thumbnail.");
        qs.append("FILTER (?p=<http://www.w3.org/2000/01/rdf-schema#label>).");
        qs.append("FILTER regex(?label, \"^" + prefix + "\", \"i\").\r\n");
        qs.append("FILTER (?type IN (<http://dbpedia.org/ontology/Museum>, <http://schema.org/Museum>)).");
        qs.append("FILTER ( lang(?label) = 'en') } LIMIT " + number);

        */