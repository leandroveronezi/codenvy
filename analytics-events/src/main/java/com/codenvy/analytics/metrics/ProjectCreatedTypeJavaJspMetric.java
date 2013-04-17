/*
 *    Copyright (C) 2013 Codenvy.
 *
 */
package com.codenvy.analytics.metrics;

import java.io.IOException;

/**
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public class ProjectCreatedTypeJavaJspMetric extends ValueFromMapMetric {


    ProjectCreatedTypeJavaJspMetric() throws IOException {
        super(MetricType.PERCENT_PROJECT_TYPE_JAVA_JSP, MetricFactory.createMetric(MetricType.PROJECT_CREATED_TYPES), "Servlet/JSP", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return "% Java Jsp";
    }
}
