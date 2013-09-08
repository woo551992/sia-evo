package evo.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Display {
	private static final int STACK_OUT_1 = 2;
	private static int c_i_stack = STACK_OUT_1;

	/** Display object by JSON */
	public static void display(Object object){
		trace();
		try {
			System.out.println(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(object));
		} catch (JsonProcessingException e) {
			System.out.println("Failed on display");
		}
	}
	public static void display(Map<?, ?> map){
		trace();
		String key = "";
		String value = "";
		
		for (Iterator<?> iterator = map.entrySet().iterator();iterator.hasNext();) {
			Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
			key += "[" + entry.getKey().toString() + "]";
			value += entry.getValue().toString() + "_";

			key = addSpace(key, value.length() - key.length());
			value = addSpace(value, key.length() - value.length());
			
			key += "\t";
			value += "\t";
		}
		System.out.println(key);
		System.out.println(value);
	}
	
	private static String addSpace(String string, int space){
		for (int i = 0; i < space; i++) {
			string += " ";
		}
		return string;
	}
	/**
	 * <PRE>---------------Display at Program.main(Program.java:158)---------------</PRE>
	 */
	private static void trace(){
		final StackTraceElement caller = (new Exception()).getStackTrace()[c_i_stack];
		System.out.println(String.format("---------------Display at %s.%s(%s:%s)---------------",
				caller.getClassName(), caller.getMethodName(), caller.getFileName(), caller.getLineNumber()));
	}

// instance
	/** Store a reference to an object for later display */
	public static Display store(Object displayObject){
		return new Display(displayObject);
	}

	private Object displayObject;
	
	public Display(Object displayObject){
		this.displayObject = displayObject;
	}
	
	public void display(){
		c_i_stack = STACK_OUT_1 + 1;	// + 1 because the static display method is call by this method
		if (displayObject instanceof Map<?, ?>) {
			display((Map<?, ?>) displayObject);
		} else if (displayObject instanceof Object) {	// just sample code for extension
			display((Object) displayObject);
		}
		c_i_stack = STACK_OUT_1;	// restore
	}
}
