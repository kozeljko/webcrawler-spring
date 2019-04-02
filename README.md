# WebCrawler

This is a webcrawler we made for a school project. It takes a few seed sites and runs until it retrieves
100k pages. 

All source code is available on https://github.com/kozeljko/webcrawler-spring as well.

### Database start (in Docker)

docker run -d --name webcrawler -e POSTGRES_USERNAME=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=webcrawler -p 5432:5432 postgres:latest

### Database dump disclaimer

So, like I said, I managed to crawl 100k pages, but forgot to save any content. To fix that, I re-ran the crawler again and parsed 40k pages.
Even after truncating to the sites we had to include, the dump size was still 350MB+. While attempting to set some of those content 
to null, I managed to forget the limit query field and I wiped all the html content again. 

I have made a backup before, but I cannot restore it. At this point I give up and will submit the previous dump.

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
    
    The drives are located in driver folder. The provided executables are for Chrome 73. Should you have a newer version of Chrome, visit https://chromedriver.storage.googleapis.com/index.html
    and download the correct one.
 6. Build maven project from the root (mvn clean package)
 7. Run the built jar: java -jar target/webcrawler-0.0.1-SNAPSHOT.jar
