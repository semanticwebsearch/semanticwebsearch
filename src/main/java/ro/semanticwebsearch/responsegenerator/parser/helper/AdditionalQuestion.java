package ro.semanticwebsearch.responsegenerator.parser.helper;

/**
 * Created by Spac on 4/22/2015.
 */
public enum AdditionalQuestion {
    WHO_IS("Who is %s"),
    LOCATION_INFO("location %s"),
    EDUCATION_INFO("education institution %s"),
    CONFLICT("military conflict %s");

    private String value;

    AdditionalQuestion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "AdditionalQuestion{" +
                "value='" + value + '\'' +
                '}';
    }
}
