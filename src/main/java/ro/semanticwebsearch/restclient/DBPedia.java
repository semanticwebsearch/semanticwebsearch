package ro.semanticwebsearch.restclient;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Created by valentin.spac on 2/4/2015.
 */
class DBPedia implements RestClient {

    public ResultSet query(String queryString) {
        ParameterizedSparqlString qs = new ParameterizedSparqlString(queryString);

        QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", qs.asQuery());

        return exec.execSelect();
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