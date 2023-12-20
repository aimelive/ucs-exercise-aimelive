package com.aimelive.ucs.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SliderModel {

    private static final Logger logger = LoggerFactory.getLogger(SliderModel.class);

    @SlingObject
    private Resource currentResource;

    List<Resource> slideResources = new ArrayList<Resource>();

    @PostConstruct
    protected void init() {

        Iterator<Resource> children = currentResource.listChildren();
        if (children == null)
            return;

        while (children.hasNext()) {
            Resource child = children.next();
            String slideType = child.getValueMap().get("slideType", String.class);
            String imageDesktopPath = child.getValueMap().get("imageDesktopPath", String.class);
            if (slideType != null && imageDesktopPath != null) {
                slideResources.add(child);
            }
            // logger.debug("Resource: {} and Image: {}", slideType, imageDesktopPath);
        }

        // ORDERING THE SLIDES
        String[] slidesOrder = currentResource.getValueMap().get("slidesOrder", String[].class);

        List<Resource> orderedSlideResources = new ArrayList<Resource>();

        if (slidesOrder != null) {
            for (String slide : slidesOrder) {
                // logger.debug("Slide: {}", slide);
                for (Resource slideResource : slideResources) {
                    if (slide.equalsIgnoreCase(slideResource.getName())) {
                        orderedSlideResources.add(slideResource);
                    }
                }
            }
            slideResources = orderedSlideResources;
        } else {
            logger.debug("Slides Order is null or empty.");
        }
    }

    public List<Resource> getSlides() {
        return slideResources;
    }
}
