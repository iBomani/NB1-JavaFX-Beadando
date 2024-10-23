package com.example.nb1javafx;

import javax.persistence.*;


@Entity
@Table(name = "klub")
public class Klub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int Id;
    @Column(name = "csapatnev")
    private String Csapatnev;

    public Klub(int id, String csapatnev) {
        Id = id;
        Csapatnev = csapatnev;
    }

    public Klub() {

    }
}
