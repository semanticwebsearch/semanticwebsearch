package rest.restclient;

import org.apache.log4j.Logger;
import rest.restclient.exception.IllegalClassConstructorException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public class RestClientFactory {

    private static Map<String, Class> registeredClients;

    private static Logger log = Logger.getLogger(RestClientFactory.class.getCanonicalName());

    static {
        registeredClients = new HashMap<>();
        registeredClients.put("Freebase", Freebase.class);
        registeredClients.put("DBPedia", DBPedia.class);
    }

    public static RestClient getInstanceFor(String clientType) throws InstantiationException, IllegalAccessException {

        if(registeredClients.containsKey(clientType)) {
            if(log.isInfoEnabled()) {
                log.info("getInstanceFor: " + clientType);
            }

            return newRestClientInstance(clientType);
        } else {
            if(log.isInfoEnabled()) {
                log.info("Invalid client type: Client type [ " + clientType + " ] does not exist");
            }
            throw new IllegalArgumentException("Client type [ " + clientType + " ] does not exist");
        }
    }

    private static RestClient newRestClientInstance(String clientType)
            throws IllegalAccessException, InstantiationException {

        Class client = registeredClients.get(clientType);
        return (RestClient)client.newInstance();
    }

    /**
     * Register a new RestClient to the factory. The added class MUST implement RestClient interface and
     * MUST have a no-args constructor
     * @param tag the tag used for retrieving a new instance of {@code client}
     * @param client class object of the newly added class
     */
    public static void registerClient(String tag, Class client)
            throws IllegalArgumentException, IllegalClassConstructorException {

        if(!RestClient.class.isAssignableFrom(client)) {
            throw new IllegalArgumentException("The added class must implement " + RestClient.class.getCanonicalName() + " interface!");
        } else if(hasNoArgsConstructor(client)) {
            throw new IllegalClassConstructorException("Class " + client.getCanonicalName() + " must have a no-args constructor!");
        } else {
            registeredClients.put(tag, client);

            if(log.isInfoEnabled()) {
                log.info("Registered new client: \n\ttag: " + tag + "\n\tclass: " + client.getCanonicalName());
            }
        }
    }

    /**
     * Checks if {@code clazz} has a parameter-less constructor
     * @param clazz class to  be checked
     * @return true, if a  parameter-less constructor is found, false otherwise
     */
    private static boolean hasNoArgsConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }

    /**
     * Check if {@code clientType} is already registered
     * @param clientType tag to be checked
     * @return true, if is already registered, false otherwise
     */
    public static boolean clientExists(String clientType) {
        return registeredClients.containsKey(clientType);
    }


}
