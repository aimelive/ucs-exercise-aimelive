package com.aimelive.ucs.core.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimelive.ucs.core.beans.RelatedArticle;
import com.aimelive.ucs.core.utils.HelperUtils;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

@Component(service = { Servlet.class })
@ServiceDescription("Articles Servlet")
@SlingServletPaths(value = { "/bin/servlets/articles" })
public class HashTagServlet extends SlingSafeMethodsServlet {
  private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws ServletException, IOException {
    List<RelatedArticle> articles = new ArrayList<RelatedArticle>();
    try {
      String hashtag = request.getParameter("hashtag");
      String limit = request.getParameter("limit");
      if (hashtag == null)
        throw new IllegalArgumentException("Hashtag is required to continue.");

      ResourceResolver resolver = request.getResourceResolver();
      Session session = resolver.adaptTo(Session.class);
      QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);

      Map<String, String> predicate = new HashMap<String, String>();
      predicate.put("path", "/content/ucs-exercise-aimelive/magazine");
      predicate.put("type", "cq:Page");
      predicate.put("orderby", "@jcr:content/cq:lastModified");
      predicate.put("orderby.sort", "desc");
      predicate.put("tagid", hashtag);
      predicate.put("tagid.property", "jcr:content/cq:tags");
      predicate.put("p.limit", limit != null ? limit : "20");

      Query query = null;

      query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);

      SearchResult result = query.getResult();

      for (Hit hit : result.getHits()) {
        Resource hitresource = hit.getResource();
        Resource resource = hitresource.getChild("jcr:content");
        Date date = resource.getValueMap().get("jcr:created", Date.class);
        String text = resource.getValueMap().get("jcr:title", String.class);
        String[] tags = resource.getValueMap().get("cq:tags", String[].class);

        Resource pictureResource = resource.getChild("cq:featuredimage");
        String picture = null;

        if (pictureResource != null) {
          picture = pictureResource.getValueMap().get("fileReference", String.class);
        }

        if (tags != null && tags.length != 0) {
          RelatedArticle article = new RelatedArticle(Long.toString(hit.getIndex()), text, formatDate(date),
              "#" + HelperUtils.extractStringTag(tags[0]), picture, hit.getPath());
          articles.add(article);
          // articles
          // .add(new ArticleTag(
          // titleA, date, new ArrayList<String>(Arrays.asList(tags)).stream()
          // .map(val -> HelperUtils.extractStringTag(val)).collect(Collectors.toList()),
          // picture, hit.getPath()));
        } else {
          LOGGER.debug("TAGS is null");
        }

      }

    } catch (Exception e) {
      LOGGER.debug("ERROR: HAPPENED: =====  {}", e);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(articles);

    response.setContentType("application/json");
    response.getWriter().write(json);
  }

  private String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy");
    return sdf.format(date);
  }
}
