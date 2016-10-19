package pl.essay.imangular.service;

import java.util.List;
import java.util.Set;

import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;

public interface ItemService {

	public int addItem(Item i);
	public void updateItem(Item i);
	public List<Item> listItems();
	public Item getItemById(int id);
	public void removeItem(int id);
	public void removeItemComponent(int componentId);
	public int addItemComponent(ItemComponent component);
	public ItemComponent getItemComponent(int id);
	public Set<ItemComponent> getItemComponentsByParent(int id);
	public boolean existsItem(String name);
}
