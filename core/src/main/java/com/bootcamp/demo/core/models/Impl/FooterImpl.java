package com.bootcamp.demo.core.models.Impl;

import com.bootcamp.demo.core.models.Footer;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Item.LOG;

@Model(adaptables = Resource.class, adapters = Footer.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterImpl implements Footer{
    @SlingObject
    Resource resourceComp;

    @Override
    public List<Map<String, String>> getMenuItems(){
        List<Map<String, String>>navigationMap= new ArrayList<>();
        Resource links= resourceComp.getChild("actions");

        if(links!=null){
            for(Resource resource : links.getChildren()){
                Map<String, String>linkMap = new HashMap<>();
                linkMap.put("Text", resource.getValueMap().get("text", String.class));
                linkMap.put("Link", resource.getValueMap().get("link", String.class));
                navigationMap.add(linkMap);
                LOG.info("Added link: {} - {}", resource.getValueMap().get("text", String.class), resource.getValueMap().get("link", String.class));

            }
        } else{
            LOG.warn("No actions child found under the resource!");
        }
        return navigationMap;
    }
}