package org.otpr11.itassetmanagementapp.db.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;

/** Represents an operating system a {@link Device} is running. */
@Entity
@Table(name = "operating_systems")
@ToString
@NoArgsConstructor
public class OperatingSystem extends DTO {

  @Id
  @Getter
  @Column(nullable = false, name = "os_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String name;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String version;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String buildNumber;
  // ^ is a string because macOS likes to add random letters into build numbers (thanks Apple)

  // Left unused as it's only needed to connect the database tables together
  @ManyToMany @Exclude private List<Device> devices;

  public OperatingSystem(
      @NotNull String name, @NotNull String version, @NotNull String buildNumber) {
    this.name = name;
    this.version = version;
    this.buildNumber = buildNumber;
  }
}
