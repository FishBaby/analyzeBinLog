package com.jd.fishbaby.utils;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月29日 下午4:12:58
* $
*/

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.jd.fishbaby.exception.CommonException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

public class DynamicCreateObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicCreateObject.class);
	/**
	 * use javassist dynamic create class
	 * @param newClassName  for example : newClassName = DynamicCreateObject
	 * @param fieldAndValue for example : key = userName , value = "FishBaby"
	 * @param methodDeclares for example : "public String getUserName(){return userName;}"
	 * @return newObject
	 */
	public static Object createClass(String newClassName, Map<String, Object> fieldAndValue, List<String> methodDeclares) {
		Object newObject = null;
		if(StringUtils.isEmpty(newClassName)) {
			throw new CommonException("NewClassNameNull","New class name can't be null.");
		}
		if(fieldAndValue == null || fieldAndValue.size() <= 0) {
			throw new CommonException("FieldAndValueEmpty", "Field and value map can't be empty.");
		}
		try {
			//add field and method
			newObject = addFieldAndMethod(newClassName , fieldAndValue, methodDeclares);
			//add field value
			newObject = addFieldValue(newObject , fieldAndValue);
		} catch (InstantiationException e) {
			LOGGER.error("InstantiationException" , e);
		} catch (IllegalAccessException e) {
			LOGGER.error("IllegalAccessException" , e);
		} catch (CannotCompileException e) {
			LOGGER.error("CannotCompileException" , e);
		} catch (NoSuchFieldException e) {
			LOGGER.error("NoSuchFieldException" , e);
		} catch (SecurityException e) {
			LOGGER.error("SecurityException" , e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("IllegalArgumentException" , e);
		}
		return newObject;
	}

	private static Object addFieldValue(Object newObject, Map<String, Object> fieldAndValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field[] fields = newObject.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			field.setAccessible(true);
			field.set(newObject, getValue(field.getName() , fieldAndValue));
			
		}
		return newObject;
	}

	private static Object getValue(String name, Map<String, Object> fieldAndValue) {
		for (Map.Entry<String , Object> entry : fieldAndValue.entrySet()) {
			if(entry.getKey().contains(name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private static Object addFieldAndMethod(String newClassName, Map<String, Object> fieldAndValue, List<String> methodDeclares) throws CannotCompileException, InstantiationException, IllegalAccessException {
		ClassPool classPool = ClassPool.getDefault();
		
		CtClass ctClass = classPool.makeClass(newClassName);
		
		//add field
		for (String fieldDeclare : fieldAndValue.keySet()) {
			CtField ctField = CtField.make(fieldDeclare, ctClass);
			ctClass.addField(ctField);
		}
		if( methodDeclares == null || methodDeclares.size() <=0) {
			return ctClass.toClass().newInstance();
		}
		//add method
		for (String methodDeclare : methodDeclares) {
			CtMethod ctMethod = CtMethod.make(methodDeclare, ctClass);
			ctClass.addMethod(ctMethod);
		}
		return ctClass.toClass().newInstance();
	}
	
}
