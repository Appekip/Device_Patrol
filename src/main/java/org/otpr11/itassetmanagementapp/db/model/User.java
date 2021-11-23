package org.otpr11.itassetmanagementapp.db.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

/** Represents a user of a {@link Device}. */
@Entity
@Table(name = "users")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@NoArgsConstructor
public class User extends DTO {
  @Getter(AccessLevel.PACKAGE)
  @OneToMany
  @Exclude
  private final List<Device> devices = new ArrayList<>();

  @Id
  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, length = 64, name = "employee_id")
  private String id;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "first_name")
  private String firstName;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "last_name")
  private String lastName;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String phone;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String email;

  public User(
      @NotNull String id,
      @NotNull String firstName,
      @NotNull String lastName,
      @NotNull String phone,
      @NotNull String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phone = phone;
    this.email = email;
  }

  @PreRemove
  private void preRemove() {
    val dao = GlobalDAO.getInstance();

    for (val device : devices) {
      device.setUser(null);
      dao.devices.save(device);
    }
  }
}
