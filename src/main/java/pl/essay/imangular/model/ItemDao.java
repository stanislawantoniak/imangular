package pl.essay.imangular.model;

import pl.essay.generic.dao.Dao;

public interface ItemDao extends Dao<Item> {

	public void addComponent(ItemComponent ic);
	
}
