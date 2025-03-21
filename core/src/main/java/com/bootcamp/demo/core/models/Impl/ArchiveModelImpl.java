package com.bootcamp.demo.core.models.Impl;

import com.bootcamp.demo.core.models.Archive;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = Archive.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArchiveModelImpl implements Archive {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveModelImpl.class);

    @ValueMapValue
    private String parentPagePath;

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    private PageManager getPageManager() {
        if (resourceResolver != null) {
            return resourceResolver.adaptTo(PageManager.class);
        }
        return null;
    }

    @Override
    public Map<String, ArchiveDate> getFormattedDates() {
        Map<String, ArchiveDate> formattedDates = new HashMap<>();

        if (parentPagePath == null || parentPagePath.isEmpty()) {
            LOG.warn("Parent Page Path is null or empty!");
            return null;
        }

        PageManager pageManager = getPageManager();
        if (pageManager == null) {
            LOG.error("PageManager is null! Unable to fetch pages.");
            return null;
        }

        Page parentPage = pageManager.getPage(parentPagePath);
        if (parentPage == null) {
            LOG.error("Parent page not found at path: {}", parentPagePath);
            return null;
        }

        Iterator<Page> childPages = parentPage.listChildren();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);

        while (childPages.hasNext()) {
            Page childPage = childPages.next();
            Calendar created = childPage.getProperties().get("jcr:created", Calendar.class);

            if (created != null) {
                String formatedDate= sdf.format(created.getTime());
                String year = yearFormat.format(created.getTime());
                String month = monthFormat.format(created.getTime());
                ArchiveDate date = new ArchiveDate(year, month);

                formattedDates.put(formatedDate, date);
            }
        }

        return formattedDates;
    }

    @Override
    public String getParentPath() {
        return parentPagePath;
    }

    public static class ArchiveDate {
        private final String year;
        private final String month;

        public ArchiveDate(String year, String month) {
            this.year = year;
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public String getMonth() {
            return month;
        }
    }
}