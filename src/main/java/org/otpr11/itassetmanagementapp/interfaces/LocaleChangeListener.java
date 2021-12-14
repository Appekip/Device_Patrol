package org.otpr11.itassetmanagementapp.interfaces;

import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

/**
 * When properly registered (as per directives in the documentation of {@link LocaleEngine}),
 * classes implementing this interface will receive locale update events. The complete documentation
 * of this behaviour is in the aforementioned class.
 *
 * @see LocaleEngine
 */
public interface LocaleChangeListener {
  void onLocaleChange();
}
