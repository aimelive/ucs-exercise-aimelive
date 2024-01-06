package com.aimelive.ucs.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimelive.ucs.core.beans.ArticleData;
import com.aimelive.ucs.core.utils.HelperUtils;
import com.day.cq.wcm.api.PageManager;

public class ArticlesImportService {
    private static Logger logger = LoggerFactory.getLogger(ArticlesImportService.class);
    public static final String IMPORT_ARTICLES_PAGE_PATH = "/apps/ucs-exercise-aimelive/components/custom/articles-import";

    public static Map<String, String> createArticlesFromCsv(ResourceResolver resolver, String filePath,
            Date latestExecutionTime, boolean isManual) throws PathNotFoundException, RepositoryException {
        Map<String, String> responseData = new HashMap<>();

        // ----------------------------------------------------------------
        Resource resource = resolver.getResource(filePath);
        if (resource == null) {
            throw new IllegalArgumentException("CSV RESOURCE FILE NOT FOUND!!!");
        }
        Date lastModified = resource.getChild("jcr:content").getValueMap().get("jcr:lastModified", Date.class);

        if (latestExecutionTime != null && lastModified != null && lastModified.before(latestExecutionTime)) {

            throw new IllegalArgumentException("Execution skipped because no modification has made to the file.");
        }

        logger.debug("START");
        Iterator<String[]> csvData = HelperUtils.readFileCSV(resource);

        List<ArticleData> articles = new ArrayList<ArticleData>();

        while (csvData.hasNext()) {
            String[] line = csvData.next();
            ArticleData articleData = HelperUtils.convertToArticleData(line);
            articles.add(articleData);
        }
        //

        logger.debug("RECORD PROCESSING {} ARTICLES", articles.size());

        int skippedArticles = HelperUtils.createPages(resolver.adaptTo(PageManager.class),
                resolver.adaptTo(Session.class), articles,
                resolver.getResource(IMPORT_ARTICLES_PAGE_PATH).adaptTo(Node.class).getNode("jcr:content"), isManual);
        logger.debug("STOP");
        logger.debug("SKIPPED ARTICLES: {} ", skippedArticles);
        logger.debug("CREATED ARTICLES: {} ", articles.size() - skippedArticles);

        responseData.put("skippedArticles", String.valueOf(skippedArticles));
        responseData.put("createdArticles", String.valueOf(articles.size() - skippedArticles));

        return responseData;
    }
}
