package ro.semanticwebsearch.restapi.endpoint;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.restapi.model.SearchData;
import ro.semanticwebsearch.restapi.model.output.Response;
import ro.semanticwebsearch.restapi.model.output.ResponseType;
import ro.semanticwebsearch.restapi.model.output.content.Image;
import ro.semanticwebsearch.restapi.model.output.content.Map;
import ro.semanticwebsearch.restapi.model.output.content.Text;
import ro.semanticwebsearch.restapi.model.output.content.Video;

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

    public static Logger log = Logger.getLogger(Search.class.getCanonicalName());

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Response> query(@BeanParam SearchData searchData) {
        if(log.isInfoEnabled()) {
            log.info(searchData.toString());
        }

        List<Response> response = new LinkedList<>();
        for(int i = 0; i < 1; i++) {
            if(searchData.isText()) {
                populateText(response);
            }

            if(searchData.isMap()) {
                populateMaps(response);
            }

            if(searchData.isImage()) {
                populateImage(response);
            }

            if(searchData.isVideo()) {
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
        qr.setItemType("http://schema.org/TechArticle");

        list.add(qr);
    }

    private void populateImage(List<Response> list) {
        Response qr = new Response();

        Image img = new Image();
        img.setSource("http://www.brockport.edu/career/images/GraduateSchoolTests.jpg");
        img.setDescription("A short description of the image");

        qr.setContent(img);
        qr.setItemType("http://schema.org/ImageObject");
        qr.setLink("#");
        qr.setId(id++);
        qr.setType(ResponseType.IMAGE);
        qr.setItemType(ResponseType.IMAGE.toString());

        list.add(qr);
    }

    private void populateVideo(List<Response> list) {
        Response qr = new Response();

        Video video = new Video();
        video.setSource("https://www.youtube.com/embed/pVJv32qj7xA");
        video.setDescription("A short description of the video");

        qr.setContent(video);
        qr.setItemType("http://schema.org/VideoObject");
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
        qr.setItemType("http://schema.org/Place");
        qr.setId(id++);
        qr.setType(ResponseType.MAP);
        qr.setItemType(ResponseType.MAP.toString());

        list.add(qr);
    }
}
