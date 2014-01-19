/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics.pig.scripts;

import com.codenvy.analytics.BaseTest;
import com.codenvy.analytics.Utils;
import com.codenvy.analytics.datamodel.SetValueData;
import com.codenvy.analytics.datamodel.StringValueData;
import com.codenvy.analytics.datamodel.ValueData;
import com.codenvy.analytics.metrics.Metric;
import com.codenvy.analytics.metrics.MetricFilter;
import com.codenvy.analytics.metrics.Parameters;
import com.codenvy.analytics.metrics.sessions.factory.CreatedFactoriesSet;
import com.codenvy.analytics.pig.scripts.util.Event;
import com.codenvy.analytics.pig.scripts.util.LogGenerator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class TestFactoryCreatedList extends BaseTest {

    private Map<String, String> params;

    @BeforeClass
    public void init() throws Exception {
        params = Utils.newContext();

        List<Event> events = new ArrayList<>();
        events.add(Event.Builder
                        .createFactoryCreatedEvent("ws1", "anonymoususer_1", "project1", "type1", "repo1", "factory1",
                                                   "", "").withDate("2013-01-01").withTime("13:00:00").build());
        events.add(Event.Builder
                        .createFactoryCreatedEvent("ws1", "anonymoususer_2", "project1", "type1", "repo1", "factory2",
                                                   "", "").withDate("2013-01-01").withTime("14:00:00").build());
        events.add(Event.Builder
                        .createFactoryCreatedEvent("ws2", "anonymoususer_3", "project1", "type1", "repo1", "factory3",
                                                   "", "").withDate("2013-01-01").withTime("15:00:00").build());
        events.add(Event.Builder
                        .createFactoryCreatedEvent("ws3", "anonymoususer_4", "project1", "type1", "repo1", "factory4",
                                                   "", "").withDate("2013-01-01").withTime("16:00:00").build());
        File log = LogGenerator.generateLog(events);

        Parameters.FROM_DATE.put(params, "20130101");
        Parameters.TO_DATE.put(params, "20130101");
        Parameters.USER.put(params, Parameters.USER_TYPES.ANY.name());
        Parameters.WS.put(params, Parameters.WS_TYPES.ANY.name());
        Parameters.STORAGE_TABLE.put(params, "testfactorycreatedlist");
        Parameters.LOG.put(params, log.getAbsolutePath());

        pigServer.execute(ScriptType.CREATED_FACTORIES, params);
    }

    @Test
    public void testSingleDateFilter() throws Exception {
        Map<String, String> context = Utils.newContext();
        Parameters.FROM_DATE.put(context, "20130101");
        Parameters.TO_DATE.put(context, "20130101");

        Metric metric = new TestSetValueResulted();
        assertEquals(metric.getValue(context),
                     new SetValueData(Arrays.<ValueData>asList(new StringValueData("factory1"),
                                                               new StringValueData("factory2"),
                                                               new StringValueData("factory3"),
                                                               new StringValueData("factory4"))));
    }


    @Test
    public void testSingleUserFilter() throws Exception {
        Map<String, String> context = Utils.newContext();
        Parameters.FROM_DATE.put(context, "20130101");
        Parameters.TO_DATE.put(context, "20130102");
        MetricFilter.USER.put(context, "anonymoususer_1");

        Metric metric = new TestSetValueResulted();
        assertEquals(metric.getValue(context),
                     new SetValueData(Arrays.<ValueData>asList(new StringValueData("factory1"))));
    }

    @Test
    public void testSeveralFilter() throws Exception {
        Map<String, String> context = Utils.newContext();
        Parameters.FROM_DATE.put(context, "20130101");
        Parameters.TO_DATE.put(context, "20130102");
        MetricFilter.USER.put(context, "anonymoususer_1,anonymoususer_2,anonymoususer_3");
        MetricFilter.WS.put(context, "ws2");

        Metric metric = new TestSetValueResulted();
        assertEquals(metric.getValue(context),
                     new SetValueData(Arrays.<ValueData>asList(new StringValueData("factory3"))));
    }

    public class TestSetValueResulted extends CreatedFactoriesSet {

        @Override
        public String getStorageCollectionName() {
            return "testfactorycreatedlist";
        }
    }
}
