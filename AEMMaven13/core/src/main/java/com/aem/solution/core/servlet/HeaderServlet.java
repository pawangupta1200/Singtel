package com.aem.solution.core.servlet;
import com.aem.solution.core.models.HeaderModel;
import com.aem.solution.core.services.HeaderService;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "= Header JSON renderer Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.resourceTypes=" + "sling/servlet/default",
        "sling.servlet.selectors=" + "header", "sling.servlet.extensions=" + "json" })
public class HeaderServlet extends SlingSafeMethodsServlet {

    @Reference
    private ResourceResolverFactory resolverFactory;

    ResourceResolver resolver = null;



    private static final long serialVersionUID = 2598426539166789516L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int limit = -1;
    HeaderModel pageTree = null;
    private int counter = 0;
    List<String> includePropertiesList = null;
    boolean tidy = false;

    @Reference
    private HeaderService headerService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws IOException {
        try {

            HashMap<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "readservice"); //readService is my System User.
            resolver = resolverFactory.getServiceResourceResolver(param);
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-Type", "application/json");
            includePropertiesList = covertArrayToList(headerService.getIncludedProperties());
            counter = 0;
            limit = headerService.getLimit();

            Resource currentResource = req.getResource();
            Page currentPage;
            tidy = hasSelector(req, "tidy");

            if (resourceNotExists(currentResource)) {
                throw new ResourceNotFoundException("No data to render.");
            }
            currentPage = getCurrentResource(currentResource);
            pageTree = new HeaderModel(currentPage.getName(), getFilteredValueMap(currentPage));
            collectChild(pageTree, currentPage, req, null, counter);
            String jsonString =  getJsonString(pageTree);
            writeDataToDAM(jsonString, resolver);
            resp.getWriter().write(jsonString);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resp.getWriter().close();
        }
    }

    private Page getCurrentResource(Resource currentResource) {
        Page currentPage = currentResource.adaptTo(Page.class);
        return currentPage;
    }

    private boolean resourceNotExists(Resource resource) {
        return ResourceUtil.isNonExistingResource(resource)
                || ResourceUtil.isNonExistingResource(resource.getChild(NameConstants.NN_CONTENT));
    }

    private void collectChild(HeaderModel pageTree, Page page, SlingHttpServletRequest req, String prefix, int counter) {
        try {
            if (counter <= limit || limit < 0) {
                Iterator<Page> children = page.listChildren();
                while (children.hasNext()) {
                    Page childPage =  children.next();
                    HeaderModel item = pageTree.addChild(childPage.getName(), getFilteredValueMap(childPage));

                    Iterator<Page> it = page.listChildren();
                    if (it.hasNext()) {
                        collectChild(item, it.next(), req, null, counter+1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> getFilteredValueMap(Page childPage) {
        HashMap<String, Object> propMap = new HashMap<String, Object>();
        try {
            for (Entry<String, Object> e : childPage.getProperties().entrySet()) {
                String key = e.getKey();
                if (includePropertiesList.contains(key)) {
                    propMap.put("pageTitle", childPage.getTitle());
                    propMap.put("pagePath", childPage.getPath());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propMap;
    }


    private String getJsonString(HeaderModel pageTree) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(pageTree).replaceAll(",\"childnodes\":\\[\\]", "");
        if (tidy) {
            gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            JsonElement jsonElement = new JsonParser().parse(json);
            json = gson.toJson(jsonElement);
        }

        return json;
    }

    private List<String> removeDuplicates(List<String> props) {
        List<String> newList = new ArrayList<String>();
        for (String ele : props) {
            if (!newList.contains(ele)) {
                newList.add(ele.trim());
            }
        }
        return newList;
    }

    private List<String> covertArrayToList(String[] props) {
        List<String> list = new ArrayList<>();
        list = Arrays.asList(props);
        list = removeDuplicates(list);
        return list;
    }


    protected boolean hasSelector(SlingHttpServletRequest req, String selectorToCheck) {
        for (String selector : req.getRequestPathInfo().getSelectors()) {
            if (selectorToCheck.equals(selector)) {
                return true;
            }
        }
        return false;
    }


    private void writeDataToDAM(String jsonString, ResourceResolver resolver){

        InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes());
        //we are sending the JSON data as a String.
       AssetManager assetMgr = resolver.adaptTo(AssetManager.class);
       try {
           assetMgr.createAsset("/content/dam/AEMMaven13/navigation.json",inputStream,"application/json",true);
       }
       catch (Exception e)
       {
           e.getMessage();
       }


    }



}