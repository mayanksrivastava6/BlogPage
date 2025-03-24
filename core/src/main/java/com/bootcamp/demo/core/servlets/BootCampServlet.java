package com.bootcamp.demo.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import jdk.internal.org.jline.utils.Log;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component(service = Servlet.class, name = "BootCamp JSON Servlet", property = {
        org.osgi.framework.Constants.SERVICE_DESCRIPTION + "=Demo Servlet for Bootcamp",
        "sling.servlet.resourceTypes=" + "bootcamp/components/page",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.selectors=" + "test",
        "sling.servlet.extensions=" + "json"

})
public class BootCampServlet extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException, IOException{
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if(pageManager == null){
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Page Manager is null");
            return;
        }
        Resource resource = request.getResource();
        Page currentPage = pageManager.getContainingPage(resource);

        if (currentPage != null) {
            JSONArray jsonArray = new JSONArray();
            Iterator<Page> pageIterator = currentPage.listChildren();
            while (pageIterator.hasNext()) {
                Page childPage = pageIterator.next();
                jsonArray.put(getPagePropertiesAsJSON(childPage));
            }

            response.setContentType("application/json");
            response.getWriter().write(jsonArray.toString());
        } else {
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Page not found.");
        }
    }
    private JSONObject getPagePropertiesAsJSON(Page page) {
        JSONObject pageJSON = new JSONObject();
        try {
            pageJSON.put("path", page.getPath());
            pageJSON.put("title", page.getTitle());
            pageJSON.put("name", page.getName());
            pageJSON.put("description", page.getDescription());

            // Get JCR properties of the page
            Resource pageContentResource = page.getContentResource();
            if (pageContentResource != null) {
                Map<String, Object> pageProperties = pageContentResource.getValueMap();

                // Convert JCR properties into JSON format
                for (Map.Entry<String, Object> entry : pageProperties.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    pageJSON.put(key, value.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();  // Handle JSONException
        }
        return pageJSON;
    }
}
