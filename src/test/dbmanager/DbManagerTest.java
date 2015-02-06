package dbmanager;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ro.semanticwebsearch.dbmanager.DbManager;

import java.util.Collection;

@RunWith(JUnit4.class)
public class DbManagerTest extends TestCase {

    private Session session;
    private static int nrOfRecords = 3;

    @Before
    public void setUp() throws Exception {
        DbManager.deleteAllFrom(PersonTest.class);

        PersonTest person;
        for(int idx = 0; idx < nrOfRecords; idx++) {
            person = new PersonTest();
            person.setPrenume("prenume" + idx);
            person.setNume("nume" + idx);
            person.setFunctie("functie" + idx);
            DbManager.save(person);
        }
    }

    @Test
    public void testDeleteAll() {
        DbManager.deleteAllFrom(PersonTest.class);
        Collection<PersonTest> list = DbManager.selectQuery("from PersonTest", PersonTest.class);
        assertTrue(0 == list.size());
    }

    @Test
    public void testSelectAllQuerySuccess() throws Exception {
        Collection<PersonTest> list = DbManager.selectQuery("from PersonTest", PersonTest.class);
        assertTrue(nrOfRecords == list.size());
    }

    @Test
    public void testSelectPartialQuerySuccess() throws Exception {
        Collection<PersonTest> list = DbManager.selectQuery("from PersonTest where nume = 'nume1'", PersonTest.class);
        assertTrue(1 == list.size());
    }

}