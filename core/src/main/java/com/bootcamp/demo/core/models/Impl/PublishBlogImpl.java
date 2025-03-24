package com.bootcamp.demo.core.models.Impl;
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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

@Model(adaptables= {SlingHttpServletRequest.class,Resource.class},adapters = PublishBlog.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PublishBlogImpl implements PublishBlog {

    private static final Logger LOG = LoggerFactory.getLogger(PublishBlogImpl.class);
    private static final String DEFAULT_IMAGE = "/content/dam/images.jpeg"; // Default fallback image

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private DefaultBlogsService defaultBlogsService;
    int numberOfBlogs = 0;

    @Override
    public List<Blogs> getBlogList() {
        List<Blogs> blogsList = new ArrayList<>();
        // Retrieve request parameters
        String yearParam = request.getParameter("year");
        String monthParam = request.getParameter("month");

        LOG.info("Received request parameters -> Year: {}, Month: {}", yearParam, monthParam);

            if (currentPage != null) {
                Iterator<Page> childPages = currentPage.listChildren();

                while (childPages.hasNext() && numberOfBlogs < defaultBlogsService.getDefault_blogs()) {
                    Page childPage = childPages.next();
                    Calendar created = childPage.getProperties().get("jcr:created", Calendar.class);

                    if (created == null) {
                        LOG.warn("No creation date found for page: {}", childPage.getPath());
                        continue;
                    }

                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);

                    String createdYear = yearFormat.format(created.getTime());
                    String createdMonth = monthFormat.format(created.getTime());

                    if (yearParam != null && monthParam != null &&
                            (!createdYear.equals(yearParam) || !createdMonth.equalsIgnoreCase(monthParam))) {
                        continue;
                    }

                    String heading = childPage.getTitle();
                    String subHeading = childPage.getDescription();
                    String formattedDate = new SimpleDateFormat("MMM dd").format(created.getTime());
                    String image = getImagePath(childPage);
                    String link = childPage.getPath() + ".html";//to access published page we used .html

                    blogsList.add(new Blogs(heading, subHeading, formattedDate, image, link));
                    numberOfBlogs++;
                }
            }
        else {
            LOG.error("Page Manager is null! Cannot fetch blog list.");
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
