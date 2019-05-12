package me.homework.server.helpers;

public class PerformanceStats {

	private static int MAX_REQUESTS_SAMPLE = 1000;

	private static long totalHandlingTime;
	private static int requestsDelivered;

	/** Circular array that will be used to keep the handling times of 
	recent requests. */
	private static int[] handlingTimes = new int[MAX_REQUESTS_SAMPLE];
	private static int idx = 0;


	public static double getAverageHandlingTime() {
		if (requestsDelivered == 0) {
			return 0;
		}
		return totalHandlingTime / (double) requestsDelivered;
	}

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