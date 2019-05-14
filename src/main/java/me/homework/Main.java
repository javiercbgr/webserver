package me.homework;

import me.homework.server.WebServer;
import me.homework.server.apps.FileServingApp;

/**
 * Created by Mihail on 10/24/2015.
 * Modified by Javier on 14/05/2019.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 3) {
            new WebServer(Integer.parseInt(args[0]), 
			            			 Integer.parseInt(args[1]), 
			            			 new FileServingApp(args[2])).run();
        } else {
            System.out.println("Usage: \njava -jar " +
            	"webserver-<version> <port> <threads> <document root>");
        }
    }

}
