/*
 *    Copyright (C) 2013 Codenvy.
 *
 */
package com.codenvy.analytics.metrics;

/**
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public enum MetricType {
    WORKSPACES_CREATED,
    WORKSPACES_DESTROYED,
    TOTAL_WORKSPACES,
    ACTIVE_WORKSPACES,
    PERCENT_ACTIVE_WORKSPACES,
    PERCENT_INACTIVE_WORKSPACES,
    USERS_CREATED,
    USERS_CREATED_PROJECTS,
    PERCENT_USERS_CREATED_PROJECTS,
    USERS_DESTROYED,
    TOTAL_USERS,
    ACTIVE_USERS,
    PERCENT_ACTIVE_USERS,
    PERCENT_INACTIVE_USERS,
    USERS_ADDED_TO_WORKSPACE,
    PERCENT_USERS_ADDED_TO_WORKSPACE_FROM_WEBSITE,
    PERCENT_USERS_ADDED_TO_WORKSPACE_FROM_INVITE,
    USERS_SSO_LOGGED_IN,
    PERCENT_USERS_SSO_LOGGED_IN_USING_GOOGLE,
    PERCENT_USERS_SSO_LOGGED_IN_USING_GITHUB,
    PERCENT_USERS_SSO_LOGGED_IN_USING_FORM,
    PRODUCT_USAGE_TIME,
    PROJECTS_CREATED,
    BUILT_PROJECTS,
    PERCENT_BUILT_PROJECTS,
    PROJECTS_DESTROYED,
    TOTAL_PROJECTS,
    ACTIVE_PROJECTS,
    PERCENT_ACTIVE_PROJECTS,
    PERCENT_INACTIVE_PROJECTS,
    PROJECT_CREATED_TYPES,
    PERCENT_PROJECT_TYPE_JAVA_JAR,
    PERCENT_PROJECT_TYPE_JAVA_WAR,
    PERCENT_PROJECT_TYPE_JAVA_JSP,
    PERCENT_PROJECT_TYPE_JAVA_SPRING,
    PERCENT_PROJECT_TYPE_PHP,
    PERCENT_PROJECT_TYPE_PYTHON,
    PERCENT_PROJECT_TYPE_JAVASCRIPT,
    PERCENT_PROJECT_TYPE_RUBY,
    PERCENT_PROJECT_TYPE_MMP,
    PERCENT_PROJECT_TYPE_GROOVY,
    PERCENT_PROJECT_TYPE_OTHERS,
    PAAS_DEPLOYEMNT_TYPES,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_AWS,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_APPFOG,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_CLOUDBESS,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_CLOUDFOUNDRY,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_GAE,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_HEROKU,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_OPENSHIFT,
    PERCENT_PAAS_DEPLOYEMNT_TYPE_LOCAL,
    JREBEL_ELIGIBLE,
    JREBEL_USAGE,
    PERCENT_JREBEL_USAGE,
    INVITATIONS_SENT,
    PERCENT_INVITATIONS_ACTIVATED
}
