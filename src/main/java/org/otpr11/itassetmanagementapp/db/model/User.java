package org.otpr11.itassetmanagementapp.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;

/** Represents a user of a {@link Device}. */
@Entity
@Table(name = "users")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@AllArgsConstructor
@NoArgsConstructor
public class User extends DTO {
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
}
