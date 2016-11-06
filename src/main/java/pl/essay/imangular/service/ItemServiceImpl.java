package pl.essay.imangular.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.IdNameIsComposedQueryResult;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;
import pl.essay.imangular.model.ItemComponentDao;
import pl.essay.imangular.model.ItemDao;

@Service
@Transactional
public class ItemServiceImpl implements ItemService{

	@Autowired private ItemDao itemDao;
	@Autowired private ItemComponentDao itemComponentDao;

	@Override
	public void updateItem(Item i){
		this.itemDao.update(i);
	}
	
	@Override
	public int addItem(Item i){
		this.itemDao.create(i);
		return i.getId();
	}
	
	@Override
	public int addItemComponentFastNotSecure(ItemComponent ic){
		this.itemComponentDao.create(ic);
		return ic.getId();
	}

	//method for fast import
	@Override
	public void addItemComponentFastNotSecure(Iterable<ItemComponent> ic){
		this.itemComponentDao.create(ic);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<Item> listItems(){
		return this.itemDao.getAll();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<Item>  listItemsPaginated(ListingParamsHolder params){
		return this.itemDao.getAll(params);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Item getItemById(int id){
		Item item = this.itemDao.get(id);
		//get associated sets while session is open
		item.getComponents().size();
		item.getUsedIn().size();
		return item;
	}
	
	@Override
	public void removeItem(int id){
		Item item = this.itemDao.load(id);
		this.itemDao.delete(item);
	}

	@Override
	public void removeItemComponent(int componentId){
		ItemComponent ic = this.itemComponentDao.load(componentId);
		Item item = ic.getParent();
		item.removeComponent(ic);
		this.itemDao.update(item);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemComponent getItemComponent(int id){
		return this.itemComponentDao.load(id);
	}

	@Override
	public int addOrUpdateItemComponent(ItemComponent component){

		if (component.getId() == 0){
			int itemId = component.getParent().getId();
			Item item = this.itemDao.load(itemId);
			item.addComponent(component);
			this.itemDao.update(item);
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
	public boolean existsItem(String name){
		return itemDao.existsItemByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Item getItemByName(String name){
		return itemDao.getItemByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<IdNameIsComposedQueryResult> getAllItemsInShort(){
		return itemDao.getAllItemsInShort();
	}
	
}
