/*
 *    Copyright (C) 2013 Codenvy.
 *
 */
package com.codenvy.analytics.metrics;

import java.io.IOException;

/**
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public class ProjectCreatedTypeMmpMetric extends ValueFromMapMetric {

    ProjectCreatedTypeMmpMetric() throws IOException {
        super(MetricType.PERCENT_PROJECT_TYPE_MMP, MetricFactory.createMetric(MetricType.PROJECT_CREATED_TYPES), "Maven Multi-module", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return "% Maven Multi Project";
    }
}
