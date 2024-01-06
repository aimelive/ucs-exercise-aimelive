package com.aimelive.ucs.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.aimelive.ucs.core.beans.ArticleData;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.text.csv.Csv;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import javax.jcr.Node;
import javax.jcr.Session;

public class HelperUtils {
    private static final String PARENT_PATH = "/content/ucs-exercise-aimelive/magazine/";
    private static final String TEMPLATE_PATH = "/conf/ucs-exercise-aimelive/settings/wcm/templates/article-template";

    public static String extractStringTag(String tag) {
        if (tag.contains("/")) {
            return tag.substring(tag.lastIndexOf("/") + 1);
        }
        return tag.substring(tag.lastIndexOf(":") + 1);
    }

    public static Iterator<String[]> readFileCSV(Resource csvResource) {
        try {
            Asset asset = csvResource.adaptTo(Asset.class);
            Rendition assetRendition = asset.getOriginal();
            InputStream inputStream = assetRendition.getStream();

            Csv csv = new Csv();

            Iterator<String[]> articleIterator = csv.read(inputStream, null);

            return articleIterator;
        } catch (Exception e) {
            return null;
        }
    }

    public static int createPages(PageManager pageManager, Session session, List<ArticleData> articles,
            Node importArticlesNode, boolean isManual) {
        try {
            if (isManual) {
                importArticlesNode.setProperty("totalRows", articles.size());
                importArticlesNode.setProperty("processedRows", 0);
                importArticlesNode.setProperty("skippedRows", 0);
                session.save();
            }
            int skippedArticles = 0;
            int processedRows = 0;

            for (ArticleData articleData : articles) {
                if (isManual) {
                    importArticlesNode.setProperty("processedRows", ++processedRows);
                    importArticlesNode.setProperty("skippedRows", skippedArticles);
                    session.save();
                }

                Page existPage = pageManager.getPage(PARENT_PATH + articleData.getPageName());
                if (existPage != null) {
                    ++skippedArticles;
                    continue;
                }
                Page newPage = pageManager.create(PARENT_PATH, null, TEMPLATE_PATH, articleData.getPageName());
                Node pageNode = newPage.adaptTo(Node.class).getNode("jcr:content");
                pageNode.setProperty("jcr:title", articleData.getTitle());
                pageNode.setProperty("jcr:description", articleData.getDescription());
                pageNode.setProperty("articleTitle", articleData.getTitle());
                pageNode.setProperty("articlePicture", articleData.getImage());
                pageNode.setProperty("articleTags", articleData.getTags().toArray(new String[0]));
                pageNode.setProperty("articleDate", Calendar.getInstance());
                session.save();
            }
            return skippedArticles;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while creating new page!!!!");
        }

    }

    public static Iterator<String[]> readFileCSVStream(InputStream inputStream) {
        BufferedReader reader = null;
        List<String[]> csvData = new ArrayList<>();

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                csvData.add(values);
            }

        } catch (IOException e) {
            // Handle the exception according to your application's requirements
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return csvData.iterator();
    }

    public static ArticleData convertToArticleData(String[] csvRow) {
        if (csvRow.length < 5) {
            throw new IllegalArgumentException("Invalid CSV row. Expected at least 5 elements.");
        }

        ArticleData articleData = new ArticleData();
        articleData.setTitle(csvRow[0].replaceAll("\"", ""));
        articleData.setDescription(csvRow[1]);
        articleData.setImage(csvRow[2]);
        articleData.setPageName(csvRow[3]);

        // Assuming tags are represented as a comma-separated list in the CSV
        String[] tagsArray = csvRow[4].split(",");
        List<String> tagsList = Arrays.asList(tagsArray);
        articleData.setTags(tagsList);

        return articleData;
    }
}
