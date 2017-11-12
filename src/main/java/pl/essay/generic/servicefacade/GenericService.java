package pl.essay.generic.servicefacade;

import java.io.Serializable;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

public interface GenericService<T extends Object> {

	public Serializable addEntity(T i);

	public void updateEntity(T i);

	public SetWithCountHolder<T> listEntities();

	public SetWithCountHolder<T> listEntitiesPaginated(ListingParamsHolder params);

	public T getEntityById(Serializable id);

	public void removeEntity(Serializable id);

}
