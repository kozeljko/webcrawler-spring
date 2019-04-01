package si.kozelj.webcrawler;

import com.google.common.collect.Sets;

import java.util.Set;

public class CrawlerConstants {

    public static final String DATA_TYPE_PDF = "PDF";
    public static final String DATA_TYPE_DOC = "DOC";
    public static final String DATA_TYPE_DOCX = "DOCX";
    public static final String DATA_TYPE_PPT = "PPT";
    public static final String DATA_TYPE_PPTX = "PPTX";

    public static final String PAGE_TYPE_HTML = "HTML";
    public static final String PAGE_TYPE_BINARY = "BINARY";
    public static final String PAGE_TYPE_DUPLICATE = "DUPLICATE";
    public static final String PAGE_TYPE_FRONTIER = "FRONTIER";

    public static final Set<String> BINARY_FILES = Sets.newHashSet(".doc", ".pdf", ".docx", ".ppt", ".pptx");

    public static final Set<String> SEED_URLS = Sets.newHashSet("evem.gov.si", "e-uprava.gov.si", "podatki.gov.si", "e-prostor.gov.si");
}
