package org.otpr11.itassetmanagementapp.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;

/** Represents the vacancy status of a {@link Device}. */
@Entity
@Table(name = "statuses")
@EntityListeners({DatabaseEventPropagator.class})
@AllArgsConstructor
@NoArgsConstructor
public class Status extends DTO {
  @Id
  @Getter
  @NotNull
  @Column(nullable = false)
  private String status;

  @Override
  public String toString() {
    // Just returning the value here since this should really just be an enum (as that's how it
    // operates, and that's how it's meant to operate), but apparently this has to be stored in the
    // database per the spec, so...
    return status;
  }
}
