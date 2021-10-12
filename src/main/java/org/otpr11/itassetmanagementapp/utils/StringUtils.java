package org.otpr11.itassetmanagementapp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;

public abstract class StringUtils {
  public static String getFullExceptionStack(@NotNull Exception e) {
    val stringWriter = new StringWriter();
    val printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  public static String getPrettyDeviceString(@NotNull Configuration cfg) {
    return switch (cfg.getDeviceType()) {
      case DESKTOP -> cfg.getDesktopConfiguration().toPrettyString();
      case LAPTOP -> cfg.getLaptopConfiguration().toPrettyString();
    };
  }

  public static String joinStrings(@NotNull List<String> array) {
    val content = new StringBuilder();

    for (int i = 0; i < array.size(); i++) {
      content.append(array.get(i));

      // Append commas where relevant
      if (array.size() > 1 && i < array.size() - 1) {
        content.append(", ");
      }
    }

    return content.toString();
  }

  public static String joinPrettyStrings(@NotNull List<PrettyStringifiable> array) {
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
