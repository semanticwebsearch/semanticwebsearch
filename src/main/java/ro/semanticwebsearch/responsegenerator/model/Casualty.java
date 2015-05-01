package ro.semanticwebsearch.responsegenerator.model;

/**
 * Created by Spac on 5/1/2015.
 */
public class Casualty {
    private StringPair combatant;
    private String casualties;
    private String casualtyType;

    public StringPair getCombatant() {
        return combatant;
    }

    public void setCombatant(StringPair combatant) {
        this.combatant = combatant;
    }

    public String getCasualties() {
        return casualties;
    }

    public void setCasualties(String casualties) {
        this.casualties = casualties;
    }

    public String getCasualtyType() {
        return casualtyType;
    }

    public void setCasualtyType(String casualtyType) {
        this.casualtyType = casualtyType;
    }
}
