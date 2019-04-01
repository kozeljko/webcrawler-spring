# WebCrawler

### Database start (in Docker)

docker run -d --name webcrawler -e POSTGRES_USERNAME=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=webcrawler -p 5432:5432 postgres:latest

### Setup guide

Required: 
 * Java 8
 * Maven
 * PostgreSQL server or a Docker 
 * A way to run SQL scripts on server (I used IntelliJ IDEA for this, pgAdmin should work)
 * Chrome browser installed
 
 Steps:
 
 1. Start database (you can use the above docker run command)
 2. Run SQL script sql-scripts/init.sql
 3. Run SQL script sql-scripts/extra.sql
 4. Set number of threads (worker.threads.number) in /src/main/resources/application.properties
 5. Set absolute location of chrome driver (driver.location) in /src/main/resources/application.properties
    
    The provided executables are for Chrome 73. Should you have a newer version of Chrome, visit https://chromedriver.storage.googleapis.com/index.html
    and download the correct one.
 6. Build maven project from the root (mvn clean package)
 7. Run the built jar: java -jar target/webcrawler-0.0.1-SNAPSHOT.jar
