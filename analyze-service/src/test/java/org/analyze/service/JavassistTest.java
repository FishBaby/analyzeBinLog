package org.analyze.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月29日 下午3:11:32
* $
*/
public class JavassistTest {
	public static void main(String[] args) throws CannotCompileException, InstantiationException, IllegalAccessException, NotFoundException, ClassNotFoundException {
		JavassistTest javassistTest = new JavassistTest();
		Object student1 = null, team = null;
		
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		fieldMap.put("name", "xiao ming");
		fieldMap.put("age", 27);
		
		student1 = javassistTest.addField("Student" , fieldMap);
		Class c = Class.forName("Student");
		Object s1 = c.newInstance();
		Object s2 = c.newInstance();
		
		//javassistTest.setFieldValue(s1, "name", "xiao ming");
		//javassistTest.setFieldValue(s2, "name", "xiao zhang");
		fieldMap.clear();
		
		Field[] fields = student1.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				System.out.println(field.getName() + " = " + javassistTest.getFieldValue(student1, field.getName()));
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Method[] methods = student1.getClass().getDeclaredMethods();
		for (Method method : methods) {
			System.out.println("method name = " + method.getName());
		}
	}

	private Object getFieldValue(Object dObject, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Object result = null;
		Field fu = dObject.getClass().getDeclaredField(name);
		fu.setAccessible(true);
		result = fu.get(dObject);
		return result;
	}

	private Object addField(String className, Map<String, Object> fieldMap) throws CannotCompileException, InstantiationException, IllegalAccessException, NotFoundException {
		//获取javassist类池
		ClassPool pool = ClassPool.getDefault();
		CtClass ctClass = pool.makeClass(className);
		
		Iterator it = fieldMap.entrySet().iterator();
		
		//add field
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String fieldName = (String)entry.getKey();
			Object fieldValue = entry.getValue();
			/*String fieldType = fieldValue.getClass().getName();
			CtField ctField = new CtField(pool.get(fieldType) , fieldName , ctClass);
			
			ctField.setModifiers(Modifier.PUBLIC);
			ctClass.addField(ctField);*/
			
			CtField ctField = CtField.make("public " + fieldValue.getClass().getName() +" " + fieldName + ";", ctClass);
			ctClass.addField(ctField);
		}
		
		
		
		//add method
		CtMethod ctMethod = CtMethod.make("public String getName(){return name;}", ctClass);
		ctClass.addMethod(ctMethod);
		Class c = ctClass.toClass();
		Object newObject = c.newInstance();
		
		it = fieldMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Entry)it.next();
			String fieldName = (String)entry.getKey();
			Object fieldValue = entry.getValue();
			this.setFieldValue(newObject , fieldName , fieldValue);
		}
		return newObject;
	}

	private Object setFieldValue(Object newObject, String fieldName, Object fieldValue) {
		Object result = null;
		try {
			Field fu = newObject.getClass().getDeclaredField(fieldName);
			fu.setAccessible(true);
			try {
				fu.set(newObject, fieldValue);
				result = fu.get(newObject);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
