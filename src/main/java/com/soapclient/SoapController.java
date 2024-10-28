package com.soapclient;

import csomag1.MNBArfolyamServiceSoap;
import org.example.Main;

public class SoapController {
    public MNBArfolyamServiceSoap getService() {
        return Main.getService();
    }

}
