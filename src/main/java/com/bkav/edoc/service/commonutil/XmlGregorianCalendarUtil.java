package com.bkav.edoc.service.commonutil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XmlGregorianCalendarUtil {

    public static String VN_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static String VN_DATE_FORMAT = "dd/MM/yyyy";

    static public XMLGregorianCalendar getInstance()
            throws DatatypeConfigurationException {

        GregorianCalendar gcal = new GregorianCalendar();
        XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(gcal);
        return xgcal;

    }

    static public Date convertToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    static public String convertToString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String result = null;
        try {
            result = formatter.format(date);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    static public Date convertToDate(String dateString, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (Exception e) {
            _log.error(e.getMessage());
        }
        return date;
    }

    static public Date getMinDefaultDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1900);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

    static public Date convertToDate(String dateString, String format,
                                     Date defaultDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            date = defaultDate;
        }
        return date;
    }

    static public XMLGregorianCalendar setTime(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        GregorianCalendar gcal = new GregorianCalendar();
        XMLGregorianCalendar xgcal = null;
        try {
            xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        xgcal.setYear(cal.get(Calendar.YEAR));
        xgcal.setMonth(cal.get(Calendar.MONTH));
        xgcal.setDay(cal.get(Calendar.DATE));

        return xgcal;
    }

    private static final Log _log = LogFactory.getLog(XmlGregorianCalendarUtil.class);
}
