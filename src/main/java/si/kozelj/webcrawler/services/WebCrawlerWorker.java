package si.kozelj.webcrawler.services;


import crawlercommons.robots.SimpleRobotRules;
import one.util.streamex.StreamEx;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import si.kozelj.webcrawler.CrawlerConstants;
import si.kozelj.webcrawler.models.Image;
import si.kozelj.webcrawler.models.Link;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.models.PageData;
import si.kozelj.webcrawler.repositories.*;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;
import si.kozelj.webcrawler.util.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Scope("prototype")
@Component
public class WebCrawlerWorker implements Runnable {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageDataRepository pageDataRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private SiteFrontierHandler frontierHandler;

    @Autowired
    private RobotRulesHandler robotRulesHandler;

    @Value("${driver.location}")
    private String driverLocation;

    private final Logger logger = LoggerFactory.getLogger(WebCrawlerWorker.class);
    private final String threadID = UUID.randomUUID().toString();

    @Override
    public void run() {
        logger.info("Setting up worker " + threadID);

        // setup webdriver
        System.setProperty("webdriver.chrome.driver", driverLocation);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--ignore-certificate-errors");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");
        options.addArguments("--dns-prefetch-disable");
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);

        logger.info("Worker " + threadID + " going to the frontier");
        for (String url; (url = frontierHandler.getNext()) != null; ) {
            URL urlObject;
            try {
                urlObject = new URL(url);
            } catch (MalformedURLException e) {
                try {
                    Page pageObject = pageRepository.findByUrl(url);
                    if (pageObject == null) {
                        pageObject = new Page();
                    }
                    pageObject.setSite(null); // can't set site
                    pageObject.setUrl(url);
                    pageObject.setHtmlContent("MALFORMED");
                    pageObject.setPageTypeCode(CrawlerConstants.PAGE_TYPE_HTML);
                    pageObject.setAccessedTime(Timestamp.from(Instant.now()));
                    pageObject.setHttpStatusCode(403);

                    pageRepository.saveAndFlush(pageObject);
                } catch (Exception ignored) {
                    logger.error("Error while saving malformed url page");
                }

                continue;
            }

            // get robots object
            SimpleRobotRules robotRules = robotRulesHandler.getRobotRules(urlObject.toString());
            if (!robotRules.isAllowed(urlObject.toString())) {
                try {
                    Page pageObject = pageRepository.findByUrl(url);
                    if (pageObject == null) {
                        pageObject = new Page();
                    }
                    pageObject.setSite(siteRepository.findSiteByDomain(urlObject.getHost()));
                    pageObject.setUrl(url);
                    pageObject.setHtmlContent("UNALLOWED");
                    pageObject.setPageTypeCode(CrawlerConstants.PAGE_TYPE_HTML);
                    pageObject.setAccessedTime(Timestamp.from(Instant.now()));
                    pageObject.setHttpStatusCode(405);

                    pageRepository.saveAndFlush(pageObject);
                } catch (Exception ignored) {
                    logger.error("Error while saving unallowed page");
                }
                continue;
            }

            // retrieve Page object, if it exists
            Page pageObject = pageRepository.findByUrl(url);

            // set site
            pageObject.setSite(siteRepository.findSiteByDomain(urlObject.getHost()));

            // retrieve content
            String correctedString = Util.prependHttp(urlObject.toString());

            // if the target URL is a document, save it
            if (isDocument(correctedString)) {
                // save page object
                pageObject.setPageTypeCode(CrawlerConstants.PAGE_TYPE_BINARY);
                pageObject.setAccessedTime(Timestamp.from(Instant.now()));
                pageObject.setHttpStatusCode(200);

                try {
                    pageObject = pageRepository.saveAndFlush(pageObject);
                } catch (Exception e) {
                    logger.error("Error while saving unparsable document page");
                    continue;
                }

                // save document object
                PageData pageData = new PageData();
                pageData.setPage(pageObject);
                pageData.setDataType(getDataType(correctedString));
                pageData.setData(new byte[0]); // we set an empty array, because of time constraints

                pageDataRepository.saveAndFlush(pageData);
                continue;
            }

            // accessed time
            pageObject.setAccessedTime(Timestamp.from(Instant.now()));
            pageObject.setPageTypeCode(CrawlerConstants.PAGE_TYPE_HTML);

            // handle this as html page
            try {
                webDriver.get(correctedString);
                pageObject.setHttpStatusCode(200);
            } catch (TimeoutException e) {
                pageObject.setHtmlContent("ERROR");
                pageObject.setHttpStatusCode(404);
                try {
                    pageRepository.saveAndFlush(pageObject);
                } catch (Exception ignored) {
                    logger.error("Error while saving timed out page");
                }

                logger.error("Timeout when trying to access page " + correctedString + ".");
                continue;
            }

            try {
                pageObject = pageRepository.saveAndFlush(pageObject);
            } catch (Exception e) {
                logger.error("Error while saving intermediate page");
                continue;
            }

            // parse document
            Document doc;
            try {
                pageObject.setHtmlContent(webDriver.getPageSource());
                pageObject = pageRepository.saveAndFlush(pageObject);

                doc = Jsoup.parse(webDriver.getPageSource(), correctedString);
            } catch (Exception e) {
                logger.error("Error while retrieving and parsing page source: " + correctedString);

                pageObject.setHtmlContent("ERROR");
                pageObject.setHttpStatusCode(404);

                try {
                    pageObject = pageRepository.saveAndFlush(pageObject);
                } catch (Exception ignored) {
                    logger.error("Error while saving unparsable page");
                }
                continue;
            }

            // extract images
            handleImages(doc, correctedString, webDriver, pageObject);

            // delay
            long delay = robotRules.getCrawlDelay() > 0 ? robotRules.getCrawlDelay() : 4000L;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.error("Error while delaying worker.");
            }

            // if we have 100k pages, don't add more
            if (!frontierHandler.isAddMorePages()) {
                continue;
            }

            // collect links
            Set<String> collected = new HashSet<>();
            collected.addAll(extractLinkTagItems(doc, webDriver.getCurrentUrl()));
            collected.addAll(extractOnClickITems(doc, webDriver.getCurrentUrl()));

            // filter so only .gov.si pages get collected
            collected = StreamEx.of(collected).filter(o -> o.contains("gov.si")).toSet();

            // check if we found some existing pages
            Map<String, Page> existingPagesByUrl;
            if (collected.isEmpty()) {
                existingPagesByUrl = new HashMap<>();
            } else {
                existingPagesByUrl = StreamEx.of(pageRepository.findPagesByUrl(collected))
                        .toMap(Page::getUrl, Function.identity());
            }

            List<Link> newLinks = new ArrayList<>();
            for (String newFrontierUrl : collected) {
                if (StreamEx.of(CrawlerConstants.SEED_URLS).noneMatch(o -> newFrontierUrl.toLowerCase().contains(o))) {
                    continue;
                }

                Page frontierPage;

                // save and retrieve page for newFrontierUrl
                if (!existingPagesByUrl.containsKey(newFrontierUrl)) {
                    frontierPage = new Page();
                    frontierPage.setUrl(newFrontierUrl);
                    frontierPage.setAccessedTime(Timestamp.from(Instant.now()));
                    frontierPage.setPageTypeCode(CrawlerConstants.PAGE_TYPE_FRONTIER);

                    try {
                        frontierPage = pageRepository.saveAndFlush(frontierPage);
                    } catch (Exception e) {
                        logger.error("Error while saving a duplicated page");
                        // if we have a duplicate url, just fetch the saved one
                        frontierPage = pageRepository.findByUrl(newFrontierUrl);
                    }
                } else {
                    frontierPage = existingPagesByUrl.get(newFrontierUrl);
                }

                // try to link the pages
                try {
                    newLinks.add(new Link(pageObject, frontierPage));
                } catch (Exception e) {
                    logger.error("Error while linking pages.");
                }
            }

            try {
                linkRepository.saveAll(newLinks);
                linkRepository.flush();
            } catch (Exception e) {
                logger.error("Error while saving links.");
            }
        }

        logger.info("Logger " + threadID + " hasn't found any new url to visit");
    }

    private void handleImages(Document doc, String correctedString, WebDriver webDriver, Page pageObject) {
        List<String> imageElements = StreamEx.of(doc.getElementsByTag("img")).map(o -> o.attr("src")).toImmutableList();

        for (String imageSrc : imageElements) {
            if (imageSrc.contains("data:")) {
                String[] split = imageSrc.replace("data:", "").split(";");
                if (split.length < 3) {
                    continue; // invalid data
                }

                Image image = new Image();
                image.setPage(pageObject);
                image.setAccessedTime(Timestamp.from(Instant.now()));
                image.setFilename("Image");
                image.setContentType(split[0]);
                image.setData(split[2].getBytes());

                try {
                    imageRepository.saveAndFlush(image);
                } catch (Exception e) {
                    logger.error("Error saving image: " + imageSrc);
                }
            } else {
                URL urlObject;
                try {
                    urlObject = new URL(new URL(correctedString), imageSrc);
                } catch (MalformedURLException e) {
                    logger.error("Error parsing image: " + imageSrc);
                    continue;
                }

                String[] split = urlObject.toString().split("/");
                if (split.length < 2) {
                    continue;
                }

                Image image = new Image();
                image.setAccessedTime(Timestamp.from(Instant.now()));
                image.setData(new byte[0]);
                image.setFilename("Image");
                image.setContentType("Image");
                image.setPage(pageObject);

                try {
                    imageRepository.saveAndFlush(image);
                } catch (Exception e) {
                    logger.error("Error saving image: " + imageSrc);
                }
            }
        }
    }

    private String getDataType(String correctedString) {
        correctedString = correctedString.toLowerCase();

        if (correctedString.contains(".docx")) {
            return CrawlerConstants.DATA_TYPE_DOCX;
        } else if (correctedString.contains(".doc")) {
            return CrawlerConstants.DATA_TYPE_DOC;
        } else if (correctedString.contains(".pdf")) {
            return CrawlerConstants.DATA_TYPE_PDF;
        } else if (correctedString.contains(".pptx")) {
            return CrawlerConstants.DATA_TYPE_PPTX;
        } else if (correctedString.contains(".ppt")) {
            return CrawlerConstants.DATA_TYPE_PPT;
        }

        return null;
    }

    private boolean isDocument(String targetUrl) {
        targetUrl = targetUrl.toLowerCase();
        return StreamEx.of(CrawlerConstants.BINARY_FILES).anyMatch(targetUrl::contains);
    }

    private Set<String> extractLinkTagItems(Document document, String url) {
        Set<String> hrefs = StreamEx.of(document.getElementsByTag("a")).map(o -> o.attr("href")).toSet();
        Set<String> result = new HashSet<>();

        for (String target : hrefs) {
            if (target.startsWith("#") || target.startsWith("javascript")) {
                continue;
            }

            try {
                URL urlObject = new URL(new URL(url), target);
                result.add(urlObject.toString());
            } catch (MalformedURLException e) {
                logger.error("Error while parsing url '" + url + "'.");
            }
        }

        return result;
    }

    private Set<String> extractOnClickITems(Document document, String url) {
        Set<String> onClicks = StreamEx.of(document.getElementsByAttribute("onclick"))
                .map(o -> o.attr("onclick"))
                .filter(o -> o.contains("location.href") || o.contains("document.location"))
                .toSet();

        Set<String> result = new HashSet<>();
        for (String target : onClicks) {
            if (target.startsWith("#") || target.startsWith("javascript")) {
                continue;
            }

            String[] split = target.split("=");
            if (split.length != 2) {
                continue;
            }

            // clear narekovaji and whitespace
            String onClickLocation = split[1]
                    .replace("\"", "")
                    .replace("'", "")
                    .replaceAll("\\s", "");

            try {
                URL urlObject = new URL(new URL(url), onClickLocation);
                result.add(urlObject.toString());
            } catch (MalformedURLException e) {
                logger.error("Error while parsing url '" + onClickLocation + "'.");
            }

        }

        return result;
    }
}
