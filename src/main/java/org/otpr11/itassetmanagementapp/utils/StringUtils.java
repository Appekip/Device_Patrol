package org.otpr11.itassetmanagementapp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;

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
 }
