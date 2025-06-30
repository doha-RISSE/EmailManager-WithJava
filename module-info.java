/**
 * 
 */
/**
 * 
 */
module Email {
    requires jakarta.mail;
    requires jakarta.activation;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;

    opens application to javafx.fxml;
    exports application;
}
