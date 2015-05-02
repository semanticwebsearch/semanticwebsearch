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
     * Initializes the dbpedia endpoint read from properties file and
     * sets the client target to that endpoint
     */
    static {
        try {
            DBPEDIA_ENDPOINT = PropertiesLoader.getInstance().getProperties().getProperty("dbpedia_endpoint");
            //needed as workaround to an error throw when outputting as JSON
            ARQ.getContext().setTrue(ARQ.useSAX);
        } catch (IOException e) {
            throw new RuntimeException("Error reading services.properties file", e);
        }

        if (DBPEDIA_ENDPOINT == null) {
            throw new InvalidConfigurationFileException("[dbpedia_endpoint] property was not set.");
        }
    }

    /**
     * Queries the DBPedia endpoint using the {@code queryString} given as parameter
     *
     * @return a {@code String} object representing a JSON which contains the response
     */
    @Override
    public String query(String queryString) throws UnsupportedEncodingException, URISyntaxException {
/*        Quepy quepy = new Quepy(QueryType.SPARQL, queryString);
        Test transformedQuery = quepy.query();*/

        ParameterizedSparqlString qs = new ParameterizedSparqlString(queryString);
        /*Model dataset = TBDManager.getModel(queryString);
        if(dataset != null) {
            try (QueryExecution qe = QueryExecutionFactory.create(String.valueOf(qs), dataset)) {
                ResultSet rs = qe.execSelect();
                ResultSetFormatter.out(rs);
            }
        }*/

        QueryExecution exec = QueryExecutionFactory.sparqlService(DBPEDIA_ENDPOINT, qs.asQuery());
        ResultSet resultSet = exec.execSelect();

        return outputAsJsonString(resultSet);

    }

    /**
     * Translates the {@code resultSet} given as parameter into a {@code String} object
     * representing a JSON which contains the response
     *
     * @param resultSet the result set to be transformed
     * @return {@code String} object representing a JSON which contains the response
     */
    private String outputAsJsonString(ResultSet resultSet) {
        OutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, resultSet);

        return outputStream.toString();
    }
}