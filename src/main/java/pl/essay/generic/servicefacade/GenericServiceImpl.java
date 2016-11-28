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
	protected GenericDaoHbn<T> templateEntityDao;
		
	@Override
	public Serializable addEntity(T e) {
		return this.templateEntityDao.create( e );
	}

	@Override
	public void updateEntity(T e) {
		this.templateEntityDao.update( e );
	}

	@Override
	public SetWithCountHolder<T> listEntities() {
		return this.templateEntityDao.getAll();
	}

	@Override
	public SetWithCountHolder<T> listEntitiesPaginated(ListingParamsHolder params) {
		return this.templateEntityDao.getAll( params );
	}

	@Override
	public T getEntityById(Serializable id) {
		return this.templateEntityDao.get( id );
	}

	@Override
	public void removeEntity(Serializable id) {
		this.templateEntityDao.deleteById( id );
	}
}
