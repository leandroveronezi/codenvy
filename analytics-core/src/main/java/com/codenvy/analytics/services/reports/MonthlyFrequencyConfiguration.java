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
package com.codenvy.analytics.services.reports;

import com.codenvy.analytics.Utils;
import com.codenvy.analytics.metrics.Parameters;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
@XmlRootElement(name = "monthly")
public class MonthlyFrequencyConfiguration extends AbstractFrequencyConfiguration {

    @Override
    public boolean isAppropriateDateToSendReport(Map<String, String> context) throws ParseException {
        Calendar toDate = Utils.getToDate(context);
        return toDate.get(Calendar.DAY_OF_MONTH) == toDate.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Parameters.TimeUnit getTimeUnit() {
        return Parameters.TimeUnit.MONTH;
    }
}
