package pl.essay.imangular.model;

public class IdNameIsComposedQueryResult{
	public int id;
	public String name;
	public boolean isComposed;
	
	public IdNameIsComposedQueryResult(int id, String n, boolean b){
		this.id = id;
		this.name = n;
		this.isComposed = b;
	}
}