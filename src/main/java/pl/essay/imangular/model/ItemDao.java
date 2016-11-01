package pl.essay.imangular.model;

import java.util.List;

import pl.essay.generic.dao.Dao;

public interface ItemDao extends Dao<Item> {

	public void addComponent(ItemComponent ic);
	public boolean existsItemByName(String name);
	public List<IdNameIsComposedQueryResult> getAllItemsInShort();
	
}
