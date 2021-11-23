package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;

/**
 * {@link Configuration} DAO implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class ConfigurationDAO extends DAO {
  private final GlobalDAO dao;

  protected ConfigurationDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  /**
   * Helper function to easily and quickly save a desktop configuration.
   *
   * <p>Prefer using this function over of stacking calls to the DAOs themselves, as this one
   * automatically saves the provided DesktopConfiguration object before self-creating a new
   * Configuration entity.
   *
   * @param deviceCfg {@link DesktopConfiguration}
   * @return {@link Configuration}
   */
  public Configuration saveDesktop(DesktopConfiguration deviceCfg) {
    dao.desktopConfigurations.save(deviceCfg);
    val cfg = new Configuration(deviceCfg);
    save(cfg);
    return cfg;
  }

  /**
   * Helper function to easily and quickly save a laptop configuration.
   *
   * <p>Prefer using this function over of stacking calls to the DAOs themselves, as this one
   * automatically saves the provided LaptopConfiguration object before self-creating a new
   * Configuration entity.
   *
   * @param deviceCfg {@link LaptopConfiguration}
   * @return {@link Configuration}
   */
  public Configuration saveLaptop(LaptopConfiguration deviceCfg) {
    dao.laptopConfigurations.save(deviceCfg);
    val cfg = new Configuration(deviceCfg);
    save(cfg);
    return cfg;
  }

  @Override
  public Configuration get(@NonNull Serializable id) {
    try {
      return dao.get(Configuration.class, id);
    } catch (Exception e) {
      log.error("Could not get configuration {}:", id, e);
      return null;
    }
  }

  @Override
  public List<Configuration> getAll() {
    try {
      return dao.getAll(Configuration.class);
    } catch (Exception e) {
      log.error("Could not get configurations:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean save(@NotNull T dto) {
    try {
      log.trace("Saving configuration {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not save configuration {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting configuration {}.", dto);

      // Delete connected object as well
      val cfg = (Configuration) dto;

      var success = false;

      switch (cfg.getDeviceType()) {
        case DESKTOP -> success = dao.desktopConfigurations.delete(cfg.getDesktopConfiguration());
        case LAPTOP -> success = dao.laptopConfigurations.delete(cfg.getLaptopConfiguration());
      }

      if (!success) {
        log.error("Deletion of connected entity failed, cannot delete configuration {}.", dto);
        return false;
      }

      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete configuration {}:", dto, e);
      return false;
    }
  }
}
