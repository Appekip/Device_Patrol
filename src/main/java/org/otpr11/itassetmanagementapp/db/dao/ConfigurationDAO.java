package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;
import org.otpr11.itassetmanagementapp.db.model.configuration.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.configuration.LaptopConfiguration;

/**
 * {@link org.otpr11.itassetmanagementapp.db.model.configuration.Configuration} DAO implementation.
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

  // TODO: Eventually move these to some kind of more sophisticated DB driver class?
  // TODO: Wait for feedback from UI dev to investigate what a good and intuitive DB driver would be

  /**
   * Helper function to easily and quickly create a desktop configuration.
   *
   * <p>Prefer using this function over of stacking calls to the DAOs themselves, as this one
   * automatically saves the provided DesktopConfiguration object before self-creating a new
   * Configuration entity.
   *
   * @param deviceCfg {@link DesktopConfiguration}
   */
  public void createDesktop(DesktopConfiguration deviceCfg) {
    dao.desktopConfigurations.create(deviceCfg);
    dao.configurations.create(new Configuration(deviceCfg));
  }

  /**
   * Helper function to easily and quickly create a laptop configuration.
   *
   * <p>Prefer using this function over of stacking calls to the DAOs themselves, as this one
   * automatically saves the provided LaptopConfiguration object before self-creating a new
   * Configuration entity.
   *
   * @param deviceCfg {@link LaptopConfiguration}
   */
  public void createLaptop(LaptopConfiguration deviceCfg) {
    dao.laptopConfigurations.create(deviceCfg);
    create(new Configuration(deviceCfg));
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
  public <T extends DTO> boolean create(@NotNull T dto) {
    try {
      log.trace("Creating configuration {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not create configuration {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean update(@NotNull T dto) {
    try {
      log.trace("Updating configuration {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not update configuration {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting configuration {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete configuration {}:", dto, e);
      return false;
    }
  }
}
