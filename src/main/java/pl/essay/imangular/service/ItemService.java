package pl.essay.imangular.service;

import java.util.List;
import java.util.Set;

import pl.essay.imangular.model.IdNameIsComposedQueryResult;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;

public interface ItemService {

	public int addItem(Item i);
	public int addItemComponentFastNotSecure(ItemComponent ic);
	public void addItemComponentFastNotSecure(Iterable<ItemComponent> ic);
	public void updateItem(Item i);
	public List<Item> listItems();
	public List<Item> listItemsPaginated(int pageNo, int pageSize,  String sortBy, String dir);
	public long getCount();
	public Item getItemById(int id);
	public void removeItem(int id);
	public void removeItemComponent(int componentId);
	public int addOrUpdateItemComponent(ItemComponent component);
	public ItemComponent getItemComponent(int id);
	public Set<ItemComponent> getItemComponentsByParent(int id);
	public boolean existsItem(String name);
	public Item getItemByName(String name);
	
	public List<IdNameIsComposedQueryResult> getAllItemsInShort();
}
