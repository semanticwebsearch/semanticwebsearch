package ro.semanticwebsearch;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.exception.HibernateInitializeException;
import ro.semanticwebsearch.persistence.DbManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by valentin.spac on 2/5/2015.
 */
public class ContextListener implements ServletContextListener {

    private static Logger log = Logger.getLogger(ContextListener.class.getCanonicalName());
    private Process quepyServer;
    private int noOfFails = 0;
    private static final String LOCALHOST = "127.0.0.1";

    @Override
    public void contextInitialized(ServletContextEvent event){
        initializeHibernate();
        startQuepyServer();
    }

    private void startQuepyServer() {
        if(log.isInfoEnabled()) {
            log.info("Start up quepy server. Fails: " + noOfFails);
        }

        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classLoader.getResourceAsStream("quepy.properties");

        try {
            properties.load(stream);

            StringBuilder execPath = new StringBuilder();
            execPath.append("python ").append(properties.getProperty("quepyStartServerPath"))
                    .append(" ").append(LOCALHOST).append(":").append(properties.getProperty("port"));

            quepyServer = Runtime.getRuntime()
                    .exec(execPath.toString());
            if(!quepyServer.isAlive() && noOfFails < 6) {
                startQuepyServer();
            }

        } catch (IOException e) {
            if(log.isInfoEnabled()) {
                log.info("Could not start quepy server", e);
            }
        }
    }

    private void initializeHibernate() throws HibernateInitializeException {
        DbManager.initialize();
    }

    private void destroyHibernate() {
        if(log.isInfoEnabled()) {
            log.info("Start up hibernate");
        }

        DbManager.getCurrentSession();// Just call the static initializer of that class
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        destroyHibernate();
        stopQuepyServer();
    }

    private void stopQuepyServer() {
        if(quepyServer != null && quepyServer.isAlive()) {
            quepyServer.destroy();

            if(quepyServer.isAlive()) {
                quepyServer.destroyForcibly();
            }
        }
    }
}
