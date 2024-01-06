package com.aimelive.ucs.core.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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

import com.aimelive.ucs.core.services.ArticlesImportService;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/servlets/file-upload" })
public class FileUploadServlet extends SlingAllMethodsServlet {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String CSV_FILE = "/content/dam/ucs-exercise-aimelive/uploads/articles.csv";

    private Date lastExecutionTime;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> responseData = new HashMap<>();

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

                ResourceResolver resolver = request.getResourceResolver();

                Map<String, String> responseArticles = ArticlesImportService.createArticlesFromCsv(resolver,
                        fileAsset.getPath(), lastExecutionTime, true);

                responseData.put("skippedArticles", responseArticles.get("skippedArticles"));
                responseData.put("createdArticles", responseArticles.get("createdArticles"));

            } else {
                ResourceResolver resolver = request.getResourceResolver();

                Map<String, String> responseArticles = ArticlesImportService.createArticlesFromCsv(resolver,
                        CSV_FILE, lastExecutionTime, false);

                responseData.put("skippedArticles", responseArticles.get("skippedArticles"));
                responseData.put("createdArticles", responseArticles.get("createdArticles"));
            }
            lastExecutionTime = new Date();
            responseData.put("latestExecutionTime", lastExecutionTime.toString());
        } catch (Exception e) {
            logger.debug("ERROR ===== {}", e);

            responseData.put("message", "Something went wrong, please try again later.");
            responseData.put("error", e.getMessage());
        }
        ObjectMapper objectMapper = new ObjectMapper();
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
