package com.jd.fishbaby.exception;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月30日 下午2:42:03
* $
*/
public class CommonException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private String message;
	
	public CommonException(String key) {
		super();
		this.key = key;
		this.message = key;
	}

	public CommonException(String key, String message) {
		super();
		this.key = key;
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
