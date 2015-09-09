package cn.sdu.online.findteam.aliwukong.imkit.widget;

/**
 * Created by wn on 2015/8/14.
 */

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.sdu.online.findteam.R;

/**
 *  日期格式化工具
 */
public class DateUtil {

    /**
     * 对工作圈的时间进行格式化操作
     * @param targetDate
     * @param isAlwayShowHHMM 是否总是显示HH:MM这样的时间段
     * @return
     */
    public static String formatRimetShowTime(Context context,long targetDate, boolean isAlwayShowHHMM) {
        //不是使用SimpleDateFormat，减少cpu计算量
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        long diffSec = now - targetDate;

        calendar.setTimeInMillis(targetDate);
        int year = calendar.get(Calendar.YEAR); //date.year
        int m = calendar.get(Calendar.MONTH) + 1;
        String month = m >= 10 ? String.valueOf(m) : "0" + m; //date.month
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day =  dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;//date.day
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = h >= 10 ? String.valueOf(h) : "0" + h; //date.hour
        int min = calendar.get(Calendar.MINUTE);
        String minute = min >= 10 ? String.valueOf(min) : "0" + min; //date.minute

        StringBuilder sb = new StringBuilder();
        boolean isFullFormat = false;

        if(currentYear > year){
            if(year >= 2000){
                year = year - 2000;
            }
            if(year < 10){
                sb.append("0" + year).append("-");
            }else{
                sb.append(year).append("-");
            }
            isFullFormat = true;
        }
        sb.append(month).append("-").append(day);

        String hhmm = hour + ":" + minute;

        if(isAlwayShowHHMM) {
            sb.append(" ").append(hhmm);
        }

        if(isFullFormat){
            return sb.toString();
        }

        //两天前
        if(diffSec >= 172800000 || (diffSec > 86400000 && currentHour < Integer.valueOf(hour))){
            return sb.toString();
        }
        //昨天
        if(diffSec >= 86400000 || currentDay != dayOfMonth){
            String result = context.getResources().getString(R.string.calendar_yesterday);
            if(isAlwayShowHHMM) {
                result = result + " " + hhmm;
            }
            return result;
        }
        //今天
        if(diffSec >= 60000){
            if(h < 12){
                return context.getResources().getString(R.string.calendar_morning)+" " + hhmm;
            }else{
                return context.getResources().getString(R.string.calendar_afternoon)+" " + hhmm;
            }
        }
        return context.getResources().getString(R.string.calendar_just_now);
    }

    /**
     * 格式化小时与分钟
     * @param targetDate
     * @return
     */
    public final static String formatHHMM(long targetDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(targetDate);

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = h >= 10 ? String.valueOf(h) : "0" + h; //date.hour
        int min = calendar.get(Calendar.MINUTE);
        String minute = min >= 10 ? String.valueOf(min) : "0" + min; //date.minute

        return hour + ":"  + minute;
    }

    public static String getDate(long date){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return inputFormat.format(date);
    }

}

