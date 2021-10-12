package org.otpr11.itassetmanagementapp.db.model.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;

/**
 * Represents a desktop-specific hardware configuration of a {@link
 * org.otpr11.itassetmanagementapp.db.model.Device}.
 */
@Entity
@Table(name = "desktop_configurations")
@ToString
@NoArgsConstructor
public class DesktopConfiguration extends DTO implements PrettyStringifiable {
  @Id
  @Getter
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  public DesktopConfiguration(
      @NotNull String cpu, @NotNull String gpu, @NotNull String memory, @NotNull String diskSize) {
    this.cpu = cpu;
    this.gpu = gpu;
    this.memory = memory;
    this.diskSize = diskSize;
  }

  public String toPrettyString() {
    return "%s, %s, %s RAM, %s disk".formatted(cpu, gpu, memory, diskSize);
  }
}
