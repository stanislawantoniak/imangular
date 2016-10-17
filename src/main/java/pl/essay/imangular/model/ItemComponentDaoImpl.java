package pl.essay.imangular.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import pl.essay.generic.dao.AbstractDaoHbn;


@Repository
public class ItemComponentDaoImpl extends AbstractDaoHbn<ItemComponent> implements ItemComponentDao{

	@Override
	public ItemComponent load(Serializable id) {
		ItemComponent ic = super.load(id);
		ic.getParent();
		ic.getComponent();
		return ic;
	}

	public Set<ItemComponent> getItemComponentsByParent(int id) {
		System.out.println("getItemComponentsByParent");
		return new HashSet<ItemComponent>(
				(List<ItemComponent>)
				getSession()
				.getNamedQuery("getComponentsByParent") 
				.setParameter("id", id)
				.list()
				);
	}

}
