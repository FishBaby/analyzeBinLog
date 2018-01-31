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
	Boolean add(BaseObject baseObject);
	
	List query(BaseObject baseObject);
}
