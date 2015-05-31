package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Weapon;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Spac on 4/26/2015.
 */
class WeaponParser implements ParserType {

    private static Logger log = Logger.getLogger(WeaponParser.class.getCanonicalName());

    //Freebase does not contain this type of information
    //TODO Update if this changes
    @Override
    public Object parseFreebaseResponse(String freebaseResponse, String questionId) {
        if (log.isInfoEnabled()) {
            log.info("WeaponUsedByCountryInConflict" + " : " + freebaseResponse);
        }

        if (freebaseResponse == null || freebaseResponse.trim().isEmpty()) {
            return null;
        }

       /* try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(freebaseResponse).get("result");
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
    public Object parseDBPediaResponse(String dbpediaResponse, String questionId) {
        if (log.isInfoEnabled()) {
            log.info("WeaponUsedByCountryInConflict" + " : " + dbpediaResponse);
        }

        if (dbpediaResponse == null || dbpediaResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> weaponUris = new ArrayList<>();
        ArrayList<Weapon> weapons = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(dbpediaResponse).get("results").get("bindings");
            if (response.isArray()) {
                JsonNode aux;
                //x0 -> conflict uri, x1 -> weapon, x2 -> country/place
                for (JsonNode responseItem : response) {
                    aux = responseItem.get("x1");

                    if (aux != null && aux.get("type").toString().equals("\"uri\"")) {
                        weaponUris.add(DBPediaPropertyExtractor.extractValue(aux.get("value")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String uri : weaponUris) {
            try {
                weapons.add(dbpediaWeapon(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return weapons;
    }

    public Weapon dbpediaWeapon(URI dbpediaUri) {
        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String weaponInfoResponse, aux;
            JsonNode weaponInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            weaponInfoResponse = client.request().get(String.class);
            weaponInfo = mapper.readTree(weaponInfoResponse).get(dbpediaUri.toString());

            Weapon weapon = new Weapon();
            aux = DBPediaPropertyExtractor.getName(weaponInfo);
            weapon.setName(aux);

            aux = DBPediaPropertyExtractor.getPrimaryTopicOf(weaponInfo);
            weapon.setWikiPageExternal(aux);

            aux = DBPediaPropertyExtractor.getAbstractDescription(weaponInfo);
            weapon.setDescription(aux);

            weapon.setThumbnails(DBPediaPropertyExtractor.getThumbnail(weaponInfo));

            weapon.setLength(DBPediaPropertyExtractor.getWeaponLength(weaponInfo));
            weapon.setWeight(DBPediaPropertyExtractor.getWeaponWeight(weaponInfo));
            weapon.setService(DBPediaPropertyExtractor.getService(weaponInfo));
            weapon.setDesigner(DBPediaPropertyExtractor.getDesigner(weaponInfo));
            weapon.setCaliber(DBPediaPropertyExtractor.getCaliber(weaponInfo));
            weapon.setOrigin(DBPediaPropertyExtractor.getOrigin(weaponInfo));

            return weapon;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
