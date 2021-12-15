package org.otpr11.itassetmanagementapp.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.Main;

/**
 * Configuration manager. Access to the config is provided via {@code Config.getConfig()}, which
 * exposes a standard {@link Dotenv} API.
 *
 * <p>Configuration is loaded from a config.env file in the resources folder. An example of this
 * file is in the root of the repository, in a file called config.env.example.
 */
// TODO: Currently no validation on that values declared in config.env.example have been declared in
// config.env
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Config {
  private static Dotenv config;

  public static Dotenv getConfig() {
    if (config == null) {
      log.trace("Loading configuration.");
      Config.load();
      log.trace("Configuration loaded.");
    }

    return config;
  }

  private static void load() {
    // Because we're using JavaFX, we need to adhere to the `resources` folder; hence
    // some path parsing magic is needed to figure out where the config.env file is.
    // Oh, and because Java is great, dotfiles get excluded during JAR builds. Hence why we have to
    // use this stupid naming convention.
    String configDir = "";
    val configPath = Main.class.getResource("config/config.env");

    // If resource exists, parse the folder out; if it doesn't exist, dotenv will crash us out,
    // so no need to throw a strop about that
    if (configPath != null) {
      val parts = configPath.toString().split("/");
      val directory = Arrays.copyOfRange(parts, 0, parts.length - 1);
      configDir = String.join("/", directory);
    }

    val dotenv = Dotenv.configure();

    // If we're running in a JAR, paths get completely screwed up, so we need to hardcode the path
    // Oh, and this is how we determine whether we're running from a JAR, because... I don't even
    // know anymore.
    val isInJar =
        Objects.requireNonNull(Main.class.getResource("Main.class")).toString().startsWith("jar:");

    if (isInJar) {
      dotenv.filename("org/otpr11/itassetmanagementapp/config/config.env");
    } else {
      dotenv.directory(configDir).filename("config.env");
    }

    // Load configuration
    config = dotenv.load();
  }
}
