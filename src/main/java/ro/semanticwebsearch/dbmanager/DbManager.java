package ro.semanticwebsearch.dbmanager;

import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valentin.spac on 2/5/2015.
 */
public class DbManager {
    private static SessionFactory sessionFactory;

    /**
     * Static block used to initialize hibernate session factory
     */
    static {
        try {
            Configuration configuration = new Configuration()
                    .setInterceptor(new AuditInterceptor())
                    .configure();

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException e) {
            e.printStackTrace();
            System.out.println("Problem creating session factory");
        }
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
        Query query = getSession().createQuery(queryString);
        List results = query.list();

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
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        session.persist(obj);
        session.flush();
        tx.commit();
    }

    /**
     * Saves the @{code obj} into the database. All this happens in a transaction. After saving, session is flushed
     * and transaction is committed
     * @param obj object to be saved
     */
    public static void save(Object obj) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        session.save(obj);
        session.flush();
        tx.commit();
    }

    /**
     * Saves or updates (if already exists) the @{code obj} into the database. All this happens in a transaction.
     * After saving (or updating), session is flushed and transaction is committed
     * @param obj object to be saved (or updated)
     */
    public static void saveOrUpdate(Object obj) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(obj);
        session.flush();
        tx.commit();
    }

    /**
     * Updates the @{code obj} into the database. All this happens in a transaction.
     * After updating the @{code obj}, session is flushed and transaction is committed
     * @param obj object to be updated
     */
    public static void update(Object obj) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        session.update(obj);
        session.flush();
        tx.commit();
    }

    public static void deleteAllFrom(Class<?> tableName) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        Query q = session.createQuery("DELETE FROM " + tableName.getSimpleName());
        q.executeUpdate();
        session.flush();
        tx.commit();

    }

    private static boolean isClassMapped(Class<?> tableName) {
       return sessionFactory.getClassMetadata(tableName) != null;
    }

    //saveAll, persistAll, updateAll

}
