package pl.essay.generic.dao;

import java.io.Serializable;
import java.util.Collection;

public interface GenericDaoHbn<T extends Object> {
	void create(T t);
	void create(Iterable<T> t);
	public T get(Serializable id);
	public T load(Serializable id);
	public SetWithCountHolder getAll();
	public SetWithCountHolder<T> getAll(ListingParamsHolder params) ;
	void update(T t);
	void delete(T t);
	void deleteById(Serializable id);
	void deleteAll();
	boolean exists(Serializable id);
}

