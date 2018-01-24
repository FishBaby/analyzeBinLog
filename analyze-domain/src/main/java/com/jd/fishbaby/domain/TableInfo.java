package com.jd.fishbaby.domain;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午6:14:14
* $
*/
public class TableInfo {
	private String dataBaseName;
	private String tableName;
	private String fullName;
	@Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || this.getClass()!=o.getClass())
            return false;
        TableInfo tableInfo = (TableInfo)o;
        if(!this.dataBaseName.equals(tableInfo.getDataBaseName()))
            return false;
        if(!this.tableName.equals(tableInfo.getTableName()))
            return false;
        if(!this.fullName.equals(tableInfo.getFullName()))
            return false;
        return true;
    }

    @Override
    public int hashCode(){
        int result = this.tableName.hashCode();
        result = 31*result+this.dataBaseName.hashCode();
        result = 31*result+this.fullName.hashCode();
        return result;
    }

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
