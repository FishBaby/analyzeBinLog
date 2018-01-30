package org.analyze.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.fishbaby.utils.DynamicCreateObject;


/**
 * Created with Eclipse.
 * 
 * @author yuminghui3
 * @version 创建时间：2018年1月29日 下午4:15:17 $
 */
public class JavassistTest2 {
	public static void main(String[] args)  {
		Map<String, Object> fieldAndValue = new HashMap<String, Object>();
		fieldAndValue.put("private String userName;", "FishBaby");
		fieldAndValue.put("private Integer age;", 24);
		List<String> methodDeclares = new ArrayList<String>();
		methodDeclares.add("public String getUserName(){return userName;}");
		methodDeclares.add("public Integer getAge(){return age;}");
		methodDeclares.add("public String toString(){return \"TestCreateClass [userName=\" + userName + \", age=\" + age + \"]\";}");
		Object obj = DynamicCreateObject.createClass("TestCreateClass", fieldAndValue, methodDeclares);
		try {
			Method method = obj.getClass().getMethod("toString");
			String res = (String) method.invoke(obj);
			System.out.println(res);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private String userName;
	private Integer age;
	@Override
	public String toString() {
		return "JavassistTest2 [userName=" + userName + ", age=" + age + "]";
	}
	
}
