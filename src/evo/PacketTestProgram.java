package evo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import evo.util.Display;

public class PacketTestProgram {
	
	public static void main(String[] args) {		
		Packet packet;
		final String jsonString;
		final Display beforeDeserialize;
		final Display afterDeserialize;
		packet = new Packet();
		
		packet.put("boolean", true);
		packet.put("double", 9d);
		packet.put("int", 99);
		packet.put("string", "Hello World");
		
		packet.put("singleObject", new TestObject(true));
		
		packet.put("objectArray", new TestObject[] { new TestObject(true), new TestObject(true) });
		
		packet.put("normalList", createNormalList());
		
		packet.put("normalMap", createNormalMap());
		
		packet.put("nestedList", createNestedList());
		
		packet.put("nestedMap", createNestedMap());

	beforeDeserialize = Display.store(packet);		packet.printStackTrace();

		jsonString = packet.toString();
		packet = Packet.fromString(jsonString);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!Deserialized!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
	afterDeserialize = Display.store(packet);		packet.printStackTrace();

		Display.display(packet.getBoolean("boolean", false));
		Display.display(packet.getDouble("double", 0));
		Display.display(packet.getInt("int", 0));
		Display.display(packet.getString("string"));
		
		Display.display(packet.get("singleObject", TestObject.class));
		
		Display.display(packet.get("objectArray", TestObject[].class));
		
		Display.display(packet.getList("normalList", TestObject.class));
		
		Display.display((Object)packet.getMap("normalMap", Integer.class, TestObject.class));
		
		Display.display(packet.get("nestedList", new TypeReference<List<List<TestObject>>>() {}));
		
		Display.display((Object)packet.get("nestedMap", new TypeReference<Map<Integer, Map<Integer, TestObject>>>() {}));

		beforeDeserialize.display();
		afterDeserialize.display();
	}

	private static Map<Integer, Map<Integer, TestObject>> createNestedMap() {
		Map<Integer, Map<Integer, TestObject>> map = new HashMap<Integer, Map<Integer,TestObject>>();
		map.put(0, createNormalMap());
		return map;
	}

	private static List<List<TestObject>> createNestedList() {
		List<List<TestObject>> list = new ArrayList<List<TestObject>>();
		list.add(createNormalList());
		return list;
	}

	private static Map<Integer, TestObject> createNormalMap() {
		Map<Integer, TestObject> map = new HashMap<Integer, PacketTestProgram.TestObject>();
		for (int i = 0; i < 2; i++)
			map.put(i, new TestObject(true));
		return map;
	}

	private static List<TestObject> createNormalList() {
		List<TestObject> list = new ArrayList<TestObject>();
		for (int i = 0; i < 2; i++)
			list.add(new TestObject(true));
		return list;
	}

	static class TestObject{
		static int COUNT= 0;
		
		public int count = COUNT++;
		public String[][] strings = new String[][]{{"a0","a1"},{"b0","b1"}};
		private SubObject subObject = null;
		
		public TestObject(){
		}
		public TestObject(boolean fl){
			if (fl)
				this.subObject = new SubObject(true);
		}
		
		public SubObject getSubObject() {
			return subObject;
		}
	}
	
	static class SubObject{
		public String string = "SubObject's string";
		private Date date = null;
		
		public SubObject(){
		}
		public SubObject(boolean fl){
			if (fl)
				this.date = new Date();
		}
		public Date getTodayDate(){
			return this.date;
		}
		public SubObject setTodayDate(Date date){
			this.date = date;
			return this;
		}
	}

}
