package com.aem.solution.core.business;

import com.aem.solution.core.configuration.HeaderConfiguration;
import com.aem.solution.core.services.HeaderService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;


@Component
@Designate(ocd = HeaderConfiguration.class)
public class HeaderImpl implements HeaderService {

    private String[] includeProperties=null;
    private int limit = 0;

    @Activate
    @Modified
    public void activate(HeaderConfiguration headerConfiguration) {
        includeProperties=headerConfiguration.include_properties();
        limit= headerConfiguration.limit();
    }

    @Override
    public String[] getIncludedProperties() {
        return includeProperties;
    }


    @Override
    public int getLimit() {
        return limit;
    }

}

