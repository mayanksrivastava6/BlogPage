package com.bootcamp.demo.core.models.Impl;

import com.adobe.aemds.guide.utils.GuideValueMapResource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import com.bootcamp.demo.core.models.Header;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.*;

@Model(adaptables= Resource.class, adapters = Header.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderImpl implements Header {

    @ValueMapValue
    private String logoPath;

    @ValueMapValue
    private String websiteName;

    @ChildResource(name = "menuItems")
    private List<Resource> menuItemsResource;

    @Override
    public String getLogoPath() {
        return logoPath;
    }

    @Override
    public String getWebsiteName() {
        return websiteName;
    }


    @SlingObject
    Resource componentResource;

    @Override
    public List<Map<String, String>>getMenuItems(){
        List<Map<String, String>>navigationMap= new ArrayList<>();
        Resource links= componentResource.getChild("actions");

        if(links!=null){
            for(Resource resource : links.getChildren()){
                Map<String, String>linkMap = new HashMap<>();
                linkMap.put("Text", resource.getValueMap().get("text", String.class));
                linkMap.put("Link", resource.getValueMap().get("link", String.class));
                navigationMap.add(linkMap);
            }
        }
        return navigationMap;
    }
}
