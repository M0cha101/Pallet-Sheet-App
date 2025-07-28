module com.alliance.palletkvalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires java.desktop;


    opens com.alliance.palletkvalproject to javafx.fxml;
    exports com.alliance.palletkvalproject;
}