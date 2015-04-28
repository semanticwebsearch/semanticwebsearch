package ro.semanticwebsearch.responsegenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Weapon;
import ro.semanticwebsearch.responsegenerator.parser.DBPediaParser;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Spac on 4/26/2015.
 */
public class WeaponUsedByCountryInConflict extends AbstractQuestionType {

    private static Logger log = Logger.getLogger(WeaponUsedByCountryInConflict.class.getCanonicalName());

    //Freebase does not contain this type of information
    //TODO Update if this changes
    @Override
    public Object parseFreebaseResponse(String freebaseResponse) {
        if (log.isInfoEnabled()) {
            log.info("WeaponUsedByCountryInConflict" + " : " + freebaseResponse);
        }

        if(freebaseResponse == null || freebaseResponse.trim().isEmpty()) {
            return null;
        }

       /* try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(freebaseResponse).findValue("result");
            if (response.isArray()) {
                ArrayNode bindingsArray = (ArrayNode) response;
                JsonNode aux;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    @Override
    public Object parseDBPediaResponse(String dbpediaResponse) {
        if (log.isInfoEnabled()) {
            log.info("WeaponUsedByCountryInConflict" + " : " + dbpediaResponse);
        }

        if(dbpediaResponse == null || dbpediaResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> weaponUris = new ArrayList<>();
        ArrayList<Weapon> weapons = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(dbpediaResponse).findValue("results").findValue("bindings");
            if (response.isArray()) {
                JsonNode aux;
                //x0 -> conflict uri, x1 -> weapon, x2 -> country/place
                for(JsonNode responseItem : response) {
                    aux = responseItem.findValue("x1");

                    if(aux != null && aux.findValue("type").toString().equals("\"uri\"")) {
                        weaponUris.add(DBPediaParser.extractValue(aux.findValue("value")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String uri : weaponUris) {
            try {
                weapons.add(dbpediaWeapon(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return weapons;
    }

    public Weapon dbpediaWeapon(URI dbpediaUri) {
        if(dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String weaponInfoResponse, aux;
            JsonNode weaponInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaParser.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            weaponInfoResponse = client.request().get(String.class);
            weaponInfo = mapper.readTree(weaponInfoResponse).findValue(dbpediaUri.toString());

            Weapon weapon = new Weapon();
            aux = DBPediaParser.getName(weaponInfo);
            weapon.setName(aux);

            aux = DBPediaParser.getPrimaryTopicOf(weaponInfo);
            weapon.setWikiPageExternal(aux);

            aux = DBPediaParser.getAbstractDescription(weaponInfo);
            weapon.setDescription(aux);

            weapon.setThumbnails(DBPediaParser.getThumbnail(weaponInfo));

            weapon.setLength(DBPediaParser.getWeaponLength(weaponInfo));
            weapon.setWeight(DBPediaParser.getWeaponWeight(weaponInfo));
            weapon.setService(DBPediaParser.getService(weaponInfo));
            weapon.setDesigner(DBPediaParser.getDesigner(weaponInfo));
            weapon.setCaliber(DBPediaParser.getCaliber(weaponInfo));
            weapon.setOrigin(DBPediaParser.getOrigin(weaponInfo));

            return weapon;

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
