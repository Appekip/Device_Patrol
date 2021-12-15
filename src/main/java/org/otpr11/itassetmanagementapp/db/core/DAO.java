package org.otpr11.itassetmanagementapp.db.core;

import java.io.Serializable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;

/** Generic DAO object. */
@RequiredArgsConstructor
public abstract class DAO {
  protected final GlobalDAO dao;

  public abstract DTO get(@NotNull Serializable id);

  public abstract List<? extends DTO> getAll();

  public abstract <T extends DTO> boolean save(@NotNull T dto);

  public abstract <T extends DTO> boolean delete(@NotNull T dto);
}
