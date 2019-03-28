package si.kozelj.webcrawler.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    public static String prependHttp(String url) {
        return url.startsWith("http") ? url : "http://" + url;
    }

    public static String extractDomainPart(URL url) {
        String domainPart = url.getHost();

        // TODO
        if (domainPart.contains("www.")) {
            domainPart = domainPart.replace("www.", "");
        }

        return domainPart;
    }
}
