package si.kozelj.webcrawler.util;

public class Util {
    public static String prependHttp(String url) {
        return url.startsWith("http") ? url : "http://" + url;
    }
}
