# it-asset-management-app

Course project for Ohjelmistotuotantoprojekti TX00CF81-3012 course at Metropolia UAS, themed around IT asset management (similarly to tools like Snipe IT).

## Development

Prerequisites:
- Docker and docker-compose ([Instructions for Windows](https://docs.docker.com/desktop/windows/install), [instructions for Mac](https://docs.docker.com/desktop/mac/install))

### Build scripts

**Main:** Start the application.  
**Docker:** Run Docker-based development dependencies (including the database).  
**Test:** Runs unit tests.  
**Javadoc:** Generates Javadoc documentation. Outputs into `target/site/apidocs`.

## Configuration

Configuration of the application is handled via environment variables in accordance with the [Twelve Factor App methodology](https://12factor.net/config).

There is a `.env.example` file at the root of this repository. Copy that to `src/main/java/resources/org/otpr11/itassetmanagementapp/.env` and edit as necessary.