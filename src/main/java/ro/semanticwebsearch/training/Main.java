package ro.semanticwebsearch.training;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.semanticwebsearch.services.Service;
import ro.semanticwebsearch.services.ServiceFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by Spac on 07 Feb 2015.
 */
public class Main {
    public static void main(String[] args)
            throws IllegalAccessException, InstantiationException {
        Service freebase = ServiceFactory.getInstanceFor("dbpedia");
        String set = null;
        try {
            set = freebase.query("List movies with Hugh Laurie");
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(set));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

       /* Service quepy = ServiceFactory.getInstanceFor("quepy");
        try {
            String response = quepy.query("type=sparql&q=what is a blowtorch?");
            System.out.println(response);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }*/
        /*String set2 = null;
        Service db = ServiceFactory.getInstanceFor("freebase");
        try {
            set2 = db.query("What is the plot of Titanic?");
        } catch (java.io.UnsupportedEncodingException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper2 = new ObjectMapper();
        try {
            System.out.println(mapper2.writeValueAsString(set2));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

    }
}
