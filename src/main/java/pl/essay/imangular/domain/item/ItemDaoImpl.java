package pl.essay.imangular.domain.item;

import java.util.List;
import pl.essay.generic.dao.GenericDaoHbnImpl;

import org.springframework.stereotype.Repository;

@Repository
public class ItemDaoImpl extends GenericDaoHbnImpl<Item> implements ItemDao {

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

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemIdNameIsComposedQueryResult> getAllItemsInShort(){
		return (List<ItemIdNameIsComposedQueryResult>) getSession()
				.createQuery(
						"select new pl.essay.imangular.domain.item.ItemIdNameIsComposedQueryResult("+
								"i.id, i.name, i.isComposed) "+
						"from Item i order by i.name") 
				.list();
	}

}
