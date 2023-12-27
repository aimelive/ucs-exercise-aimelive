package com.aimelive.ucs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimelive.ucs.core.beans.ArticleTagBean;
import com.aimelive.ucs.core.utils.HelperUtils;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HashTagModel {

    private static final Logger logger = LoggerFactory.getLogger(HashTagModel.class);

    @Inject
    private List<String> tags;

    @PostConstruct
    protected void init() {
        logger.debug("HASTAG INITIALIZATION");
    }

    public List<ArticleTagBean> getTags() {
        List<ArticleTagBean> articleTags = new ArrayList<ArticleTagBean>();
        if (tags == null) {
            return articleTags;
        }
        for (String tag : tags) {
            articleTags.add(new ArticleTagBean(tag, HelperUtils.extractStringTag(tag)));
        }
        return articleTags.stream().limit(4).collect(Collectors.toList());
    }

}
