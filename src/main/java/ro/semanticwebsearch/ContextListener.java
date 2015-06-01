package ro.semanticwebsearch;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.utils.Config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by valentin.spac on 2/5/2015.
 */
public class ContextListener implements ServletContextListener {

    private static final String LOCALHOST = "127.0.0.1";
    private static Logger log = Logger.getLogger(ContextListener.class.getCanonicalName());
    private Process quepyServer;
    private Process mongoDB;
    private int noOfFails = 0;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //startQuepyServer();
        startMongoDB();
    }

    private void startMongoDB() {
        String mongodbPath = Config.getProperty("mongodb_path");
        String mongodbDatabasePath = Config.getProperty("mongodb_dbpath");

        if(mongodbPath == null || mongodbDatabasePath == null) {
            throw new RuntimeException("MongoDB path or mongo database path was not found in services.properties");
        } else {
            StringBuilder execPath = new StringBuilder();
            execPath.append(mongodbPath).append("/").append("mongod").append(" --dbpath ").append(mongodbDatabasePath);

            try {
                mongoDB = Runtime.getRuntime()
                        .exec(execPath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        stopQuepyServer();
        stopMongoDB();
    }

    private void stopMongoDB() {
        if (mongoDB != null && mongoDB.isAlive()) {
            mongoDB.destroy();

            if (mongoDB.isAlive()) {
                mongoDB.destroyForcibly();
            }
        }
    }

    private void startQuepyServer() {
        if (log.isInfoEnabled()) {
            log.info("Start up quepy server. Fails: " + noOfFails);
        }

        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //InputStream stream = classLoader.getResourceAsStream("quepy_old.properties");
        InputStream stream2 = classLoader.getResourceAsStream("services.properties");

        try {
            properties.load(stream2);

            StringBuilder execPath = new StringBuilder();
           /* execPath.append("python ").append(properties.getProperty("quepyStartServerPath"))
                    .append(" ").append(LOCALHOST).append(":").append(properties.getProperty("port"));*/
            String quepyEndpoint = properties.getProperty("quepy_endpoint");

            //removing the http:// from endpoint
            if (quepyEndpoint.contains("//")) {
                int start = quepyEndpoint.indexOf("//");
                quepyEndpoint = quepyEndpoint.substring(start + 2);
            }

            execPath.append("python ").append(properties.getProperty("quepyStartServerPath"))
                    .append(" ").append(quepyEndpoint);

            quepyServer = Runtime.getRuntime()
                    .exec(execPath.toString());
            if (!quepyServer.isAlive() && noOfFails < 6) {
                startQuepyServer();
            }

        } catch (IOException e) {
            if (log.isInfoEnabled()) {
                log.info("Could not start quepy server", e);
            }
        }
    }

    private void stopQuepyServer() {
        if (quepyServer != null && quepyServer.isAlive()) {
            quepyServer.destroy();

            if (quepyServer.isAlive()) {
                quepyServer.destroyForcibly();
            }
        }
    }
}
