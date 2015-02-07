package restclientfactory;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ro.semanticwebsearch.restclient.RestClient;
import ro.semanticwebsearch.restclient.RestClientFactory;
import ro.semanticwebsearch.restclient.exception.IllegalClassConstructorException;

@RunWith(JUnit4.class)
public class RestClientFactoryTest extends TestCase {

    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceForBadClient() throws Exception {
        RestClientFactory.getInstanceFor("dada");
    }

    @Test
    public void getInstanceForGoodClient() throws Exception{
        RestClientFactory.getInstanceFor("Freebase");
        RestClientFactory.getInstanceFor("DBPedia");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterClientNotImplementInterface() throws Exception {
        RestClientFactory.registerClient("test", Object.class);
    }

    @Test(expected = IllegalClassConstructorException.class)
    public void testRegisterBadClientNoNOARGSConstructors() throws Exception {
        RestClientFactory.registerClient("test2", BadCl.class);
    }

    @Test
    public void testRegisterClient() throws Exception {
        RestClientFactory.registerClient("test", GoodCl.class);
    }

    @Test
    public void testRegisterClient2Constructors() throws Exception {
        RestClientFactory.registerClient("test", GoodCl2.class);
    }

    @Test
    public void testClientExists() throws Exception {
        assertTrue(RestClientFactory.clientExists("Freebase"));
    }

    @Test
    public void testClientNotExists() throws Exception {
        assertFalse(RestClientFactory.clientExists("Fre"));
    }

    public class BadCl implements RestClient {

        public BadCl(String g) {
            System.out.println("dada");
        }

        @Override
        public String query(String queryString) {
            return null;
        }
    }

}