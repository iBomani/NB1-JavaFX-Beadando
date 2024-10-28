module com.example.nb1javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;
    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires com.sun.xml.ws.jaxws;

    requires java.jws;
    requires java.xml.ws;
    requires java.desktop;
    requires gson;
    requires httpcore;
    requires httpclient;
    requires HegesztesSoapMNB;
    requires gmbal;
    requires net.bytebuddy;

    opens com.example.nb1javafx to javafx.fxml;

    exports com.example.nb1javafx;

    opens com.oanda.v20;
    opens com.oanda.v20.account;
    opens com.oanda.v20.pricing;
    opens com.oanda.v20.pricing_common;
    opens com.oanda.v20.order;
    opens com.oanda.v20.instrument;
    opens com.oanda.v20.transaction;
    opens com.oanda.v20.trade;
    exports com.oanda.v20.primitives;
    exports com.oanda.v20.transaction;
    exports com.oanda.v20.pricing_common;
    exports com.oanda.v20.order;
    exports com.oanda.v20.trade;
    exports com.soapclient;
    opens com.soapclient to javafx.fxml;

}