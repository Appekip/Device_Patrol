package org.otpr11.itassetmanagementapp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public abstract class StringUtils {
  public static String getFullExceptionStack(@NotNull Exception e) {
    val stringWriter = new StringWriter();
    val printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
