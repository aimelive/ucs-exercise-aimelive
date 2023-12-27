package com.aimelive.ucs.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.ModifiableValueMap;
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

    @Inject
    private String duration;

    @Inject
    private Boolean disableAutoplay;

    @PostConstruct
    protected void init() {

        try {
            Iterator<Resource> children = currentResource.listChildren();
            if (children == null)
                return;

            while (children.hasNext()) {
                Resource child = children.next();
                String slideType = child.getValueMap().get("slideType", String.class);
                String imageDesktopPath = child.getValueMap().get("imageDesktopPath", String.class);
                String[] tags = child.getValueMap().get("tags", String[].class);

                if (slideType != null && imageDesktopPath != null) {
                    if (slideType.equalsIgnoreCase("article-slide") && tags != null &&
                            tags.length > 0) {
                        try {
                            child.getValueMap().put("tags", extractStringTags(tags));
                        } catch (Exception e) {
                            logger.debug("SLIDE ERROR" + e);
                            slideResources.add(child);
                            continue;
                        }
                    }
                    slideResources.add(child);
                }
                // logger.debug("Resource: {} and Image: {}", slideType, imageDesktopPath);
            }

            // ORDERING THE SLIDES
            String[] slidesOrder = currentResource.getValueMap().get("slidesOrder", String[].class);

            List<Resource> orderedSlideResources = new ArrayList<Resource>();

            if (slidesOrder != null) {
                Arrays.stream(slidesOrder)
                        .map(String::trim)
                        .filter(slide -> !slide.isEmpty())
                        .map(slide -> slideResources.stream()
                                .filter(resource -> slide.equalsIgnoreCase(resource.getName()))
                                .findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(orderedSlideResources::add);
                slideResources = orderedSlideResources;
            } else {
                logger.debug("Slides Order is null or empty.");
            }
        } catch (Exception e) {
            logger.debug("SLIDE ERROR" + e);
        }
    }

    public List<Resource> getSlides() {
        return slideResources;
    }

    public Integer getDuration() {
        // if (disableAutoplay == true)
        // return null;
        if (duration == null || duration == "0")
            return 4 * 1000;
        return Integer.parseInt(duration) * 1000;
    }

    private String[] extractStringTags(String[] inputs) {
        String[] results = new String[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            if (input.contains(":")) {
                String[] parts = input.split(":");
                results[i] = parts.length > 1 ? capitalizeFirstLetter(parts[1]) : capitalizeFirstLetter(parts[0]);
            } else {
                results[i] = input;
            }
        }

        return results;
    }

    private static String capitalizeFirstLetter(String input) {
        if (input != null && !input.isEmpty()) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        } else {
            return input;
        }
    }
}
