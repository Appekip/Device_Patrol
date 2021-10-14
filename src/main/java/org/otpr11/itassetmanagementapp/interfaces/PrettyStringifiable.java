package org.otpr11.itassetmanagementapp.interfaces;

import org.otpr11.itassetmanagementapp.utils.StringUtils;

/**
 * Some entity classes, especially {@link org.otpr11.itassetmanagementapp.db.core.DTO}s, often
 * contain information superfluous to the user, that is still equally warranted to be included in
 * the default toString() method for debugging purposes.
 *
 * <p>This interface allows these classes to declare themselves as being "pretty-stringifiable"
 * through exposing a <code>toPrettyString()</code> method, which is intended to return a
 * human-readable, easy-to-parse and aesthetically pleasing string representation of the object.
 *
 * <p>There are also some special helper functions available to pretty-stringifiable objects, such
 * as {@link StringUtils#getPrettyHWConfig} and {@link StringUtils#joinPrettyStrings}.
 */
public interface PrettyStringifiable {
  String toPrettyString();
}
