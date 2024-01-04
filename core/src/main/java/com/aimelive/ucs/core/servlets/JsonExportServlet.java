package com.aimelive.ucs.core.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimelive.ucs.core.beans.ArticleData;
import com.aimelive.ucs.core.utils.HelperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "cq:Page", methods = "GET", extensions = "json", selectors = "export")
public class JsonExportServlet extends SlingSafeMethodsServlet {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            Resource resource = request.getResource().getChild("jcr:content");

            String text = resource.getValueMap().get("articleTitle", String.class);
            String[] tags = resource.getValueMap().get("articleTags", String[].class);
            String picture = resource.getValueMap().get("articlePicture", String.class);
            String description = resource.getValueMap().get("jcr:description", String.class);

            ArticleData articleData = new ArticleData();
            articleData.setTitle(text);
            articleData.setDescription(description);
            articleData.setImage(picture);
            articleData.setTags(Arrays.asList(tags).stream().map(tag -> HelperUtils.extractStringTag(tag))
                    .collect(Collectors.toList()));
            articleData.setPageName((resource.getPath().substring(0, resource.getPath().lastIndexOf("/")) + ".html"));

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(articleData);

            response.setContentType("application/json");
            response.getWriter().write(json);
        } catch (Exception e) {
            logger.debug("ERROR ===== {}", e);
            Map<String, String> responseData = new HashMap<>();

            responseData.put("message", "Something went wrong, please try again later.");
            responseData.put("error", e.toString());

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(responseData);

            response.setContentType("application/json");
            response.getWriter().write(json);
        }
    }
}
