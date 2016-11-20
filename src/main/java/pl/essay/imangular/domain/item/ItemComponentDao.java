package pl.essay.imangular.domain.item;

import java.util.Set;

import pl.essay.generic.dao.GenericDaoHbn;

public interface ItemComponentDao extends GenericDaoHbn<ItemComponent>{

	public Set<ItemComponent> getItemComponentsByParent(int id);
	
}
