package pl.essay.generic.dao;

import java.util.Map;

public class ListingParamsHolder {

	public Integer pageNo;
	public Integer pageSize; 

	//expecting pairs like 
	//"name":"asc"
	//"name":"desc"
	public Map<String, String> sortOrderFields;
	
	//expecting pairs like
	//"name":"Kowal"
	//"age":"23"
	public Map<String, String> filterFields;
	
}
