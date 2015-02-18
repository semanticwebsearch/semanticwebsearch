package ro.semanticwebsearch.services;

import com.hp.hpl.jena.query.*;
import ro.semanticwebsearch.services.exception.InvalidConfigurationFileException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by valentin.spac on 2/4/2015.
 */
class DBPedia implements Service {

    private static String DBPEDIA_ENDPOINT;

    /**
     * Queries the DBPedia endpoint using the {@code queryString} given as parameter
     * @return a {@code String} object representing a JSON which contains the response
     */
    @Override
    public String query(String queryString) throws UnsupportedEncodingException, URISyntaxException {
        if(DBPEDIA_ENDPOINT == null) {
            initialize();
        }

        Quepy quepy = new Quepy("sparql", queryString);
        String transformedQuery = quepy.query();

        ParameterizedSparqlString qs = new ParameterizedSparqlString(transformedQuery);
        QueryExecution exec = QueryExecutionFactory.sparqlService(DBPEDIA_ENDPOINT, qs.asQuery());

        return outputAsJsonString(exec.execSelect());
    }

    /**
     * Initializes the dbpedia endpoint read from properties file and
     * sets the client target to that endpoint
     */
    private void initialize() {
        try {
            DBPEDIA_ENDPOINT = PropertiesLoader.getInstance().getProperties().getProperty("dbpedia_endpoint");
            //needed as workaround to an error throw when outputting as JSON
            ARQ.getContext().setTrue(ARQ.useSAX);
        } catch (IOException e) {
            throw new RuntimeException("Error reading services.properties file", e);
        }

        if(DBPEDIA_ENDPOINT == null) {
            throw new InvalidConfigurationFileException("[dbpedia_endpoint] property was not set.");
        }
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