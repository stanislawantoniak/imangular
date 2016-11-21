package pl.essay.generic.servicefacade;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.GenericDaoHbn;
import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

@Transactional
public class GenericServiceImpl<T extends Object> implements GenericService<T> {

	@Autowired
	private GenericDaoHbn<T> dao;
		
	@Override
	public Serializable addEntity(T e) {
		return this.dao.create( e );
	}

	@Override
	public void updateEntity(T e) {
		this.dao.update( e );
	}

	@Override
	public SetWithCountHolder<T> listEntities() {
		return this.dao.getAll();
	}

	@Override
	public SetWithCountHolder<T> listEntitiesPaginated(ListingParamsHolder params) {
		return this.dao.getAll( params );
	}

	@Override
	public T getEntityById(int id) {
		return this.dao.get( id );
	}

	@Override
	public void removeEntity(int id) {
		this.dao.deleteById( id );
	}
}
