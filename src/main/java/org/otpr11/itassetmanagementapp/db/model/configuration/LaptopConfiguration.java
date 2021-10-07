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

/**
 * Represents a laptop-specific hardware configuration of a {@link
 * org.otpr11.itassetmanagementapp.db.model.Device}.
 */
@Entity
@Table(name = "laptop_configurations")
@ToString
@NoArgsConstructor
public class LaptopConfiguration extends DTO {
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

  @Getter
  @Setter
  @Column(nullable = false, name = "screen_size")
  private int screenSize; // In inches

  public LaptopConfiguration(
      @NotNull String cpu,
      @NotNull String gpu,
      @NotNull String memory,
      @NotNull String diskSize,
      int screenSize) {
    this.cpu = cpu;
    this.gpu = gpu;
    this.memory = memory;
    this.diskSize = diskSize;
    this.screenSize = screenSize;
  }

  public String toPrettyString() {
    return "%s\", %s, %s, %s RAM, %s disk".formatted(screenSize, cpu, gpu, memory, diskSize);
  }
}
