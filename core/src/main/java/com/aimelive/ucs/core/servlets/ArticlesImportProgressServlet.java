package com.aimelive.ucs.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import com.aimelive.ucs.core.services.ArticlesImportService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = { Servlet.class })
@SlingServletPaths(value = { "/bin/servlets/articles/import/progress" })
public class ArticlesImportProgressServlet extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = resolver.getResource(ArticlesImportService.IMPORT_ARTICLES_PAGE_PATH + "/jcr:content");
        Long processedRows = resource.getValueMap().get("processedRows", Long.class);
        Long totalRows = resource.getValueMap().get("totalRows", Long.class);
        Long skippedRows = resource.getValueMap().get("skippedRows", Long.class);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("totalRows", totalRows);
        responseData.put("processedRows", processedRows);
        responseData.put("skippedRows", skippedRows);

        if (processedRows != null && totalRows != null) {
            responseData.put("progress", (int) (((double) processedRows / totalRows) * 100) + "%");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(responseData);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
