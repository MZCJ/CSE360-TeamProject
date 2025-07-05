module sundevil_marketplace {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.asu.sundevil to javafx.fxml;
    opens com.asu.sundevil.model to javafx.base, javafx.fxml;

    exports com.asu.sundevil;
    exports com.asu.sundevil.model;
}
