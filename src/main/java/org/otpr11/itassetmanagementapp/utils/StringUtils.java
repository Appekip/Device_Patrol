package org.otpr11.itassetmanagementapp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;

/**
 * Generic string utilities.
 */
public abstract class StringUtils {
  /** Creates a complete string representation of an exception stack trace. */
  public static String getFullExceptionStack(@NotNull Exception e) {
    val stringWriter = new StringWriter();
    val printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  /**
   * Shortcut to immediately get the correct pretty string representation of a device's {@link Configuration}.
   *
   * @param cfg {@link Configuration}
   * @return {@link DesktopConfiguration#toPrettyString()}/{@link LaptopConfiguration#toPrettyString()}
   */
  public static String getPrettyHWConfig(@NotNull Configuration cfg) {
    return switch (cfg.getDeviceType()) {
      case DESKTOP -> cfg.getDesktopConfiguration().toPrettyString();
      case LAPTOP -> cfg.getLaptopConfiguration().toPrettyString();
    };
  }

  /**
   * A {@link String#join} implementation for {@link PrettyStringifiable} objects.
   *
   * @param array List of {@link PrettyStringifiable} objects to join together
   * @return Joined string representations of the passed objects delimited by commas
   */
  public static String joinPrettyStrings(@NotNull List<? extends PrettyStringifiable> array) {
    val content = new StringBuilder();

    for (int i = 0; i < array.size(); i++) {
      content.append(array.get(i).toPrettyString());

      // Append commas where relevant
      if (array.size() > 1 && i < array.size() - 1) {
        content.append(", ");
      }
    }

    return content.toString();
  }
 }
