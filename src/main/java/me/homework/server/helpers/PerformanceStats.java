package me.homework.server.helpers;

/** 
* Records the performance of requests.
*
* Created by Javier on 05/12/2019.
*/
public class PerformanceStats {

	/** How many recent requests will be used to compute the performance. */
	private static int MAX_REQUESTS_SAMPLE = 1000;

	/** Store the total sum of recent requests handling time. */
	private static long totalHandlingTime;

	/** 
	* Keeps count of the requests delivered so far. Must be less  
	* than MAX_REQUESTS_SAMPLE.
	*/
	private static int requestsDelivered;

	/** 
	* Circular array that will be used to keep the handling times of 
	* recent requests. 
	*/
	private static int[] handlingTimes = new int[MAX_REQUESTS_SAMPLE];

	/** Index for keeping track of next cell to modify in circular array. */
	private static int idx = 0;

	/** Retrieves the average handling time of recent requests. */
	public static double getAverageHandlingTime() {
		if (requestsDelivered == 0) {
			return 0;
		}
		return totalHandlingTime / (double) requestsDelivered;
	}

	/**
	* Records the handling time of a new request.
	* 
	* @param handlingTime The handling time of the request.
	*/
	public static synchronized void recordHandlingTime(int handlingTime) {
		if (requestsDelivered < MAX_REQUESTS_SAMPLE) {
			requestsDelivered++;
			handlingTimes[idx] = handlingTime;
		} else {
			totalHandlingTime -= handlingTimes[idx];
			handlingTimes[idx] = handlingTime;
			idx++;
		}
		totalHandlingTime += handlingTime;
		if (idx == handlingTimes.length) {
			idx = 0;
		}
	}
}