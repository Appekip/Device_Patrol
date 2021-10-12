module org.otpr11.itassetmanagementapp {
  requires java.persistence;
  requires java.naming;
  requires java.sql;
  requires lombok;
  requires com.google.common;
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
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;
  requires org.jetbrains.annotations;

  opens org.otpr11.itassetmanagementapp to
      javafx.fxml;
  opens org.otpr11.itassetmanagementapp.ui.controllers to
      javafx.fxml;
  opens org.otpr11.itassetmanagementapp.db.core to
      org.hibernate.orm.core;
  opens org.otpr11.itassetmanagementapp.db.model to
      javafx.base,
      org.hibernate.orm.core;
  opens org.otpr11.itassetmanagementapp.db.model.configuration to
      javafx.base,
      org.hibernate.orm.core;

  exports org.otpr11.itassetmanagementapp;
  exports org.otpr11.itassetmanagementapp.config;
  exports org.otpr11.itassetmanagementapp.constants;
  exports org.otpr11.itassetmanagementapp.db.core;
  exports org.otpr11.itassetmanagementapp.db.dao;
  exports org.otpr11.itassetmanagementapp.db.model;
  exports org.otpr11.itassetmanagementapp.db.model.configuration;
  exports org.otpr11.itassetmanagementapp.interfaces;
  exports org.otpr11.itassetmanagementapp.ui.controllers;
  exports org.otpr11.itassetmanagementapp.ui.utils;
  exports org.otpr11.itassetmanagementapp.utils;

  opens org.otpr11.itassetmanagementapp.interfaces to
      javafx.fxml,
      org.hibernate.orm.core;
}
