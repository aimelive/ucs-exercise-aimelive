package com.aimelive.ucs.core.models;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import com.day.cq.wcm.api.Page;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Model(adaptables = { SlingHttpServletRequest.class,
        Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ReferenceModel {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceModel.class);

    @Inject
    public Page currentPage;

    private static final String HOME_PAGE_TEMPLATE_PATH = "/conf/ucs-exercise-aimelive/settings/wcm/templates/unicredit-page-template";
    private static String HEADER_PATH = "/jcr:content/root/header";
    private static String FOOTER_PATH = "/jcr:content/root/footer";

    public String getHeaderRef() {
        try {
            logger.error("CURRENT PAGE HEADER REF: {}", currentPage.getPath());
            Page home = getHomePage(currentPage);

            return home.getPath() + HEADER_PATH;
        } catch (Exception e) {
            return "Invalid Reference";
        }
    }

    public String getFooterRef() {
        return getHomePage(currentPage).getPath() + FOOTER_PATH;
    }

    public static boolean isHomePage(Page page) {
        if (page != null && page.getTemplate() != null) {
            String templatePath = page.getTemplate().getPath();
            return HOME_PAGE_TEMPLATE_PATH.equalsIgnoreCase(templatePath);
        } else {
            return false;
        }
    }

    public static Page getHomePage(Page currentPage) {
        while (!isHomePage(currentPage)) {
            currentPage = currentPage.getParent();
            if (currentPage == null) {
                return null;
            }
        }
        return currentPage;
    }
}
