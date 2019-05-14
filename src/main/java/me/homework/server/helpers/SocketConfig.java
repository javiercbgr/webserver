package me.homework.server.helpers;

/** 
* Socket config parameters.
*
* Created by Javier on 05/13/2019.
*/
public abstract class SocketConfig {

    /** Timeout for the incoming requests channels. */
    public final static int socketTimeout = 2000;

    /** Time the socket will be kept alive if required. */
    public final static int keepAliveTime = 4000;
}