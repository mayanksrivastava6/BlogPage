package com.bootcamp.demo.core.models.Impl;

import com.bootcamp.demo.core.models.Heading;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Model(
        adaptables = {SlingHttpServletRequest.class,Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        adapters = Heading.class
)
public class HeadingImpl implements Heading {


    @ValueMapValue
    private String title;

    @ScriptVariable
    private Page currentPage;

    @Override
    public String getTitle() {
        if(currentPage == null) return "Page Not Found";
        return title == null ? currentPage.getProperties().get("jcr:title", String.class) : title;
    }

    @Override
    public String getAuthor() {
        if(currentPage == null) return "Page Not Found";
        return currentPage.getProperties().get("jcr:createdBy", String.class);
    }

    @Override
    public String getDate() {
        if(currentPage == null) return "Page Not Found";
        Calendar created = currentPage.getProperties().get("jcr:created", Calendar.class);
        if (created != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return sdf.format(created.getTime());
        }
        return "No Date Found";
    }
}
