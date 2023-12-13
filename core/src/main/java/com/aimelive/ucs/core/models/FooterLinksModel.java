package com.aimelive.ucs.core.models;

import org.apache.sling.models.annotations.Model;

import com.aimelive.ucs.core.beans.FooterLinkBean;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterLinksModel {
    private static final Logger logger = LoggerFactory.getLogger(FooterLinksModel.class);

    @Self
    Resource resource;

    private List<FooterLinkBean> footerLinks = new ArrayList<>();

    @PostConstruct
    protected void init() {

        Resource navLinkResource = resource.getChild("footerLink");
        if (navLinkResource == null) {
            logger.debug("=============================Footer link is null....=============================");
            return;
        }

        for (Resource navLink : navLinkResource.getChildren()) {

            ValueMap valueMap = navLink.getValueMap();
            FooterLinkBean navbarLinkBean = new FooterLinkBean(
                    valueMap.get("label", String.class),
                    valueMap.get("url", String.class),
                    valueMap.get("newTab", Boolean.class) == true ? "_blank" : null);

            if (StringUtils.isNotBlank(navbarLinkBean.getLabel())) {
                footerLinks.add(navbarLinkBean);
            }
        }

        logger.debug("=============================WE HAVE {} FOOTER LINKS ===================================",
                footerLinks.size());
    }

    public List<FooterLinkBean> getFooterLinks() {
        return footerLinks;
    }
}
