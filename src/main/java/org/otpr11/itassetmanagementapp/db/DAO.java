package org.otpr11.itassetmanagementapp.db;

import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.val;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.config.Config;

public class DAO {
  @Getter private static final DAO instance = new DAO();
  private SessionFactory sessionFactory;

  private DAO() {
    try {
      val config = Config.getConfig();

      // Disable Hibernate logging if desired
      if (Boolean.parseBoolean(config.get("DISABLE_HIBERNATE_LOGGING"))) {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
      }

      // Initialise Hibernate
      val hibernateConfig =
          new Configuration().configure(Main.class.getResource("config/hibernate.cfg.xml"));

      // (Re-)create database if desired
      val shouldInitDB = Boolean.parseBoolean(config.get("FORCE_DB_RECREATE"));

      System.out.println(
          shouldInitDB ? "Creating database tables." : "Using existing database tables.");
      hibernateConfig.setProperty("hibernate.hbm2ddl.auto", shouldInitDB ? "create" : "validate");

      // Define connection details
      val connectionUrl =
          "jdbc:postgresql://%s:%s/%s"
              .formatted(
                  config.get("POSTGRES_HOST"),
                  config.get("POSTGRES_PORT"),
                  config.get("POSTGRES_DATABASE"));

      hibernateConfig.setProperty("hibernate.connection.url", connectionUrl);
      hibernateConfig.setProperty("hibernate.connection.username", config.get("POSTGRES_USER"));
      hibernateConfig.setProperty("hibernate.connection.password", config.get("POSTGRES_PASSWORD"));

      sessionFactory = hibernateConfig.buildSessionFactory();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1); // No point trying to boot if we have no DB connection
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  protected void finalize() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }
}
