package restclientfactory;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ro.semanticwebsearch.services.Service;
import ro.semanticwebsearch.services.ServiceFactory;
import ro.semanticwebsearch.services.exception.IllegalClassConstructorException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@RunWith(JUnit4.class)
public class ServiceFactoryTest extends TestCase {

    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceForBadClient() throws Exception {
        ServiceFactory.getInstanceFor("dada");
    }

    @Test
    public void getInstanceForGoodClient() throws Exception{
        ServiceFactory.getInstanceFor("freebase");
        ServiceFactory.getInstanceFor("dbpedia");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterClientNotImplementInterface() throws Exception {
        ServiceFactory.registerClient("test", Object.class);
    }

    @Test(expected = IllegalClassConstructorException.class)
    public void testRegisterBadClientNoNOARGSConstructors() throws Exception {
        ServiceFactory.registerClient("test2", BadCl.class);
    }

    @Test
    public void testRegisterClient() throws Exception {
        ServiceFactory.registerClient("test", GoodCl.class);
    }

    @Test
    public void testRegisterClient2Constructors() throws Exception {
        ServiceFactory.registerClient("test", GoodCl2.class);
    }

    @Test
    public void testClientExists() throws Exception {
        assertTrue(ServiceFactory.clientExists("freebase"));
    }

    @Test
    public void testClientNotExists() throws Exception {
        assertFalse(ServiceFactory.clientExists("fre"));
    }

    public class BadCl implements Service {

        public BadCl(String g) {
            System.out.println("dada");
        }

        @Override
        public String query(String queryString) throws UnsupportedEncodingException, URISyntaxException {
            return null;
        }
    }

}