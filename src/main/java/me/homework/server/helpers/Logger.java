package me.homework.server.helpers;

import java.util.Calendar;

public class Logger {

    private static String makeLogString(String s) {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        String callerClass = st[st.length - 2].getClassName();
        return String.format("[%s] %s - %s", Calendar.getInstance().getTime(), 
            callerClass, s);
    }

    public static void info(String msg) {
        System.out.println(makeLogString(msg));
    }

    public static void error(String msg) {
        System.err.println(makeLogString(msg));
    }
}