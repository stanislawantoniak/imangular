package pl.essay.imangular.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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
	
	@Override
	public void addItemComponentFastNotSecure(Iterable<ItemComponent> ic){
		this.itemComponentDao.create(ic);
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<Item> listItems(){
		return this.itemDao.getAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Item> listItemsPaginated(int pageNo, int pageSize,  String sortBy, String dir){
		return this.itemDao.getWithPagination(pageNo, pageSize, sortBy, dir);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long getCount(){
		return this.itemDao.count();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Item getItemById(int id){
		Item item = this.itemDao.get(id);
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
		item.getComponents().remove(ic);
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
