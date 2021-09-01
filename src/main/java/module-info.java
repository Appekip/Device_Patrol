module org.otpr11.itassetmanagementapp {
  requires lombok;

  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires javafx.graphics;
  requires dotenv.java;

  opens org.otpr11.itassetmanagementapp to javafx.fxml;
  exports org.otpr11.itassetmanagementapp;
}