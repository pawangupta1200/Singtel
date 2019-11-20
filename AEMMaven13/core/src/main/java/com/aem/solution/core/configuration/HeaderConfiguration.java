package com.aem.solution.core.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Header JSON  Service configuration", description = "Header Service configuration JSON ")
public @interface HeaderConfiguration {


    @AttributeDefinition(
            name = "include properties",
            description = "List of properties to include from JSON response",
            type = AttributeType.STRING
    )
    String[] include_properties() default {"jcr:title"};


    @AttributeDefinition(
            name = "limit",
            description = "Node traversing limit , -1 for no limit",
            type = AttributeType.LONG
    )
    int limit() default 2;
}