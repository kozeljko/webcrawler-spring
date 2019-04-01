# Getting Started

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)


docker run -d --name webcrawler -e POSTGRES_USERNAME=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=webcrawler -p 5432:5432 postgres:latest

### Setup guide

Required: 
 * Java 8
 * Maven
 * PostgreSQL server or a Docker 
 * A way to run SQL scripts on server
 
 Steps:
 
 1. Start database
 2. Run SQL script sql-scripts/init.sql
 3. Run SQL script sql-scripts/extra.sql
 4. Build maven project from the root (mvn clean package)
 5. TODO