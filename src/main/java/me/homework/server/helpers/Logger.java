package me.homework.server.helpers;

import java.util.Calendar;

/** 
* Simple logging utility class. Provides info and error channels.
*
* Created by Javier on 05/12/2019.
*/
public class Logger {

    /**
    * Creates a string for logging in the format:
    * `[<Thread id> - <Current timestamp>] <caller class> <msg>`
    * @param msg Log message to add in the string.
    */
    private static String makeLogString(String msg) {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        String callerClass = st[3].toString();
        return String.format("[%s - %s] %s - %s", 
            Thread.currentThread().getId(), 
            Calendar.getInstance().getTime(), 
            callerClass, 
            msg);
    }

    /**
    * Logs the message in the info channel in the following format:
    * `[<Thread id> - <Current timestamp>] <caller class> <msg>`
    * @param msg The message to log.
    */
    public static void info(String msg) {
        System.out.println(makeLogString(msg));
    }

    /**
    * Logs the message in the error channel in the following format:
    * `[<Thread id> - <Current timestamp>] <caller class> <msg>`
    * @param msg The message to log.
    */
    public static void error(String msg) {
        System.err.println(makeLogString(msg));
    }
}