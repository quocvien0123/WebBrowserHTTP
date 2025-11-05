module com.example.simplehttpbrowser {
    requires javafx.fxml;
    requires org.jsoup;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.web;
    requires jdk.httpserver;

    opens com.example.simplehttpbrowser to javafx.fxml;
    opens com.example.simplehttpbrowser.controller to javafx.fxml;


    exports com.example.simplehttpbrowser;
    exports com.example.simplehttpbrowser.controller;
}
