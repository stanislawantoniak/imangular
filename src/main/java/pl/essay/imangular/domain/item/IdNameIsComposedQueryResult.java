package pl.essay.imangular.domain.item;

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