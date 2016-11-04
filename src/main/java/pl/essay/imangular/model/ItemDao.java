package pl.essay.imangular.model;

import java.util.List;

import pl.essay.generic.dao.GenericDaoHbn;

public interface ItemDao extends GenericDaoHbn<Item> {

	public void addComponent(ItemComponent ic);
	public boolean existsItemByName(String name);
	public Item getItemByName(String name);
	public List<IdNameIsComposedQueryResult> getAllItemsInShort();
	
}
