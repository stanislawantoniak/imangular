package pl.essay.imangular.model;

import java.util.Set;

import pl.essay.generic.dao.Dao;

public interface ItemComponentDao extends Dao<ItemComponent>{

	public Set<ItemComponent> getItemComponentsByParent(int id);
	
}
