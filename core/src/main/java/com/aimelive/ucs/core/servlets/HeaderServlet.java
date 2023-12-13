package com.aimelive.ucs.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.Servlet;
import org.osgi.framework.Constants;
import java.util.*;
import com.adobe.granite.ui.components.ds.DataSource;
import org.apache.commons.collections4.iterators.TransformIterator;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "= Header Servlet Dropdown",
        "sling.servlet.resourceTypes=" + "/apps/dropDownIcons"
})
public class HeaderServlet extends SlingSafeMethodsServlet {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) {
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            List<KeyValue> dropDownList = new ArrayList<>();
            dropDownList.add(new KeyValue("icon-contattaci2", "Contact Us", ""));
            dropDownList.add(new KeyValue("icon-calendar", "Calendar", ""));
            dropDownList.add(new KeyValue("icon-estero2", "WorldWide", ""));
            dropDownList.add(new KeyValue("icon-search", "Search", ""));

            @SuppressWarnings("unchecked")
            DataSource ds = new SimpleDataSource(
                    new TransformIterator(
                            dropDownList.iterator(),
                            input -> {
                                KeyValue keyValue = (KeyValue) input;
                                ValueMap vm = new ValueMapDecorator(new HashMap<>());
                                vm.put("value", keyValue.key);
                                vm.put("text", keyValue.value);
                                vm.put("icon", keyValue.icon);
                                return new ValueMapResource(
                                        resourceResolver, new ResourceMetadata(),
                                        JcrConstants.NT_UNSTRUCTURED, vm);
                            }));
            request.setAttribute(DataSource.class.getName(), ds);

        } catch (Exception e) {
            LOGGER.error("Error in Get Drop Down Values", e);
        }
    }

    private class KeyValue {
        private String key;
        private String value;
        private String icon;

        private KeyValue(final String newKey, final String newValue, final String icon) {
            this.key = newKey;
            this.value = newValue;
            this.icon = icon;
        }
    }
}