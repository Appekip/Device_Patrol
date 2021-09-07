module org.otpr11.itassetmanagementapp {
  requires java.persistence;
  requires java.naming;
  requires java.sql;
  requires lombok;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires dotenv.java;
  requires org.hibernate.orm.core;

  opens org.otpr11.itassetmanagementapp to
      javafx.fxml;

  exports org.otpr11.itassetmanagementapp;
}
