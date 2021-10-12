package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.Device;

/**
 * {@link Device} DAO implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class DeviceDAO extends DAO {
  private final GlobalDAO dao;

  protected DeviceDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public Device get(@NonNull Serializable id) {
    try {
      return dao.get(Device.class, id);
    } catch (Exception e) {
      log.error("Could not get device {}:", id, e);
      return null;
    }
  }

  @Override
  public List<Device> getAll() {
    try {
      return dao.getAll(Device.class);
    } catch (Exception e) {
      log.error("Could not get devices:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean save(@NotNull T dto) {
    try {
      log.trace("Saving device {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not save device {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting device {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete device {}:", dto, e);
      return false;
    }
  }
}
