package pl.essay.imangular.domain.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemComponentDao itemComponentDao;

	@Override
	public void updateItem(Item i) {
		this.itemDao.update(i);
	}

	@Override
	public int addItem(Item i) {
		this.itemDao.create(i);
		return i.getId();
	}

	@Override
	public int addItemComponentFastNotSecure(ItemComponent ic) {
		this.itemComponentDao.create(ic);
		return ic.getId();
	}

	// method for fast import
	@Override
	public void addItemComponentFastNotSecure(Iterable<ItemComponent> ic) {
		this.itemComponentDao.create(ic);
	}

	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<Item> listItems() {
		return this.itemDao.getAll();
	}

	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<Item> listItemsPaginated(ListingParamsHolder params) {
		return this.itemDao.getAll(params);
	}

	@Override
	@Transactional(readOnly = true)
	public Item getItemById(int id) {
		Item item = this.itemDao.get(id);
		// get associated sets while session is open
		item.getComponents().size();
		item.getUsedIn().size();
		return item;
	}

	@Override
	public void removeItem(int id) {
		Item item = this.itemDao.load(id);
		this.itemDao.delete(item);
	}

	@Override
	public void removeItemComponent(int componentId) {
		ItemComponent ic = this.itemComponentDao.load(componentId);
		Item parentItem = ic.getParent();
		parentItem.removeComponent(ic);
		this.itemDao.update(parentItem);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemComponent getItemComponent(int id) {
		return this.itemComponentDao.load(id);
	}

	@Override
	public int addOrUpdateItemComponent(ItemComponent component) {
		System.out.println("addOrUpdateItemComponent component item::" + component);

		if (component.getId() == 0) {
			int itemId = component.getParent().getId();
			Item paretnItem = this.itemDao.load(itemId);
			Item componentItem = this.itemDao.load(component.getComponent().getId());
			component.setComponent(componentItem);
			paretnItem.addComponent(component);
			System.out.println("addOrUpdateItemComponent component item::" + component.getComponent());
			this.itemDao.update(paretnItem);
		} else {
			this.itemComponentDao.update(component);
		}

		return component.getId();
	}

	@Override
	public Set<ItemComponent> getItemComponentsByParent(int id) {
		return this.itemComponentDao.getItemComponentsByParent(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsItem(String name) {
		return itemDao.existsItemByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public Item getItemByName(String name) {
		return itemDao.getItemByName(name);
	}

	/*
	 * method for preparing select list for - add/edit component - create bom (that
	 * why isComposed is needed)
	 * 
	 * assumptions: when id != 0 then it is list for add/edit component (need to
	 * exclude some items according to parent items) when id == 0 then it is list
	 * for create bom - need to exclude not composed items
	 * 
	 * must not contain the item for which we prepare the list
	 * 
	 * (to be done later: and any components that contain the item in descendants)
	 * 
	 * term - to be done later - for fetching narrowed by name select list (for
	 * ui-select)
	 * 
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ItemIdNameIsComposedQueryResult> getAllItemsInShort(int itemId, String term) {
		Map<String, ItemIdNameIsComposedQueryResult> theMap = new TreeMap<String, ItemIdNameIsComposedQueryResult>();
		// copy all the result to map
		for (ItemIdNameIsComposedQueryResult i : itemDao.getAllItemsInShort()) {
			if (itemId != 0 || i.isComposed == true) // add to list all when id is not 0 (then it is select for add bom
														// and need to check if isComposed)
				theMap.put(i.name, i);
		}
		// remove from map the item for which we build select list
		// if id == 0 then it's select for create bom
		if (itemId != 0) {
			Item item = this.itemDao.get(itemId);
			theMap.remove(item.getName()); // name is safe as we have primary key on name

			/*
			 * this part is removed as there can be the same item in components many times
			 * on various steps of assembling parent item for (ItemComponent ic :
			 * item.getComponents()){ theMap.remove(ic.getComponentName()); }
			 */
		}
		// move all the items to a simple list
		List<ItemIdNameIsComposedQueryResult> theList = new ArrayList<ItemIdNameIsComposedQueryResult>();
		for (Map.Entry<String, ItemIdNameIsComposedQueryResult> entry : theMap.entrySet())
			theList.add(entry.getValue());

		return theList;
	}

}
