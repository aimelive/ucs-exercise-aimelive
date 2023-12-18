package com.aimelive.ucs.core.models;

import com.aimelive.ucs.core.beans.MenuItemBean;
import com.aimelive.ucs.core.beans.NavbarLinkBean;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {
    private static final Logger logger = LoggerFactory.getLogger(HeaderModel.class);

    @Self
    Resource resource;

    private List<MenuItemBean> menuItems = new ArrayList<>();
    private List<NavbarLinkBean> navLinks = new ArrayList<>();

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private String rootPath;

    @PostConstruct
    protected void init() {

        PageManager pageManager;
        pageManager = resourceResolver.adaptTo(PageManager.class);

        if (rootPath != null) {
            Page rootPage = pageManager.getPage(rootPath);
            if (rootPage == null)
                return;

            Iterable<Page> iterable = rootPage::listChildren;

            for (Page child : iterable) {
                if (child.isHideInNav())
                    continue;
                Iterable<Page> subIterable = child::listChildren;
                MenuItemBean menuItemBean = new MenuItemBean();
                menuItemBean.setTitle(child.getTitle());
                menuItemBean.setPath(child.getPath());

                menuItemBean.setName(child.getName());

                List<MenuItemBean> subChildren = new ArrayList<>();

                for (Page subChild : subIterable) {
                    if (subChild.isHideInNav())
                        continue;
                    MenuItemBean subMenuItemBean = new MenuItemBean();

                    subMenuItemBean.setTitle(subChild.getTitle());
                    subMenuItemBean.setPath(subChild.getPath());
                    subMenuItemBean.setName(subChild.getName());

                    subChildren.add(subMenuItemBean);
                }

                menuItemBean.setSubItems(subChildren);
                // if (menuItemBean.getSubItems().size() != 0) {
                // menuItemBean.setPath("#");
                // }
                menuItems.add(menuItemBean);
            }

            for (MenuItemBean output : menuItems) {
                logger.debug("Title {} , Children = {}", output.getTitle(),
                        output.getSubItems().size());
            }
        }

        // NAVIGATION LINKS
        Resource navLinkResource = resource.getChild("navbarLink");
        if (navLinkResource == null) {
            logger.debug("=============================Navbar link is null....=============================");
            return;
        }

        for (Resource navLink : navLinkResource.getChildren()) {
            NavbarLinkBean navbarLinkBean = new NavbarLinkBean();

            ValueMap valueMap = navLink.getValueMap();

            navbarLinkBean.setLabel(valueMap.get("label", String.class));
            navbarLinkBean.setIconClassName(valueMap.get("iconClassName", String.class));
            navbarLinkBean.setElementClass(valueMap.get("elementClass", String.class));
            navbarLinkBean.setLinkUrl(valueMap.get("linkUrl", String.class));
            navbarLinkBean.setTargetBlank(valueMap.get("targetBlank", Boolean.class));

            if (StringUtils.isNotBlank(navbarLinkBean.getLabel())) {
                navLinks.add(navbarLinkBean);
            }
        }

        logger.debug("=============================WE HAVE {} NAV LINKS ===================================",
                navLinks.size());
    }

    public List<MenuItemBean> getMenuItems() {
        return menuItems;
    }

    public List<NavbarLinkBean> getNavLinks() {
        return navLinks;
    }

    public Boolean getIsConfigured() {
        return rootPath != null || !menuItems.isEmpty();
    }

    public String getRootPath() {
        return rootPath;
    }
}