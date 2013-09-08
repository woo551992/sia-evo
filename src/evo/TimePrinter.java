package evo;

import java.util.HashMap;
import java.util.Map;

public class TimePrinter {
	
	private static Map<Integer, Long> TIMERS = new HashMap<Integer, Long>();
	
	public static void start(){
		start(-100);
	}
	public static void start(int id){
		TIMERS.put(id, System.currentTimeMillis());
	}
	
	public static void print(){
		print(-100);
	}
	public static void print(int id){
		print(id, null);
	}
	public static void print(int id, String name){
		long time = System.currentTimeMillis() - TIMERS.get(id);
		System.out.println(String.format("STOPWATCH[%s]: %s", name, time));
	}

}
