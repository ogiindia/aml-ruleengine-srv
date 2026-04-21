package com.aml.srv.core.efrmsrv.utils;

public class Expriment {

	
	public static String spltPath(String destLocation,String shortName, String  source){
		
		
		int idx = destLocation.indexOf("#year#");
		 String patternUpToYear = destLocation.substring(0, idx);
		 // patternUpToYear = "c:/#ShortName#/#Source#/#year#"
		String rtnPath = patternUpToYear
			        .replace("#ShortName#", shortName)
			        .replace("#Source#", source);
		System.out.println(rtnPath);
		String query = "SELECT ";
		String parqutePath	=rtnPath;
		String parqutePathPart1 = "*/*/*"; //-----year/month/date
		query = query + " FROM read_parquet('" + parqutePath.replace("\\", "/") + parqutePathPart1
				+ "/*.parquet' , union_by_name=true)";
		
		return query;
	}
	
	public static void main(String arg[]) {
		String schemaTag ="ACCOUNTS_custometype";
		String parquetName = schemaTag.substring(0, schemaTag.indexOf("_"));
		String srchColumn = schemaTag.substring(schemaTag.indexOf("_")+1,schemaTag.length());
		System.out.println(parquetName);
		System.out.println(srchColumn);
		
		
		//System.out.println(spltPath("c:/#ShortName#/#Source#/#year#/#month#/#date#/","ACCOUNTS","CBS"));
	}
}
