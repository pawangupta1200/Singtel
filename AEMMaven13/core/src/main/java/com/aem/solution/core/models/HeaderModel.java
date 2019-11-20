package com.aem.solution.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeaderModel {

    private String pageName = null;
    private HashMap<String, Object> properties = null;
    private List<HeaderModel> childPages = new ArrayList<>();

    public HeaderModel(String pageName, HashMap<String, Object> properties) {
        this.pageName = pageName;
        if(!properties.isEmpty()) {
            this.properties = properties;
        }
    }

    public HeaderModel addChild(String pageName, HashMap<String, Object> value) {
        HeaderModel newChild = new HeaderModel(pageName, value);
        childPages.add(newChild);
        return newChild;
    }

}