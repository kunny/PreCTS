package com.androidhuman.ctsprepare.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Waiver {
	public String modelName;
	public String packageName;
	public String testCase;
	public String test;
	
	public static Waiver fromResultSet(ResultSet result) throws SQLException{
		Waiver waiver = new Waiver();
		waiver.modelName = result.getString("modelName");
		waiver.packageName = result.getString("packageName");
		waiver.testCase = result.getString("testCase");
		waiver.test = result.getString("test");
		return waiver;
	}
}
