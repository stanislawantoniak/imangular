package pl.essay.imangular.domain.item;

import java.util.List;

import pl.essay.generic.dao.GenericDaoHbn;

public interface ItemDao extends GenericDaoHbn<Item> {

	public boolean existsItemByName(String name);
	public Item getItemByName(String name);
	public List<ItemIdNameIsComposedQueryResult> getAllItemsInShort();
	
}
