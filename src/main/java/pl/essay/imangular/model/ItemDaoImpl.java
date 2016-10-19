package pl.essay.imangular.model;

import org.springframework.stereotype.Repository;

import pl.essay.generic.dao.AbstractDaoHbn;

@Repository
public class ItemDaoImpl extends AbstractDaoHbn<Item> implements ItemDao {

	public void addComponent(ItemComponent ic){
		Item item = super.load( ic.getParent().getId() );
		item.addComponent( ic );
		this.update(item);
	}

	@Override
	public boolean existsItemByName(String name) {
		Item item = (Item) getSession()
				.getNamedQuery("getItemByName") 
				.setParameter("nameParam", name)
				.uniqueResult();

		return item != null;
	}

}
