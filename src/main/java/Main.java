import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import rest.restclient.RestClient;
import rest.restclient.RestClientFactory;

import javax.annotation.Resource;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public class Main {
    public static void main(String[] args) {
       /* ParameterizedSparqlString qs = new ParameterizedSparqlString( "" +
                "prefix : <http://rdf.basekb.com/ns/>\n" +
                "\n" +
                "select ?river ?length {\n" +
                "   ?river :geography.river.length ?length .\n" +
                "   ?river :location.location.containedby :m.06bnz .\n" +
                "} ORDER BY DESC(?length) LIMIT 1" );

     *//*   Literal london = ResourceFactory.createPlainLiteral("1900-01-01");
        qs.setParam( "mydate", london );

        Literal londo2n = ResourceFactory.createTypedLiteral("Berlin");
        qs.setParam( "mata", londo2n );

*//*
        System.out.println( qs );

        QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", qs.asQuery());

        // Normally you'd just do results = exec.execSelect(), but I want to
        // use this ResultSet twice, so I'm making a copy of it.
        ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

       *//* while ( results.hasNext() ) {
            // As RobV pointed out, don't use the `?` in the variable
            // name here. Use *just* the name of the variable.
            System.out.println( results.next().get( "resource" ));
        }*//*

        // A simpler way of printing the results.
        ResultSetFormatter.out(results);*/

        RestClient s = null;
        try {
            s = RestClientFactory.getInstanceFor("Freebase");
            RestClient s2 = RestClientFactory.getInstanceFor("Freebase");
            System.out.println(s == s2);
            RestClient s3 = RestClientFactory.getInstanceFor("Freebase");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
