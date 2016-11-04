package pl.essay.imangular.model;

import java.util.List;
import pl.essay.generic.dao.GenericDaoHbnImpl;

import org.springframework.stereotype.Repository;

@Repository
public class ItemDaoImpl extends GenericDaoHbnImpl<Item> implements ItemDao {

	public void addComponent(ItemComponent ic){
		Item item = super.load( ic.getParent().getId() );
		item.addComponent( ic );
		this.update(item);
	}

	@Override
	public boolean existsItemByName(String name) {
		Item item = this.getItemByName(name);
		return item != null;
	}
	
	@Override
	public Item getItemByName(String name) {
		Item item = (Item) getSession()
				.getNamedQuery("getItemByName") 
				.setParameter("nameParam", name)
				.uniqueResult();
		return item;
	}

	@Override
	public List<IdNameIsComposedQueryResult> getAllItemsInShort(){
		return (List<IdNameIsComposedQueryResult>) getSession()
				.createQuery(
						"select new pl.essay.imangular.model.IdNameIsComposedQueryResult("+
								"i.id, i.name, i.isComposed) "+
						"from Item i order by i.name") 
				.list();
	}

}
