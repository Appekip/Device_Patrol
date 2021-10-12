package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.configuration.LaptopConfiguration;

/**
 * {@link org.otpr11.itassetmanagementapp.db.model.configuration.LaptopConfiguration} DAO
 * implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class LaptopConfigurationDAO extends DAO {
  private final GlobalDAO dao;

  protected LaptopConfigurationDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public LaptopConfiguration get(@NonNull Serializable id) {
    try {
      return dao.get(LaptopConfiguration.class, id);
    } catch (Exception e) {
      log.error("Could not get laptop configuration {}:", id, e);
      return null;
    }
  }

  @Override
  public List<LaptopConfiguration> getAll() {
    try {
      return dao.getAll(LaptopConfiguration.class);
    } catch (Exception e) {
      log.error("Could not get laptop configurations:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean save(@NotNull T dto) {
    try {
      log.trace("Creating laptop configuration {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not save laptop configuration {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting laptop configuration {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete laptop configuration {}:", dto, e);
      return false;
    }
  }
}
