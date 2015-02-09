package dbmanager;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ro.semanticwebsearch.dbmanager.DbManager;

import java.util.Collection;

@RunWith(JUnit4.class)
public class DbManagerTest extends TestCase {

    private static int nrOfRecords = 10;

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

    @Test
    public void testUpdateObject() {
        String newName = "modified";
        Collection<PersonTest> list = DbManager.selectQuery("from PersonTest where nume = 'nume1'", PersonTest.class);
        PersonTest modified = list.iterator().next();
        modified.setNume(newName);
        DbManager.update(modified);

        list = DbManager.selectQuery("from PersonTest where nume = 'modified'", PersonTest.class);
        PersonTest isModified = list.iterator().next();

        assertTrue(isModified.getNume().equals(newName));
    }


}