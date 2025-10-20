module org.example.inferface_distribution {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens org.example.inferface_distribution to javafx.fxml;
    exports org.example.inferface_distribution;
}