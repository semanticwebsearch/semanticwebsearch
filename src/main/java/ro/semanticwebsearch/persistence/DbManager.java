package ro.semanticwebsearch.persistence;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import ro.semanticwebsearch.exception.HibernateInitializeException;
import ro.semanticwebsearch.exception.InvalidPersistentObjectException;
import ro.semanticwebsearch.training.Person;

import java.util.*;

/**
 * Created by valentin.spac on 2/5/2015.
 */
public class DbManager {
    public static Logger log = Logger.getLogger(DbManager.class.getCanonicalName());

    private static SessionFactory sessionFactory;
    private static final int MAX_UNFLUSHED_OBJ = 100;

    /**
     * Initializes hibernate
     * @throws HibernateInitializeException if an exception occurs
     */
    public static void initialize() throws HibernateInitializeException {
        try {
            if(log.isInfoEnabled()) {
                log.info("Initializing Hibernate sessionFactory");
            }

            Configuration configuration = new Configuration()
                    .setProperty(Environment.GENERATE_STATISTICS, "true")
                    .setInterceptor(new AuditInterceptor())
                    .configure();

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException e) {
            if(log.isInfoEnabled()) {
                log.info("Problem initializing Hibernate sessionFactory", e);
            }
            throw new HibernateInitializeException("Problem initializing Hibernate sessionFactory", e);
        }
    }

    public static void test2() {

        Random random = new Random();
        Person person;
        for(int idx = 0; idx < 2000; idx++) {
            person = new Person();
            person.setPrenume("prenume" + random.nextInt(200));
            person.setNume("nume" + idx);
            person.setFunctie("functie" + idx);
            DbManager.save(person);
        }
    }

    public static void stats() {
        System.out.println(sessionFactory.getStatistics());
    }



    /**
     * Closes current opened session factory
     */
    public static void closeSessionFactory() {
        if(sessionFactory != null) {
            sessionFactory.close();
        }
    }


    /**
     * Returns a new opened session
     * @return new opened session
     */
    public static Session getSession() {
        return sessionFactory.openSession();
    }

    /**
     * Returns the current session
     * @return current session
     */
    public static Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }


    /**
     * Executes the select query given as parameter
     * @param queryString select query to be executed
     * @param objTypeReturned class of the expected return type
     * @param <T> the expected return type
     * @return a collection of {@code T}'s
     * @throws ClassCastException if the expected type {@code T} does not match the one retrieved
     */
    public static <T> Collection<T> selectQuery(String queryString, Class<T> objTypeReturned) throws ClassCastException {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(queryString);
        List results = query.list();
        tx.commit();

        logStatistics();

        if(results.isEmpty()) {
            return Collections.emptyList();
        } else if(!objTypeReturned.isInstance(results.get(0))) {
            throw new ClassCastException("Type mismatch. Expected " + objTypeReturned.getName()+ " got " + results.get(0).getClass());
        } else {
            Collection<T> response = new LinkedList<>();
            response.addAll(results);
            return response;
        }
    }

    /**
     * Persists the @{code obj} into the database. All this happens in a transaction. After persisting, session is flushed
     * and transaction is committed
     * @param obj object to be persisted
     */
    public static void persist(Object obj) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.persist(obj);
        session.flush();
        tx.commit();

        logStatistics();
    }

    /**
     * Saves the @{code obj} into the database. All this happens in a transaction. After saving, session is flushed
     * and transaction is committed
     * @param obj object to be saved
     */
    public static void save(Object obj) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.save(obj);
        session.flush();
        tx.commit();

        logStatistics();
    }

    /**
     * Saves or updates (if already exists) the @{code obj} into the database. All this happens in a transaction.
     * After saving (or updating), session is flushed and transaction is committed
     * @param obj object to be saved (or updated)
     */
    public static void saveOrUpdate(Object obj) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(obj);
        session.flush();
        tx.commit();

        logStatistics();
    }

    /**
     * Updates the @{code obj} into the database. All this happens in a transaction.
     * After updating the @{code obj}, session is flushed and transaction is committed
     * @param obj object to be updated
     */
    public static void update(Object obj) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.update(obj);
        session.flush();
        tx.commit();

        logStatistics();
    }

    /**
     * Deletes all entries from the table mapped to the object of class {@code tableName}
     * @param tableName the class of the table-mapped object which is wanted to be deleted
     */
    public static void deleteAllFrom(Class<?> tableName) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        Query q = session.createQuery("DELETE FROM " + tableName.getSimpleName());
        q.executeUpdate();
        session.flush();
        tx.commit();

        logStatistics();

    }

    /**
     * Checks if the class is mapped in the hibernate configuration file
     * @param className the class to be checked
     * @return true, if the class is mapped, false otherwise
     */
    private static boolean isClassMapped(Class<?> className) {
        return sessionFactory.getClassMetadata(className) != null;
    }


    /**
     * Saves a collection of objects to database
     * @param collection the collection to be saved
     * @throws InvalidPersistentObjectException if the class of the objects found in collection is not mapped.
     * In this case, the database is rollbacked.
     */
    public static void saveAll(Collection<Object> collection) throws InvalidPersistentObjectException {

        if (collection != null && !collection.isEmpty()) {
            Session session = getCurrentSession();
            Transaction tx = session.beginTransaction();

            int noOfSavedObjects = 0;
            for (Object obj : collection) {
                if(!isClassMapped(obj.getClass())) {
                    tx.rollback();
                    throw new InvalidPersistentObjectException("Class " + obj.getClass().getCanonicalName() + " is not mapped!");
                } else {
                    session.save(obj);
                    noOfSavedObjects++;

                    if (MAX_UNFLUSHED_OBJ < noOfSavedObjects) {
                        session.flush();
                    }
                }
            }

            session.flush();
            tx.commit();

            logStatistics();
        }

    }

    /**
     * Persists a collection of objects to database
     * @param collection the collection to be persisted
     * @throws InvalidPersistentObjectException if the class of the objects found in collection is not mapped.
     * In this case, the database is rollbacked.
     */
    public static void persistAll(Collection<Object> collection) throws InvalidPersistentObjectException {

        if (collection != null && !collection.isEmpty()) {
            Session session = getCurrentSession();
            Transaction tx = session.beginTransaction();

            int noOfSavedObjects = 0;
            for (Object obj : collection) {
                if(!isClassMapped(obj.getClass())) {
                    tx.rollback();
                    throw new InvalidPersistentObjectException("Class " + obj.getClass().getCanonicalName() + " is not mapped!");
                } else {
                    session.persist(obj);
                    noOfSavedObjects++;

                    if (MAX_UNFLUSHED_OBJ < noOfSavedObjects) {
                        session.flush();
                    }
                }
            }

            session.flush();
            tx.commit();

            logStatistics();
        }

    }

    /**
     * Updates a collection of objects to database
     * @param collection the collection to be updated
     * @throws InvalidPersistentObjectException if the class of the objects found in collection is not mapped.
     * In this case, the database is rollbacked.
     */
    public static void updateAll(Collection<Object> collection) throws InvalidPersistentObjectException {

        if (collection != null && !collection.isEmpty()) {
            Session session = getCurrentSession();
            Transaction tx = session.beginTransaction();

            int noOfSavedObjects = 0;
            for (Object obj : collection) {
                if(!isClassMapped(obj.getClass())) {
                    tx.rollback();
                    throw new InvalidPersistentObjectException("Class " + obj.getClass().getCanonicalName() + " is not mapped!");
                } else {
                    session.update(obj);
                    noOfSavedObjects++;

                    if (MAX_UNFLUSHED_OBJ < noOfSavedObjects) {
                        session.flush();
                    }
                }
            }

            session.flush();
            tx.commit();

            logStatistics();
        }

    }

    private static void logStatistics() {
        if(log.isInfoEnabled()) {
            log.info("Hibernate statistics :" + sessionFactory.getStatistics());
        }
    }
}
