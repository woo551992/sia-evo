package evo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A Map which use String as key and accept any type of value .<br/>
 * Support complex type object value to parse JSON.
 * @usage
 * <PRE>
 * {@code
 * Packet packet = new Packet();
 * packet.put("Any type of Object", new ComplexObject());
 * packet = Packet.fromString(packet.toString());
 * ComplexObject object = packet.get("Any type of Object", ComplexObject.class);
 * }
 * </PRE>
 * @rule 
 * <PRE>
 * Base on Jackson, your objects have to follow the rules to access your required fields.
 * 1) A default constructor must be exist.
 * 2) Non-public fields must have public getter & setter with the same name (Example: getX() & setX(int)), if you need to use those fields.
 * 3) The structure of the getter & setter must follow
 * {@code
 * private int x;
 * public int getX();
 * public void setX(int para);//The signature void and para's name is not care
 * }
 * 4) If you do not have a setter for an non-public field, the name of that field must match it's getter (Example: myName & getMyName)
 * 5) If you want more customization, you can use Jackson's annotation.
 * </PRE>
 * 
 * @version jackson2.2.0
 */
public class Packet implements Map<String, Object>{

	/** Map core, storage of all values */
	private Map<String, Object> cm_o_core;
	/** Jackson ObjectMapper, configuration is accepted. */
	private final ObjectMapper c_o_mapper = onCreateObjectMapper(
			new ObjectMapper()
//			.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)					// enable String indentation
//			.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)	// Hide fields when null
			);

//		GETTER - Fields		\\
	public Map<String, Object> getCore(){
		return this.cm_o_core;
	}
	/** Jackson ObjectMapper, configuration is accepted. */
	public ObjectMapper getObjectMapper(){
		return this.c_o_mapper;
	}
//
	/** Construct an instance using a empty default core Map: {@link java.util.HashMap HashMap} */
	public Packet(){
		this(new HashMap<String, Object>());
	}
	/**
	 * Construct an instance by a customized Map. 
	 * Therefore you can use different Map such as {@link java.util.concurrent.ConcurrentHashMap ConcurrentHashMap} for synchronized, or
	 * {@link java.util.LinkedHashMap LinkedHashMap} to have ordered collection.
	 * @param pm_o_core Customized Map with string as key.
	 */
	@SuppressWarnings("unchecked")	// unchecked - super type must have a absolute type for value, currently is <Object>. In case, constructor accept any type of Map with key <String>.
	public Packet(Map<String, ? extends Object> pm_o_core){
		this.cm_o_core = (Map<String, Object>) pm_o_core;
	}
	
	/** Override this method for configuring Jackson ObjectMapper */
	protected ObjectMapper onCreateObjectMapper(ObjectMapper objectMapper) {
		return objectMapper;
	}

//		Static Methods		\\
	/**
	 * Same as {@link #fromString(String, Map)} but use the default Map core - HashMap.
	 * @param p_s_json json string
	 * @return
	 */
	public static Packet fromString(String p_s_json){
		return fromString(p_s_json, new HashMap<String, Object>());
	}
	/**
	 * Deserialize from json string
	 * @param p_s_json json string
	 * @param pm_o_core customized Map
	 * @return {@link #Packet()} never null, empty if failed. 
	 */
	public static Packet fromString(String p_s_json, Map<String, Object> pm_o_core) {
		Packet r_o_pack = new Packet(pm_o_core);
		r_o_pack.deserialize(p_s_json);
		return r_o_pack;
	}
		
//		Methods		\\
	/**
	 * Read json string then put all collections to core. Failed if syntax-error.
	 * @param p_s_json
	 */
	public void deserialize(String p_s_json){
		try {
			JSONObject l_o_json = new JSONObject(p_s_json);
			//	Looping of the keys
			for (Iterator<?> l_itr_key = l_o_json.keys(); l_itr_key.hasNext();) {
				String l_s_key = (String)l_itr_key.next();
				this.put(l_s_key, l_o_json.get(l_s_key));	// transfer the JSON object to Map.
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Same as {@link #serialize()}
	 */
	@Override
	public String toString() {
		return this.serialize();
	}
	/**
	 * Serialize to json string with indentation, calling {@link #toString()} have same effect.
	 * @return json string
	 */
	public String serialize(){		
		try {
			return c_o_mapper.writeValueAsString(cm_o_core);
		} catch (JsonProcessingException e) {
			return new JSONObject(cm_o_core).toString();
		}
	}

//		Custom Definition		\\
	private static final String 
		KEY_ERROR = "error", KEY_MESSAGE = "message";
	
	public boolean isError()					{return this.getBoolean(KEY_ERROR, false);}
	public String getMessage()					{return this.getString(KEY_MESSAGE);}
	public void setError(boolean p_fl_error)	{this.put(KEY_ERROR, p_fl_error);}
	public void setMessage(String p_s_msg)		{this.put(KEY_MESSAGE, p_s_msg);}

//		put - basic data type		\\
	public Object put(String p_s_key, boolean p_val)			{return this.put(p_s_key, (Object) p_val);}
	public Object put(String p_s_key, int p_val)				{return this.put(p_s_key, (Object) p_val);}
	public Object put(String p_s_key, double p_val)				{return this.put(p_s_key, (Object) p_val);}
	
//		get - basic data type		\\
	public boolean getBoolean(String p_s_key, boolean p_def)	{return this.get(p_s_key, Boolean.class, p_def);}
	public int getInt(String p_s_key, int p_def)				{return this.get(p_s_key, Integer.class, p_def);}
	public double getDouble(String p_s_key, double p_def)		{return this.get(p_s_key, Double.class, p_def);}
	/**
	 * @param p_s_key
	 * @return null if not found
	 */
	public String getString(String p_s_key)						{return this.get(p_s_key, String.class);}
	/**
	 * Get string with default value
	 * @param p_s_key
	 * @param p_def default string
	 * @return default string if not found
	 */
	public String getString(String p_s_key, String p_def)		{return this.get(p_s_key, String.class, p_def);}
	
//	public Calendar getCalendar(String p_s_key){
//		Calendar calendar = null;
//		if (cm_o_core.get(p_s_key) instanceof Calendar) {
//			calendar = (Calendar) cm_o_core.get(p_s_key);
//		} else {
//			final Long millis = this.get(p_s_key, Long.class);	// calendar was serialize as long
//			if (millis != null) {
//				calendar = Calendar.getInstance();
//				calendar.setTimeInMillis(millis);
//			}
//		}
//		return calendar;
//	}

	public Calendar getCalendar(String p_s_key){
		return this.getBasicAndCast(p_s_key, Long.class, Calendar.class, new ElementCaster<Long, Calendar>(){

			@Override
			public Calendar doCast(Long p_o_from) {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(p_o_from);
				return calendar;
			}});
	}
	 
	/**
	 * Handle basic type element casting such as Calendar was serialized as long
	 * @param p_s_key Element key
	 * @param p_clss_from cast from
	 * @param p_clss_to cast to
	 * @param elementCaster used to initialize the object 
	 * @return the object returned by {@link ElementCaster}, or the original element if not serialized; null if not found
	 */
	protected <F,T> T getBasicAndCast(String p_s_key, Class<F> p_clss_from, Class<T> p_clss_to, ElementCaster<F,T> elementCaster){
		T r_o_to = null;
		if (p_clss_to.isInstance(cm_o_core.get(p_s_key))) {	// if the value is not serialized(instance of to Object)
			r_o_to = p_clss_to.cast(cm_o_core.get(p_s_key));
		} else {
			// casting is required
			final F p_o_from = this.get(p_s_key, p_clss_from);
			if (p_o_from != null) {
				r_o_to = elementCaster.doCast(p_o_from);
				
				// Replace
				cm_o_core.put(p_s_key, r_o_to);
			}
		}
		return r_o_to;
	}
	
	protected interface ElementCaster<F,T>{
		/** Called by {@link Packet #getBasicAndCast(String, Class, Class, ElementCaster)}, when the element require casting */
		T doCast(F p_o_from);
	}

//		get - complex type		\\
	/**
	 * This get method can handle normal List
	 * <PRE>
	 * {@code
	 * List<MyObject> list = packet.getList("key", MyObject.class);
	 * }
	 * </PRE>
	 */
	public <T> List<T> getList(String p_s_key, final Class<T> p_cls_component) {
		return this.get(p_s_key, new ElementDeserializer() {
			
			@Override
			public Object deserialize(ObjectMapper p_mapper, String p_s_json)
					throws JsonParseException, JsonMappingException, IOException {
				final JavaType l_jtype = p_mapper.getTypeFactory().constructCollectionType(List.class, p_cls_component);
				return p_mapper.readValue(p_s_json, l_jtype);
			}
		});
	}
	
	/**
	 * This get method can handle normal Map
	 * <PRE>
	 * {@code
	 * Map<Integer, MyObject> map = packet.getMap("key", Integer.class, MyObject.class);
	 * }
	 * </PRE>
	 */
	public <K,V> Map<K, V> getMap(String p_s_key, final Class<K> p_cls_key, final Class<V> p_cls_val){
		return this.get(p_s_key, new ElementDeserializer() {
			
			@Override
			public Object deserialize(ObjectMapper p_mapper, String p_s_json)
					throws JsonParseException, JsonMappingException, IOException {
				final JavaType l_jtype = p_mapper.getTypeFactory().constructMapType(Map.class, p_cls_key, p_cls_val);
				return p_mapper.readValue(p_s_json, l_jtype);
			}
		});
	}
//
	/**
	 * Same as {@link #get(String, Class)} but have default value
	 * @param p_s_key
	 * @param p_clss
	 * @param p_def default value
	 * @return default value if object is not found
	 */
	public <T> T get(String p_s_key, Class<T> p_clss, T p_def){
		T r_o_val = this.get(p_s_key, p_clss);
		return r_o_val!=null? r_o_val : p_def;
	}
	/**
	 * This get method can handle normal object
	 * <PRE>
	 * {@code
	 * MyObject object = packet.get("key", MyObject.class);
	 * }
	 * </PRE>
	 * @param p_s_key
	 * @param p_clss Class of the object
	 * @return null if object is not found
	 */
	public <T> T get(String p_s_key, final Class<T> p_clss){
		return this.get(p_s_key, new ElementDeserializer() {
			
			@Override
			public Object deserialize(ObjectMapper p_mapper, String p_s_json)
					throws JsonParseException, JsonMappingException, IOException {
				return p_mapper.readValue(p_s_json, p_clss);
			}
		});
	}
	
	/**
	 * This get method can handle most case of deserialization<br/>
	 * <PRE>
	 * {@code
	 * List{@literal <List<MyObject>>} list = packet.get("key", new TypeReference{@literal <List<List<MyObject>>>}() {});
	 * }
	 * </PRE>
	 */
	public <T> T get(String p_s_key, final TypeReference<T> p_type){
		return this.get(p_s_key, new ElementDeserializer() {
			
			@Override
			public Object deserialize(ObjectMapper p_mapper, String p_s_json)
					throws JsonParseException, JsonMappingException,
					IOException {
				return p_mapper.readValue(p_s_json, p_type);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T get(String p_s_key, ElementDeserializer deserializer){
		Object r_o_val = cm_o_core.get(p_s_key);
		if (this.checkIsJSON(r_o_val)) {
			final String l_s_json = r_o_val.toString();
			try {
				r_o_val = deserializer.deserialize(c_o_mapper, l_s_json);	// dynamic code
				System.out.println(String.format("Packet.get(): deserialized ELEMENT: %s_ KEY: %s_ VALUE: %s_", r_o_val.getClass(), p_s_key, r_o_val.toString()));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Replace
			cm_o_core.put(p_s_key, r_o_val);
		}
		
		return (T) r_o_val;
	}
	
	protected interface ElementDeserializer{
		/** Called by {@link Packet #get(String, ElementDeserializer)}, when an object was deserailized to an JSONObject/Array */
		Object deserialize(ObjectMapper p_mapper, String p_s_json) throws JsonParseException, JsonMappingException, IOException;
	}
	
	/** Determine the Object was deserialized to JSON. */
	private boolean checkIsJSON(Object p_o_val){
		return p_o_val instanceof JSONObject || p_o_val instanceof JSONArray;
	}
	
	
//		Utilize		\\
	/**
	 * Transform the Map core to another Map, 
	 * for example: You constructed a Packet using HashMap, and want to transform to ConcurrentHashMap to be synchronized.
	 * @param p_cls_map Class of the Map which you want to transform, must be extends from Map and have a Constructor(Map)
	 * @return true when the transform is success or the core is same as the target.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean transformCore(Class<? extends Map> p_cls_map){
		if (cm_o_core.getClass() == p_cls_map)
			return true;
		if (p_cls_map.isInterface()) {
			(new Exception("Packet.transformCore(Class) discarded: Class must be extends from Map.")).printStackTrace();
			return false;
		}
		try {
			this.cm_o_core = p_cls_map.getConstructor(Map.class).newInstance(this.cm_o_core);
			return true;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/** Pretty print to JSON format */
	public void printStackTrace(){
		String l_s_json = this.serialize();
		try {
			final Object l_o_json = c_o_mapper.readValue(l_s_json, Object.class);
			final ObjectWriter writer = c_o_mapper.writer(SerializationFeature.INDENT_OUTPUT);
			l_s_json = writer.writeValueAsString(l_o_json);
			System.out.println(l_s_json);
			return;
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		System.out.println("Failed on Packet.printStack()");		
	}
		
	
	
	
	
	
//		Map Methods		\\
	@Override
	public void clear() {
		this.cm_o_core.clear();
	}
	@Override
	public boolean containsKey(Object key) {
		return this.cm_o_core.containsKey(key);
	}

	@Override
	public boolean isEmpty() {
		return this.cm_o_core.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return this.cm_o_core.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		return this.cm_o_core.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		this.cm_o_core.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return this.cm_o_core.remove(key);
	}

	@Override
	public int size() {
		return this.cm_o_core.size();
	}

	/**
	 * @deprecated Deprecated, since values require deserialization
	 * @throws RuntimeException throws when value is serialized and has unknown type
	 */
	@Override @Deprecated
	public Object get(Object key) {
		Object r_o_val = this.cm_o_core.get(key);
		if (this.checkIsJSON(r_o_val))
			throw new RuntimeException("Value is serialized and has unknown type, please use other get methods");
		return r_o_val;
	}

	/**
	 * @deprecated Deprecated, since values require deserialization
	 */
	@Override @Deprecated
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return this.cm_o_core.entrySet();
	}

	/**
	 * @deprecated Deprecated, since values require deserialization
	 */
	@Override @Deprecated
	public Collection<Object> values() {
		return this.cm_o_core.values();
	}

	/**
	 * @deprecated Deprecated, since values require deserialization
	 */
	@Override @Deprecated
	public boolean containsValue(Object value) {
		return this.cm_o_core.containsValue(value);
	}
	
}
