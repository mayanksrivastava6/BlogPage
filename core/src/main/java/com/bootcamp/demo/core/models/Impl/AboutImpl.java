package com.bootcamp.demo.core.models.Impl;

import com.bootcamp.demo.core.models.About;
import com.bootcamp.demo.core.models.Heading;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;

@Model(
        adaptables = {SlingHttpServletRequest.class, Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        adapters = About.class
)
public class AboutImpl implements About {

    @ScriptVariable
    private Page currentPage;

    @Override
    public String getDescription() {
        if(currentPage== null) return "Page Not Found";
        return currentPage.getProperties().get("jcr:description", String.class) ;
    }
}
