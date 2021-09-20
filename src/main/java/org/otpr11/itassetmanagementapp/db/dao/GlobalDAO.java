package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseQueryException;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

/**
 * Global Data Access Object that provides access to all database functions in the application. This
 * class also contains generic CRUD logic for DTO entities, to abstract away the slightly messy
 * session handling and transaction logic from the actual DAOs.
 *
 * <p>The documented properties provide access to sub-DAOs for granular data insertion (though, in
 * most cases, you're probably just going to need {@link GlobalDAO#devices}).
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * val dao = GlobalDAO.getInstance();
 * val os1 = new OperatingSystem("macOS Big Sur (rev. 1)", "11.5.2", "20G95");
 * val os2 = new OperatingSystem("macOS Big Sur (rev. 2)", "11.5.2", "20G95");
 * val os3 = new OperatingSystem("macOS Big Sur (rev. 3)", "11.5.2", "20G95");
 *
 * dao.operatingSystems.create(os1);
 * dao.operatingSystems.create(os2);
 * dao.operatingSystems.create(os3);
 *
 * os2.setBuildNumber("20G96");
 * dao.operatingSystems.update(os2);
 *
 * dao.operatingSystems.delete(os3);
 *
 * System.out.println(dao.operatingSystems.getAll());
 * }</pre>
 */
@Log4j2
public class GlobalDAO {

  /** Provides the entrypoint to the DAO. */
  @Getter private static final GlobalDAO instance = new GlobalDAO();

  /** Exposes CRUD operations for the device database. */
  public final DeviceDAO devices = new DeviceDAO(this);

  /** Exposes CRUD operations for the user database. */
  public final UserDAO users = new UserDAO(this);

  /** Exposes CRUD operations for the location database. */
  public final LocationDAO locations = new LocationDAO(this);

  /** Exposes CRUD operations for the OS database. */
  public final OperatingSystemDAO operatingSystems = new OperatingSystemDAO(this);

  /** Exposes CRUD operations for the hardware configuration database. */
  public final ConfigurationDAO configurations = new ConfigurationDAO(this);

  /** Exposes CRUD operations for the desktop-specific hardware configuration database. */
  public final DesktopConfigurationDAO desktopConfigurations = new DesktopConfigurationDAO(this);

  /** Exposes CRUD operations for the laptop-specific hardware configuration database. */
  public final LaptopConfigurationDAO laptopConfigurations = new LaptopConfigurationDAO(this);

  private SessionFactory sessionFactory;

  private GlobalDAO() {
    try {
      log.debug("Initialising database...");
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

      log.debug(
          shouldInitDB ? "Will create database tables." : "Will use existing database tables.");
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

      log.debug("Building session factory...");
      sessionFactory = hibernateConfig.buildSessionFactory();
      log.debug("Session factory ready.");

      log.debug("Database initialisation complete.");

      if (shouldInitDB) {
        log.debug("Running post-core initialisations...");
        createStatuses();
        log.debug("Post-core initialisations complete.");
      }
    } catch (Exception e) {
      log.fatal("Could not initialise database:", e);
      AlertUtils.showExceptionAlert("Could not establish database connection.", e);
      System.exit(1); // No point trying to boot if we have no DB connection
    }
  }

  /**
   * Generic database fetch function for a singular DTO entity.
   *
   * @param dtoClass Class of the DTO entity to look for.
   * @param id Entity ID (usually String or long) to look for.
   * @param <T> DTO class (i.e. any class from db.model, which all need to extend {@link DTO})
   * @return {@link DTO}
   * @throws DatabaseQueryException to rethrow any underlying exceptions from Hibernate.
   */
  protected <T extends DTO> T get(Class<T> dtoClass, Serializable id)
      throws DatabaseQueryException {
    Transaction transaction = null;

    try (val session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      val result = session.get(dtoClass, id);
      transaction.commit();
      return result;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }

      throw new DatabaseQueryException(e);
    }
  }

  /**
   * Generic database fetch function for all DTO entities of a certain type.
   *
   * @param dtoClass Class of the DTO entity.
   * @param <T> DTO class (i.e. any class from db.model, which all need to extend {@link DTO})
   * @return {@link List}<{@link DTO}>
   * @throws DatabaseQueryException to rethrow any underlying exceptions from Hibernate.
   */
  protected <T extends DTO> List<T> getAll(Class<T> dtoClass) throws DatabaseQueryException {
    try (val session = sessionFactory.openSession()) {

      val builder = session.getCriteriaBuilder();
      val query = builder.createQuery(dtoClass);
      query.from(dtoClass);

      return session.createQuery(query).getResultList();
    } catch (Exception e) {
      throw new DatabaseQueryException(e);
    }
  }

  /**
   * Generic function to create or update a DTO entity.
   *
   * @param dto DTO to update.
   * @param <T> DTO class (i.e. any class from db.model, which all need to extend {@link DTO})
   * @throws DatabaseQueryException to rethrow any underlying exceptions from Hibernate.
   */
  protected <T extends DTO> void saveOrUpdate(T dto) throws DatabaseQueryException {
    Transaction transaction = null;

    try (val session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(dto);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }

      throw new DatabaseQueryException(e);
    }
  }

  /**
   * Generic function to delete a DTO entity.
   *
   * @param dto DTO to delete.
   * @param <T> DTO class (i.e. any class from db.model, which all need to extend {@link DTO})
   * @throws DatabaseQueryException to rethrow any underlying exceptions from Hibernate.
   */
  protected <T extends DTO> void delete(T dto) throws DatabaseQueryException {
    Transaction transaction = null;

    try (val session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.delete(dto);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }

      throw new DatabaseQueryException(e);
    }
  }

  /** Initialises all known {@link Device#getStatus()} statuses into the database on first boot. */
  private void createStatuses() {
    try {
      log.debug("Creating device statuses...");

      for (val status : DeviceStatus.values()) {
        val toCreate = status.toString();
        log.trace("Creating status {}.", toCreate);
        saveOrUpdate(new Status(toCreate));
      }

      log.debug("Device statuses created successfully.");
    } catch (Exception e) {
      log.fatal("Error while creating statuses:", e);
      System.exit(1); // We need these in the database for correct operation
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
