package pl.essay.imangular.domain.item;

public class ItemIdNameIsComposedQueryResult{
	public int id;
	public String name;
	public boolean isComposed;
	
	public ItemIdNameIsComposedQueryResult(int id, String n, boolean b){
		this.id = id;
		this.name = n;
		this.isComposed = b;
	}
}