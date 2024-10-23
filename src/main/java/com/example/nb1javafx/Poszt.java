package com.example.nb1javafx;

import javax.persistence.*;

@Entity
@Table(name = "poszt")
public class Poszt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int Id;
    @Column(name = "nev")
    private String Nev;

    public Poszt(int id, String nev) {
        Id = id;
        Nev = nev;
    }

    public Poszt() {

    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNev() {
        return Nev;
    }

    public void setNev(String nev) {
        Nev = nev;
    }
}
