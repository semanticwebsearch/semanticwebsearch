package rest.controller;

import rest.model.output.Response;
import rest.model.output.ResponseType;
import rest.model.output.content.Image;
import rest.model.output.content.Map;
import rest.model.output.content.Text;
import rest.model.output.content.Video;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Spac on 25 Ian 2015.
 */
@Path("query")
public class Search {
    static int id = 0;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Response> query(@QueryParam("q")String queryString,
                                @QueryParam("text")boolean isText,
                                @QueryParam("image")boolean isImage,
                                @QueryParam("map")boolean isMap,
                                @QueryParam("video")boolean isVideo) {

        List<Response> response = new LinkedList<>();
        for(int i = 0; i < 1; i++) {
            if(isText) {
                populateText(response);
            }

            if(isMap) {
                populateMaps(response);
            }

            if(isImage) {
                populateImage(response);
            }

            if(isVideo) {
                populateVideo(response);
            }
        }

        return response;

    }

    private void populateText(List<Response> list) {
        Response qr = new Response();
        qr.setContent(new Text("Lorem Ipsum is simply dummy text of the printing and typesetting industry. "
                                       + "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, "
                                       + "when an unknown printer took a galley of type and scrambled it to make a type specimen book. "
                                       + "It has survived not only five centuries, but also the leap into electronic typesetting, remaining "
                                       + "essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing "
                                       + "Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including "
                                       + "versions of Lorem Ipsum."));

        qr.setLink("#");
        qr.setId(id++);
        qr.setType( ResponseType.TEXT);
        qr.setItemType("article");

        list.add(qr);
    }

    private void populateImage(List<Response> list) {
        Response qr = new Response();

        Image img = new Image();
        img.setSource("http://www.brockport.edu/career/images/GraduateSchoolTests.jpg");
        img.setDescription("A short description of the image");

        qr.setContent(img);
        qr.setLink("#");
        qr.setId(id++);
        qr.setType(ResponseType.IMAGE);
        qr.setItemType(ResponseType.IMAGE.toString());

        list.add(qr);
    }

    private void populateVideo(List<Response> list) {
        Response qr = new Response();

        Video video = new Video();
        video.setSource("https://www.youtube.com/embed/co4YpHTqmfQ");
        video.setDescription("A short description of the video");

        qr.setContent(video);
        qr.setLink("#");
        qr.setId(id++);
        qr.setType(ResponseType.VIDEO);
        qr.setItemType(ResponseType.VIDEO.toString());

        list.add(qr);
    }

    private void populateMaps(List<Response> list) {
        Response qr = new Response();

        Map map = new Map();
        map.setLatitude("47.1739348");
        map.setLongitude("27.5749005");
        map.setDescription("A short description of the map");

        qr.setContent(map);
        qr.setLink("#");
        qr.setId(id++);
        qr.setType(ResponseType.MAP);
        qr.setItemType(ResponseType.MAP.toString());

        list.add(qr);
    }
}
