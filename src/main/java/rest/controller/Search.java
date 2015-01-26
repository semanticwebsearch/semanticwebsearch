package rest.controller;

import rest.model.output.QueryResponse;
import rest.model.output.ResponseType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Spac on 25 Ian 2015.
 */
@Path("query")
public class Search {
    int id = 0;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<QueryResponse> query(@QueryParam("q")String queryString) {
        System.out.println(queryString);
        List<QueryResponse> response = new LinkedList<>();
        for(int i = 0; i < 10; i++) {
            populate(response);
        }

        return response;

    }

    private void populate(List<QueryResponse> list) {
        QueryResponse qr = new QueryResponse();
        qr.setContent("222Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut. \"\n" +
                "          + \"Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut.\"\n" +
                "          + \"Neque, vitae, fugiat, libero corrupti Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut. \"\n" +
                "          + \"Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut.\"\n" +
                "          + \"Neque, vitae, fugiat, libero corrupti officiis sint facilis tempora quidem repudiandae praesentium odit similique adipisci aut.");

        qr.setLink("no link");
        qr.setId(id++);

        Random r = new Random();
        int n = r.nextInt(4);
        ResponseType responseType;
        if(n == 1) {
            responseType = ResponseType.PICTURE;
        } else if(n == 2) {
            responseType = ResponseType.MAP;
        } else if(n == 3) {
            responseType = ResponseType.VIDEO;
        } else {
            responseType = ResponseType.TEXT;
        }

        qr.setType(responseType);

        list.add(qr);
    }
}
