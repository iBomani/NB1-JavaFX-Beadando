package com.example.nb1javafx;

import javax.persistence.*;

@Entity
@Table(name = "labdarugo")
public class Labdarugo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int Id;
    @Column(name = "mezszam")
    public int Mezszam;
    @Column(name = "posztid")
    public int PosztId;
    @Column(name = "utonev")
    public String Utonev;
    @Column(name = "vezeteknev")
    public String Vezeteknev;
    @Column(name = "szulido")
    public String Szulido;
    @Column(name = "magyar")
    public boolean Magyar;
    @Column(name = "kulfoldi")
    public boolean Kulfoldi;
    @Column(name = "ertek")
    public int Ertek;
    @Column(name = "klubid")
    public int KlubId;

    public Labdarugo(int id, int mezszam, int posztId, String utonev, String vezeteknev, String szulido, boolean magyar, boolean kulfoldi, int ertek, int klubId) {
        Id = id;
        Mezszam = mezszam;
        PosztId = posztId;
        Utonev = utonev;
        Vezeteknev = vezeteknev;
        Szulido = szulido;
        Magyar = magyar;
        Kulfoldi = kulfoldi;
        Ertek = ertek;
        KlubId = klubId;
    }

    public Labdarugo() {

    }




    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getMezszam() {
        return Mezszam;
    }

    public void setMezszam(int mezszam) {
        Mezszam = mezszam;
    }

    public int getPosztId() {
        return PosztId;
    }

    public void setPosztId(int posztId) {
        PosztId = posztId;
    }

    public String getUtonev() {
        return Utonev;
    }

    public void setUtonev(String utonev) {
        Utonev = utonev;
    }

    public String getVezeteknev() {
        return Vezeteknev;
    }

    public void setVezeteknev(String vezeteknev) {
        Vezeteknev = vezeteknev;
    }

    public String getSzulido() {
        return Szulido;
    }

    public void setSzulido(String szulido) {
        Szulido = szulido;
    }

    public boolean isMagyar() {
        return Magyar;
    }

    public void setMagyar(boolean magyar) {
        Magyar = magyar;
    }

    public boolean isKulfoldi() {
        return Kulfoldi;
    }

    public void setKulfoldi(boolean kulfoldi) {
        Kulfoldi = kulfoldi;
    }

    public int getErtek() {
        return Ertek;
    }

    public void setErtek(int ertek) {
        Ertek = ertek;
    }

    public int getKlubId() {
        return KlubId;
    }

    public void setKlubId(int klubId) {
        KlubId = klubId;
    }
}
