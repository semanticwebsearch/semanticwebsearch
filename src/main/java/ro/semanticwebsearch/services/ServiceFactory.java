package ro.semanticwebsearch.services;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.services.exception.IllegalClassConstructorException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public class ServiceFactory {

    private static Map<String, Class> registeredServices;
    private static Logger log = Logger.getLogger(ServiceFactory.class.getCanonicalName());

    /**
     * Initialises the standard rest clients, Freebase, DBPedia and Generic clients
     */
    static {
        registeredServices = new HashMap<>();
        registeredServices.put("freebase", Freebase.class);
        registeredServices.put("dbpedia", DBPedia.class);
    }

    /**
     * Returns a new instance of RestClient og given type {@code clientType}
     *
     * @param clientType the type of the client
     * @return a new instance of that type
     * <p>The client type must implement {@code RestClient} interface and must have no-args constructor</p>
     * @throws InstantiationException   if a new instance of the {@code clientType} could not be created
     * @throws IllegalAccessException   if a new instance of the {@code clientType} could not be created
     * @throws IllegalArgumentException if the {@code clientType} received as parameter is not registered
     */
    public static Service getInstanceFor(String clientType)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException {
        Service serviceInstance;

        if (!registeredServices.containsKey(clientType)) {
            if (log.isInfoEnabled()) {
                log.info("Invalid client type: Client type [ " + clientType + " ] does not exist");
            }
            throw new IllegalArgumentException("Client type [ " + clientType + " ] does not exist");
        } else {
            if (log.isInfoEnabled()) {
                log.info("getInstanceFor: " + clientType);
            }
            serviceInstance = newServiceInstance(clientType);
        }

        return serviceInstance;
    }


    /**
     * Uses reflection to create a new instance of {@code clientType}
     *
     * @param clientType the type of the object needed to be created
     * @return a new instance of {@code clientType}
     * @throws IllegalAccessException if a new instance of the {@code clientType} could not be created
     * @throws InstantiationException if a new instance of the {@code clientType} could not be created
     */
    private static Service newServiceInstance(String clientType)
            throws IllegalAccessException, InstantiationException {

        Class client = registeredServices.get(clientType);
        return (Service) client.newInstance();
    }


    /**
     * Register a new RestClient to the factory. The added class MUST implement RestClient interface and
     * MUST have a no-args constructor
     *
     * @param tag    the tag used for retrieving a new instance of {@code client}
     * @param client class object of the newly added class
     * @throws IllegalArgumentException         if the class being registered does not implements {@code RestClient} interface
     * @throws IllegalClassConstructorException if the class being registered does not have a no-args constructor
     */
    public static void registerClient(String tag, Class client)
            throws IllegalArgumentException, IllegalClassConstructorException {

        if (!Service.class.isAssignableFrom(client)) {
            throw new IllegalArgumentException("The added class must implement " + Service.class.getCanonicalName() + " interface!");
        } else if (!hasNoArgsConstructor(client)) {
            throw new IllegalClassConstructorException("Class " + client.getCanonicalName() + " must have a no-args constructor!");
        } else {
            registeredServices.put(tag, client);

            if (log.isInfoEnabled()) {
                log.info("Registered new client: \n\ttag: " + tag + "\n\tclass: " + client.getCanonicalName());
            }
        }
    }

    /**
     * Checks if {@code clazz} has a parameter-less constructor
     *
     * @param clazz class to  be checked
     * @return true, if a parameter-less constructor is found, false otherwise
     */
    private static boolean hasNoArgsConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }

    /**
     * Check if {@code clientType} is already registered
     *
     * @param clientType tag to be checked
     * @return true, if is already registered, false otherwise
     */
    public static boolean clientExists(String clientType) {
        return registeredServices.containsKey(clientType);
    }


}
