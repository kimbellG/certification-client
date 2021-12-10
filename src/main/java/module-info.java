module org.filippenkov.certification_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires unirest.java;

    requires gson;
    requires java.sql;
    requires java.dotenv;
    requires com.fasterxml.jackson.databind;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.compress;
    requires java.desktop;

    opens org.filippenkov.certification_client to javafx.fxml;
    exports org.filippenkov.certification_client.controllers.managers;
    opens org.filippenkov.certification_client.controllers.managers to javafx.fxml;

    opens org.filippenkov.certification_client.models to gson, javafx.base;

    exports org.filippenkov.certification_client;
    exports org.filippenkov.certification_client.controllers;
    opens org.filippenkov.certification_client.controllers to javafx.fxml;
    exports org.filippenkov.certification_client.controllers.leads;
    opens org.filippenkov.certification_client.controllers.leads to javafx.fxml;
    exports org.filippenkov.certification_client.models to com.fasterxml.jackson.databind;
}