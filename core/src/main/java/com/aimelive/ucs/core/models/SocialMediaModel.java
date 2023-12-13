package com.aimelive.ucs.core.models;

import com.aimelive.ucs.core.beans.SocialLinkBean;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialMediaModel {
    private static final Logger logger = LoggerFactory.getLogger(SocialMediaModel.class);

    @Self
    Resource resource;

    private List<SocialLinkBean> socialLinks = new ArrayList<>();

    @Inject
    private String text1;

    @Inject
    private String text2;

    @PostConstruct
    protected void init() {

        Resource navLinkResource = resource.getChild("socialMedia");
        if (navLinkResource == null) {
            logger.debug("=============================Social link is null....=============================");
            return;
        }

        for (Resource socialLink : navLinkResource.getChildren()) {

            ValueMap valueMap = socialLink.getValueMap();
            SocialLinkBean navbarLinkBean = new SocialLinkBean(
                    valueMap.get("icon", String.class),
                    valueMap.get("url", String.class));

            if (StringUtils.isNotBlank(navbarLinkBean.getIcon())) {
                socialLinks.add(navbarLinkBean);
            }
        }

        logger.debug("=============================WE HAVE {} SOCIAL LINKS ===================================",
                socialLinks.size());
    }

    public List<SocialLinkBean> getSocialLinks() {
        return socialLinks;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }
}
