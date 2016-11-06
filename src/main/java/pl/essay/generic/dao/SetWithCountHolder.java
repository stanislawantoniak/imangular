package pl.essay.generic.dao;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SetWithCountHolder <T extends Object>{

	@JsonIgnore //do not need any wrapper, only objects accesible via getters
	private	Map<String, Object> holder = new HashMap<String, Object>();
	
	public SetWithCountHolder(List<T> collection, long size){
		this.holder.put("totalRows", (Long) size);
		this.holder.put("collection", new LinkedHashSet<T>(collection) ) ;
	}

	@JsonIgnore
	public Map<String, Object> getCollectionWithCount(){
		return this.holder;
	}
	
	public SetWithCountHolder<T> setCollection(List<T> collection){
		this.holder.put("collection", new LinkedHashSet<T>(collection) ) ;
		return this;
	}
	
	public SetWithCountHolder<T>  setTotalRows( long size){
		this.holder.put("totalRows", (Long) size);
		return this;
	}

	@SuppressWarnings("unchecked")
	public LinkedHashSet<T> getCollection(){
		return (LinkedHashSet<T> ) this.holder.get("collection");
	}
	
	public Long getTotalRows(){
		return (Long) this.holder.get("totalRows");
	}

}
