package org.otpr11.itassetmanagementapp.db.model;

import com.google.common.base.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

/**
 * Represents a desktop-specific hardware configuration of a {@link
 * org.otpr11.itassetmanagementapp.db.model.Device}.
 */
@Entity
@Table(name = "desktop_configurations")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@NoArgsConstructor
public class DesktopConfiguration extends DTO implements PrettyStringifiable {
  @Id
  @Getter
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String cpu;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String gpu;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String memory;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "disk_size")
  private String diskSize;

  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PACKAGE)
  @OneToOne
  @Exclude
  private Configuration configuration;

  public DesktopConfiguration(
      @NotNull String cpu, @NotNull String gpu, @NotNull String memory, @NotNull String diskSize) {
    this.cpu = cpu;
    this.gpu = gpu;
    this.memory = memory;
    this.diskSize = diskSize;
  }

  public String toPrettyString() {
    return "%s, %s, %s RAM, %s %s"
        .formatted(
            cpu,
            gpu,
            memory,
            diskSize,
            LocaleEngine.getResourceBundle().getString("disk").toLowerCase());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesktopConfiguration that = (DesktopConfiguration) o;
    return Objects.equal(cpu, that.cpu)
        && Objects.equal(gpu, that.gpu)
        && Objects.equal(memory, that.memory)
        && Objects.equal(diskSize, that.diskSize)
        && Objects.equal(configuration, that.configuration);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(cpu, gpu, memory, diskSize, configuration);
  }

  @PreRemove
  private void preRemove() {
    if (configuration != null) {
      val dao = GlobalDAO.getInstance();
      configuration.setDesktopConfiguration(null);
      dao.configurations.save(configuration);
    }
  }
}
