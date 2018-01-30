package com.jd.fishbaby.analyze.core;

import java.awt.List;

import com.jd.fishbaby.domain.BaseObject;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月29日 下午2:38:25
* $
*/
public interface EsServiceInterface {
	static final String BEFORE = "Before_";
	static final String AFTER = "After_";
	static final String PREFIX_FIELD_DECLARE = "public String ";
	static final String SUFFIX_FIELD_DECLARE = " ;";
	
	Boolean add(BaseObject baseObject);
	
	List query(BaseObject baseObject);
}
