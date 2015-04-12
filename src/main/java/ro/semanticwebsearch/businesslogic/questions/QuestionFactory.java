package ro.semanticwebsearch.businesslogic.questions;

import com.google.common.reflect.ClassPath;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.services.ServiceFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public class QuestionFactory {
    private static QuestionFactory instance;
    private static Map<String, QuestionType> registeredQuestions;
    private static Logger log = Logger.getLogger(ServiceFactory.class.getCanonicalName());


    public static QuestionFactory getInstance() {
        if(instance == null) {
            instance = new QuestionFactory();
        }

        return instance;
    }

    private QuestionFactory() {
        registeredQuestions = new HashMap<>();

        try {
            Class<?> loaded;
            ClassPath classpath = ClassPath.from(getClass().getClassLoader());
            String packageName = this.getClass().getPackage().getName();

            for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(packageName)) {
                loaded = classInfo.load();
                if(QuestionType.class.isAssignableFrom(loaded)
                        && !Modifier.isAbstract(loaded.getModifiers())
                        && !loaded.isInterface()) {
                    registeredQuestions.put(loaded.getSimpleName().toLowerCase(), (QuestionType) loaded.newInstance());
                }
            }

        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns a new instance of RestClient og given type {@code clientType}
     * @param questionType the type of the client
     * @return a new instance of that type
     * <p>The client type must implement {@code RestClient} interface and must have no-args constructor</p>
     * @throws InstantiationException if a new instance of the {@code clientType} could not be created
     * @throws IllegalAccessException if a new instance of the {@code clientType} could not be created
     * @throws IllegalArgumentException if the {@code clientType} received as parameter is not registered
     */
    public QuestionType getInstanceFor(String questionType)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException {

        if(!registeredQuestions.containsKey(questionType)) {
            if(log.isInfoEnabled()) {
                log.info("Invalid question type: Question type [ " + questionType + " ] does not exist");
            }
            throw new IllegalArgumentException("Question type [ " + questionType + " ] does not exist");
        } else {
            if(log.isInfoEnabled()) {
                log.info("getInstanceFor: " + questionType);
            }
            return registeredQuestions.get(questionType);
        }
    }

}