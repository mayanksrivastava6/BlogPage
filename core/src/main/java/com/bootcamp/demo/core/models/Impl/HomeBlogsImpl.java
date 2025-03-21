package com.bootcamp.demo.core.models.Impl;

import aQute.bnd.header.Attrs;
import com.bootcamp.demo.core.models.HomeBlog;
import com.bootcamp.demo.core.models.PublishBlog;
import com.bootcamp.demo.core.services.DefaultBlogsService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = HomeBlog.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomeBlogsImpl implements HomeBlog {
    private static final Logger LOG = LoggerFactory.getLogger(HomeBlogsImpl.class);
    private static final String DEFAULT_IMAGE = "/content/dam/images.jpeg"; // Default fallback image

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private DefaultBlogsService defaultBlogsService;
    int numberOfBlogs = 0;

    @ValueMapValue
    private String parentPath;

    private PageManager getPageManager() {
        if (resourceResolver != null) {
            return resourceResolver.adaptTo(PageManager.class);
        }
        return null;
    }

    @Override
    public List<Blogs> getBlogList() {
        int TotalBlogs = 3;
        List<Blogs> blogsList = new ArrayList<>();

        if (parentPath== null || parentPath.isEmpty()) {
            LOG.warn("Parent Page Path is null or empty!");
            return null;
        }

        PageManager pageManager = getPageManager();
        if (pageManager == null) {
            LOG.error("PageManager is null! Unable to fetch pages.");
            return null;
        }

        Page parentPage = pageManager.getPage(parentPath);

        if (parentPage != null) {
            Iterator<Page> childPages = parentPage.listChildren();
            while (childPages.hasNext() && numberOfBlogs < TotalBlogs) {
                Page childPage = childPages.next();
                LOG.info("Processing child page: {}", childPage.getPath());

                // Fetch Created Date
                Calendar created = childPage.getProperties().get("jcr:created", Calendar.class);
                if (created == null) {
                    LOG.warn("No creation date found for page: {}", childPage.getPath());
                    continue; // Skip this page if creation date is missing
                }

                String heading = childPage.getTitle(); // jcr:title
                String subHeading = childPage.getDescription(); // jcr:description
                LOG.info("Title: {}, Description: {}", heading, subHeading);

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                String formattedDate = sdf.format(created.getTime());

                String image = getImagePath(childPage);
                LOG.info("Final Image Path: {}", image);

                String link = childPage.getPath() + ".html";

                blogsList.add(new Blogs(heading, subHeading, formattedDate, image, link));
                numberOfBlogs++;
            }
        } else {
            LOG.error("Current page is null! Cannot fetch blog list.");
        }
        return blogsList;
    }

    private String getImagePath(Page childPage) {
        String imagePath = DEFAULT_IMAGE;
        String imageResourcePath = "root/responsivegrid/image/file/jcr:content";
        Resource imageResource = childPage.getContentResource(imageResourcePath);

        if (imageResource != null) {
            LOG.info("Image resource found at: {}", imageResource.getPath());
            ValueMap properties = imageResource.getValueMap();

            if (properties.containsKey("jcr:data")) { // Check if actual image binary exists
                imagePath = childPage.getPath() + "/jcr:content/root/responsivegrid/image/file";
                LOG.info("Image found, setting path: {}", imagePath);
            } else {
                LOG.warn("No 'jcr:data' found in image resource: {}", imageResource.getPath());
            }
        } else {
            LOG.warn("Image resource not found at expected path: {}/{}", childPage.getPath(), imageResourcePath);
        }
        return imagePath;
    }
}
