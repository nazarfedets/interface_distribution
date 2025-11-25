module org.example.inferface_distribution {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;


    opens org.example.inferface_distribution to javafx.fxml;
    exports org.example.inferface_distribution;
}