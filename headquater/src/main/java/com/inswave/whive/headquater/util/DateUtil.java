package com.inswave.whive.headquater.util;

import com.inswave.whive.headquater.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final DateTimeFormatter DATE_TIME_NANOSECONDS_OFFSET_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive().append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 3, true)
            .appendOffset("+HH:mm", "Z")
            .toFormatter();

    private static final Logger logger = LoggerFactory.getLogger(Class.class);

    public static String getDate(int iDay) {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();

        temp.add(Calendar.DAY_OF_MONTH, iDay);

        int nYear = temp.get(Calendar.YEAR);
        int nMonth = temp.get(Calendar.MONTH) + 1;
        int nDay = temp.get(Calendar.DAY_OF_MONTH);

        sbDate.append(nYear + "-");
        if (nMonth < 10)
            sbDate.append("0");

        sbDate.append(nMonth + "-");
        if (nDay < 10)
            sbDate.append("0");
        sbDate.append(nDay);

        return sbDate.toString();
    }

    public static String getDate2(int iDay) {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();

        temp.add(Calendar.DAY_OF_MONTH, iDay);

        int nYear = temp.get(Calendar.YEAR);
        int nMonth = temp.get(Calendar.MONTH) + 1;
        int nDay = temp.get(Calendar.DAY_OF_MONTH);

        sbDate.append(nYear);
        if (nMonth < 10)
            sbDate.append("0");

        sbDate.append(nMonth);
        if (nDay < 10)
            sbDate.append("0");
        sbDate.append(nDay);

        return sbDate.toString();
    }

    public static int getYear() {
        Calendar temp = Calendar.getInstance();
        return temp.get(Calendar.YEAR);
    }

    public static int getMonth() {
        Calendar temp = Calendar.getInstance();
        return temp.get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();
        return temp.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek() {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();
        return temp.get(Calendar.DAY_OF_WEEK);
    }

    public static int getMinute10() {
        Calendar calendar = Calendar.getInstance();
        int ret = 0;
        try {
            ret = calendar.get(Calendar.MINUTE) / 10;
        } catch (Exception e) {
        }
        return ret;
    }

    public static int getMinute() {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();
        return temp.get(Calendar.MINUTE);
    }

    public static int getSecond() {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();
        return temp.get(Calendar.SECOND);
    }

    public static int getHour() {
        Calendar temp = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();
        return temp.get(Calendar.HOUR_OF_DAY);
    }

    public static Date getDate(String datePattern, String sdate) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            date = simpleDateFormat.parse(sdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date toDate(String date, String pattern) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getPasswordDueDate(int day) {
        String yyyy_mm_dd = getDate(day);
        String HH_mm_ss = "23:59:59";
        Date date = getDate("yyyy-MM-dd", yyyy_mm_dd + " " + HH_mm_ss);
        return date;
    }

    public static String getCurrentDate(String datePattern) {
        String ret = null;
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            ret = simpleDateFormat.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getAdminDisplayDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.TAG_DATE_PATTERN_OF_ADMIN_DISPLAY);
        String sDate = formatter.format(date);
        return sDate;
    }

    private static String addDate(String strDate, String pattern, int yyyy, int MM, int dd, int HH, int mm, int ss) {
        String ret = "";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        try {
            Date date = format.parse(strDate);
            cal.setTime(date);
            cal.add(Calendar.YEAR, yyyy); //년 더하기
            cal.add(Calendar.MONTH, MM); //월 더하기
            cal.add(Calendar.DATE, dd); //일 더하기
            cal.add(Calendar.HOUR, HH); //시 더하기
            cal.add(Calendar.MINUTE, mm); //분 더하기
            cal.add(Calendar.SECOND, ss); //초 더하기
            ret = format.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static long getElapsedTime(String elapsedTime) {
        long ret = 0L;
        try {
            String time = "00:00:00.000";
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

            Date d1 = format.parse(time);
            Date d2 = format.parse(elapsedTime);

            long diff = d2.getTime() - d1.getTime();
            ret = diff;

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;

            //            System.out.println(diffHours + " hours, ");
            //            System.out.println(diffMinutes + " minutes, ");
            //            System.out.println(diffSeconds + " seconds.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getDate(long lDate, String pattern) {
        Date date = new Date(lDate);
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String getDate(Date date, String datePattern) {
        if (date == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        return formatter.format(date);
    }

    public static String getDateFromPath(Path path, String pattern) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime date = attr.creationTime();

        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date.toMillis());
    }

    public static Long get10Minutely(Date date) {
        if (date == null)
            return -1L;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        String strdate = formatter.format(date);
        if (strdate.length() > 0)
            strdate = strdate.substring(0, strdate.length() - 1);
        return Long.parseLong(strdate);
    }

    public static boolean isSavedElasticDate(String elasticDate, int savedDay) {
        if (savedDay <= 0)
            return true; // 조건 체크하지 않고 무조건 저장
        int checkDay = savedDay * -1;
        int elasticIndiceDate = Integer.parseInt(elasticDate);
        int fromDate = Integer.parseInt(DateUtil.getDate2(checkDay));
        logger.debug("elasticIndiceDate-->" + elasticIndiceDate + " fromDate-->" + fromDate);
        if (elasticIndiceDate > fromDate)
            return true;
        return false;
    }

    public static boolean isDateInBetweenIncludingEndPoints(final Date min, final Date max, final Date date) {
        return !(date.before(min) || date.after(max));
    }

    public static Date getDate2(String datePattern, String strDate) {
        Date retDate = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat(datePattern);
            df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            retDate = df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static void main(String argv[]) {

        System.out.println(DateUtil.getCurrentDateElasticsearch(Constants.TAG_DATE_PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z));
        System.out.println(DateUtil.getYear());
        System.out.println(DateUtil.getMonth());
        System.out.println(DateUtil.getDay());
        System.out.println(DateUtil.getDayOfWeek());
        System.out.println(DateUtil.getHour());
        System.out.println(DateUtil.getMinute10());

        //        super.setYear = DateUtil.;
        //        super.setMonth = ;
        //        super.setDay = -1;
        //        super.setDayOfWeek = DateUtil.get
        //        super.setHour = DateUtil.getHour();
        //        super.setMinutely10 = -1;

        //        String fileName = "this.getClass().getSimpleName()"+"_"+DateUtil.getCurrentDate("yyyyMMddHH");
        //        System.out.println(fileName);

        //        String from = DateUtil.getCurrentDate(Constants.TAG_DATE_PATTERN_YYYY_MM_DD) + "T00:00:00.000Z";
        //        String to = DateUtil.getCurrentDate(Constants.TAG_DATE_PATTERN_YYYY_MM_DD) + "T"+16+":00:00.000Z"; // ??????????
        //        Date fDate = getDate2(Constants.TAG_DATE_PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z,from);
        //        Date tDate = getDate2(Constants.TAG_DATE_PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z,to);
        //
        //        Date checkDate = getDate2(Constants.TAG_DATE_PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z, DateUtil.getCurrentDate(Constants.TAG_DATE_PATTERN_YYYY_MM_DD) + "T"+15+":00:00.000Z");
        //
        //        System.out.println(   isDateInBetweenIncludingEndPoints(fDate, tDate, checkDate)  );

        //System.out.println(isSavedElasticDate("20211118", 3));
        //System.out.println(getDate("yyyy-MM-dd HH:mm:ss","0000-00-00 00:00:00"));
        //        String elapsedTime  = "00:01:59.560";
        //
        //        System.out.println( DateUtil.getElapsedTime(elapsedTime) );
        //
        //        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        //        String currentDate = DateUtil.getCurrentDateElasticsearch(pattern);
        //        System.out.println( currentDate );
        //
        //        Date date = getDate(pattern, currentDate);
        //        System.out.println(  );
        //
        //        Date sDate = getDate(pattern, "2000-01-01'T'00:00:00.000'Z'");
        //        Date aDate = getDate(pattern, "2000-01-01'T'01:10:10.001'Z'");
        //
        //        String strDate = "2021-06-16'T'01:01:01.001'Z'";
        //        //System.out.println(addDate(strDate,pattern, 1, 1, 1, 1, 1, 1));

        //String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        //Date sDate = getDate(pattern, "2021-01-01'T'01:01:15.548'Z'");

        //        System.out.println( get10Minutely(new Date()) );
        //
        //        System.out.println(Long.MAX_VALUE);
        //        System.out.println(Double.MAX_VALUE);

        //        Date date = getTimeRegistered();
        //        System.out.println();

        //        System.out.println( DateUtil.getMinute() );
    }

    public static String getCurrentDateElasticsearch(String datePattern) {
        String ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);//, Locale.KOREA);
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            ret = sdf.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Date svnDateToDate(String svnDate) throws ParseException {
        if (svnDate != null) {
            String trimmed = svnDate.substring(0, "yyyy-MM-ddTHH:mm:ss.SSS".length());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return format.parse(trimmed);
        }
        return null;
    }

    public static ZonedDateTime getDate(String isoString) {
        ZonedDateTime zdt = ZonedDateTime.parse(isoString, DATE_TIME_NANOSECONDS_OFFSET_FORMATTER);
        return zdt;
    }

}
