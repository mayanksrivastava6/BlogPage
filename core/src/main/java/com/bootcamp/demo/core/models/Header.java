package com.bootcamp.demo.core.models ;

import java.util.List;
import java.util.Map;

public interface Header{
    String getLogoPath();

    String getWebsiteName();

    List<Map<String,String>>getMenuItems();

}
