package org.analyze.service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jd.fishbaby.analyze.core.impl.EsServiceInterfaceImpl;
import com.jd.fishbaby.domain.BaseObject;
import com.jd.fishbaby.enums.EventTypeEnum;
import com.jd.fishbaby.utils.DynamicCreateObject;

/**
 * Created with Eclipse.
 * 
 * @author yuminghui3
 * @version 创建时间：2018年1月30日 下午4:04:46 $
 */
public class JavassistTest3 {
	static final String BEFORE = "Before_";
	static final String AFTER = "After_";
	static final String PREFIX_FIELD_DECLARE = "public String ";
	static final String SUFFIX_FIELD_DECLARE = " ;";
	public static void main(String[] args) {

		BaseObject baseObject = new BaseObject();
		baseObject.setDatabaseName("df_news");
		baseObject.setTableName("userInfo");
		baseObject.setEventOccurTime(new Date());
		baseObject.setEventType(EventTypeEnum.EVENT_INSERT);
		Map<String, String> afterMap = new HashMap<String, String>();
		Map<String, String> beforeMap = new HashMap<String, String>();
		afterMap.put("id", "2");
		beforeMap.put("id", "2");
		baseObject.setFieldAndValue(beforeMap);
		String newClassName = baseObject.getDatabaseName() + "_" + baseObject.getTableName();
		Map<String, Object> fieldAndValue = null;
		ArrayList<String> methodDeclare = new ArrayList<String>();
		try {
			fieldAndValue = getFieldAndValue(baseObject);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		fieldAndValue = formatFieldAndValue(fieldAndValue);
		methodDeclare.add("public String toString() { return \"BaseObject [databaseName=\" + databaseName + \", tableName=\" + tableName + \"]\";}");
		Object newObject = DynamicCreateObject.createClass(newClassName, fieldAndValue, methodDeclare);
		System.out.println(newObject.toString());
	}
	private static Map<String, Object> formatFieldAndValue(Map<String, Object> fieldAndValue) {
		Map<String, Object> rst = new HashMap<String, Object>();
		for (Entry<String , Object> entry : fieldAndValue.entrySet()) {
			String fieldName = entry.getKey();
			String declareField = PREFIX_FIELD_DECLARE + fieldName + SUFFIX_FIELD_DECLARE;
			rst.put(declareField, entry.getValue());
		}
		return rst;
	}
	private static Map<String, Object> getFieldAndValue(BaseObject baseObject) throws InstantiationException, IllegalAccessException, IllegalArgumentException {
		Map<String, Object> rst = new HashMap<String, Object>();
		Field[] fields = baseObject.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		for (Field field : fields) {
			if(field.getType().isEnum()) {
				Enum e = (Enum) field.get(baseObject);
				rst.put(field.getName(), e.name());
				continue;
			}
			if(field.getGenericType().toString().equals("class java.util.Date")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String sdfDate = sdf.format((Date)field.get(baseObject));
				rst.put(field.getName(), sdfDate);
				continue;
			}
			if(!(field.getGenericType().toString().equals("java.util.Map<java.lang.String, java.lang.String>"))) {
				rst.put(field.getName(), field.get(baseObject));
			}
		}
		for (Entry<String, String> entry : baseObject.getFieldAndValue().entrySet()) {
			rst.put(BEFORE + entry.getKey(), entry.getValue());
		}
		return rst;
	}
}
