<!-- @formatter:off -->

# it-asset-management-app
(OTP R11: Linus Willner, Lilli-Mari Häyhänen, Arttu Perämäki, Teemu Olkkonen)

Course project for Ohjelmistotuotantoprojekti TX00CF81-3012 course at Metropolia UAS, themed around IT asset management (similarly to tools like Snipe IT).

The vision of this project is to create a program to track the IT devices of a company, such as desktops and laptops, by using agile software development. The user of this program should be able to easily observe and manage the data of devices. This data contains basic information about the device, who is in possession of the device, status and amount of devices. The product will increase a company's efficiency in management of IT equipment.

We have used IntelliJ IDEA, Docker and PostgreSQL as development environment.

## Documentation

This README contains some basic development environment level documentation. Further development documentation may be found in the `docs` folder.

(Side note: The `documents` and `diagrams` folders contain mainly project meta-documentation, like reports and UML diagrams. No code documentation will be found here.)

For actual code documentation, feel free to browse the Javadoc in either the source code, or at the built site found at https://ohjelmistotuotantoprojekti-tx00cf81-3012-r11.gitlab.io/it-asset-management-app.

## Development

Prerequisites:
- Docker and docker-compose ([Instructions for Windows](https://docs.docker.com/desktop/windows/install), [instructions for Mac](https://docs.docker.com/desktop/mac/install))

### Build scripts

**Main:** Start the application.  
**Docker:** Run Docker-based development dependencies (including the database).  
**Test:** Runs unit tests.  
**Javadoc:** Generates Javadoc documentation. Outputs into `target/site/apidocs`.

### Starting

Run the "Docker" configuration before starting the application (as this will start the prerequisite database). Then run the "Main" configuration.

## Configuration

Configuration of the application is handled via environment variables in accordance with the [Twelve Factor App methodology](https://12factor.net/config).

There is a `.env.example` file at the root of this repository. This file serves as the single source of truth for possible configuration variables. Copy that to `src/main/java/resources/org/otpr11/itassetmanagementapp/config/.env` and edit as necessary.

## Gotchas and resolutions to common issues

### Updating .env

Alternative title: "Environment variables I just declared are turning up as `null` and now the program is crashing"

Updates to the .env file are sometimes not automatically mirrored to the built code that Java will eventually run, possibly resulting in confusion when a variable clearly exists in .env but appears to magically disappear when running the application. This can be solved by running Build => Rebuild Project from the IDEA title bar, which forces a re-mirroring of the `resources` directory.

### Cryptic Hibernate errors

When you do something that Hibernate does like, you may see some repeated but ultimately uninformative errors turn up. Below listed are some of these and some of their possible causes. 

#### "java.lang.IllegalStateException: org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing"

This error _can_ result if you forget to save a Hibernate child entity before trying to save a parent that references it. However, it also appears when you _do_ save the object, but the saving fails. This happens because the DAO CRUD operations are try-caught at query time (to not force you to try-catch everywhere every time you try to do something with the DB). However, this means that the the exception does not propagate all the way to your code, and it thinks everything is fine, so it goes on to try to save the parent object (which then fails because one of its children is transient).

This issue can be avoided by always checking the return value from the `create()`, `update()` and `delete()` DAO methods. They return `true` or `false` depending on whether the operation succeeded or not. If the return value is `false`, just halt execution there.

#### Using the `@NotNull` annotation

The rule for this one is pretty simple: **If you don't immediately know that you'll need to support someone sending `null` for a field, annotate it as `@NotNull` anyway, even if you're not sure.** This will save your bacon more times than you can count. If it eventually turns out that you do actually need nullability support, you can always just remove it.

**Side note:** There is a confusingly similar annotation called `@NonNull` from the lombok library. **DO NOT USE THAT ONE,** as it will just produce a `NullPointerException` when you put a null somewhere you shouldn't (and/but the editor won't forewarn you about it, unlike `@NotNull`). Hence why you shouldn't use it (unless you really, _really_ like crashes and try-catching everything and adding `throws` annotations everywhere).
