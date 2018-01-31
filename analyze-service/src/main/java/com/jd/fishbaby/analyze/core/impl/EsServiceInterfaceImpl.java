package com.jd.fishbaby.analyze.core.impl;

import java.awt.List;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.fishbaby.analyze.core.EsServiceInterface;
import com.jd.fishbaby.constant.EsConstant;
import com.jd.fishbaby.domain.BaseObject;
import com.jd.fishbaby.enums.EventTypeEnum;
import com.jd.fishbaby.utils.DynamicCreateObject;

/**
 * Created with Eclipse.
 * 
 * @author yuminghui3
 * @version 创建时间：2018年1月29日 下午2:47:17 $
 */
public class EsServiceInterfaceImpl implements EsServiceInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(EsServiceInterfaceImpl.class);
	private Client client;

	@Override
	public Boolean add(BaseObject baseObject) {
		BulkRequestBuilder prepareBulk = client.prepareBulk();
		ObjectMapper objectMapper = new ObjectMapper();
		Object newObject = createObject(baseObject);
		byte[] jsonObj;
		try {
			jsonObj = objectMapper.writeValueAsBytes(newObject);
			prepareBulk.add(client.prepareIndex(baseObject.getDatabaseName(), baseObject.getTableName())
					.setSource(jsonObj).setId(baseObject.getDatabaseName() + "_" + baseObject.getTableName() + "_c"
							+ new Date().getTime()));
		} catch (Exception e) {
			LOGGER.error("Convert json format error!!!", e);
		}
		// 执行批量处理
		BulkResponse response = prepareBulk.execute().actionGet();
		if (response.hasFailures()) {
			LOGGER.error("批量添加ES错误!失败个数：" + response.getItems().length);
			return false;
		}else {
			return true;
		}
	}

	public static Object createObject(BaseObject baseObject) {
		String newClassName = baseObject.getDatabaseName() + "_" + baseObject.getTableName() + new Date().getTime();
		Map<String, Object> fieldAndValue = null;
		ArrayList<String> methodDeclare = new ArrayList<String>();
		try {
			fieldAndValue = getFieldAndValue(baseObject);
		} catch (InstantiationException e) {
			LOGGER.error("InstantiationException", e);
		} catch (IllegalAccessException e) {
			LOGGER.error("IllegalAccessException", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("IllegalArgumentException", e);
		}
		fieldAndValue = formatFieldAndValue(fieldAndValue);
		return DynamicCreateObject.createClass(newClassName, fieldAndValue, methodDeclare);
	}

	private static Map<String, Object> formatFieldAndValue(Map<String, Object> fieldAndValue) {
		Map<String, Object> rst = new HashMap<String, Object>();
		for (Entry<String, Object> entry : fieldAndValue.entrySet()) {
			String fieldName = entry.getKey();
			String declareField = EsConstant.PREFIX_FIELD_DECLARE + fieldName + EsConstant.SUFFIX_FIELD_DECLARE;
			rst.put(declareField, entry.getValue());
		}
		return rst;
	}

	private static Map<String, Object> getFieldAndValue(BaseObject baseObject)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException {
		Map<String, Object> rst = new HashMap<String, Object>();
		Field[] fields = baseObject.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		for (Field field : fields) {
			if (field.getType().isEnum()) {
				Enum e = (Enum) field.get(baseObject);
				rst.put(field.getName(), e.name());
				continue;
			}
			if (field.getGenericType().toString().equals(EsConstant.DATE_TYPE_NAME)) {
				SimpleDateFormat sdf = new SimpleDateFormat(EsConstant.DATE_FORMAT_STRING);
				String sdfDate = sdf.format((Date) field.get(baseObject));
				rst.put(field.getName(), sdfDate);
				continue;
			}
			if (!(field.getGenericType().toString().equals(EsConstant.MAP_TYPE_NAME))) {
				rst.put(field.getName(), field.get(baseObject));
			}
		}
		if(baseObject.getFieldAndValue()!= null && baseObject.getFieldAndValue().size() > 0) {
			for (Entry<String, String> entry : baseObject.getFieldAndValue().entrySet()) {
				rst.put(entry.getKey(), entry.getValue());
			}
		}
		return rst;
	}

	@Override
	public List query(BaseObject baseObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
