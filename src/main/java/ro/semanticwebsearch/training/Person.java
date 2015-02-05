package ro.semanticwebsearch.training;

import javax.persistence.*;

/**
 * Created by valentin.spac on 2/5/2015.
 */
@Entity
@Table(name = "persoane")
public class Person {
    @Id
    private int id;

    private String nume;
    private String prenume;
    private String functie;

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getFunctie() {
        return functie;
    }

    public void setFunctie(String functie) {
        this.functie = functie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
