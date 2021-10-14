package org.otpr11.itassetmanagementapp.constants;
/**
 * This enum is just a convenient repackaging of the default Hibernate/JPA events (which do not use
 * these names - despite them being the actual event names - in their default implementations).
 *
 * @see <a
 *     href="https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/Hibernate_User_Guide.html#events-jpa-callbacks">https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/Hibernate_User_Guide.html#events-jpa-callbacks</a>
 */
public enum DatabaseEvent {
  PRE_PERSIST,
  POST_PERSIST,
  PRE_REMOVE,
  POST_REMOVE,
  PRE_UPDATE,
  POST_UPDATE,
  POST_LOAD
}
