module com.example.nb1javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.xml.bind;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;

    requires java.jws;
    requires java.xml.ws;

    opens com.example.nb1javafx to javafx.fxml;
    exports com.example.nb1javafx;
}