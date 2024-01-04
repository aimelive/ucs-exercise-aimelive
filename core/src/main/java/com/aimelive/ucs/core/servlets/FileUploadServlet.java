package com.aimelive.ucs.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimelive.ucs.core.beans.ArticleData;
import com.aimelive.ucs.core.utils.HelperUtils;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/servlets/file-upload" })
public class FileUploadServlet extends SlingAllMethodsServlet {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> responseData = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Part filePart = request.getPart("file");
            if (filePart != null) {
                if (!filePart.getContentType().equals("text/csv")) {
                    throw new IllegalArgumentException("Only CSV files are supported");
                }
                AssetManager assetManager = request.getResourceResolver().adaptTo(AssetManager.class);
                Asset fileAsset = assetManager.createAsset(
                        "/content/dam/ucs-exercise-aimelive/uploads/" + extractFileName(filePart),
                        filePart.getInputStream(), filePart.getContentType(), true);

                // responseData.put("filename", fileAsset.getName());
                responseData.put("filepath", fileAsset.getPath());
                // responseData.put("contentType", fileAsset.getMimeType());
                logger.debug("START");
                ResourceResolver resolver = request.getResourceResolver();
                Iterator<String[]> csvData = HelperUtils.readFileCSV(resolver.getResource(fileAsset.getPath()));

                List<ArticleData> articles = new ArrayList<ArticleData>();

                while (csvData.hasNext()) {
                    String[] line = csvData.next();
                    ArticleData articleData = HelperUtils.convertToArticleData(line);
                    articles.add(articleData);
                }
                logger.debug("RECORD PROCESSING");
                int skippedArticles = HelperUtils.createPages(resolver.adaptTo(PageManager.class),
                        resolver.adaptTo(Session.class), articles);
                logger.debug("STOP");
                logger.debug("SKIPPED ARTICLES: {} ", skippedArticles);
                logger.debug("CREATED ARTICLES: {} ", articles.size() - skippedArticles);

                // Creating Pages

                // response.setContentType("application/json");
                // response.getWriter().write(objectMapper.writeValueAsString(articles));
                // return;
                responseData.put("skippedArticles", String.valueOf(skippedArticles));
                responseData.put("createdArticles", String.valueOf(articles.size() - skippedArticles));
            } else {
                throw new IllegalArgumentException("File field is required");
            }
        } catch (Exception e) {
            logger.debug("ERROR ===== {}", e);

            responseData.put("message", "Something went wrong, please try again later.");
            responseData.put("error", e.getMessage());
        }

        String json = objectMapper.writeValueAsString(responseData);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }
}
