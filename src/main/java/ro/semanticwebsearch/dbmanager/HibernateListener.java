package ro.semanticwebsearch.dbmanager;

import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by valentin.spac on 2/5/2015.
 */
public class HibernateListener implements ServletContextListener {

    private static Logger log = Logger.getLogger(HibernateListener.class.getCanonicalName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if(log.isInfoEnabled()) {
            log.info("Start up hibernate");
        }

        DbManager.getSession();// Just call the static initializer of that class
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if(log.isInfoEnabled()) {
            log.info("Shutdown hibernate");
        }

        DbManager.closeSessionFactory(); // Free all resources
    }
}
