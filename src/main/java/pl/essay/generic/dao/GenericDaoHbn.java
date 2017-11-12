package pl.essay.generic.dao;

import java.io.Serializable;

public interface GenericDaoHbn<T extends Object> {
	Serializable create(T t);

	void create(Iterable<T> t);

	public T get(Serializable id);

	public T load(Serializable id);

	public SetWithCountHolder<T> getAll();

	public SetWithCountHolder<T> getAll(ListingParamsHolder params);

	public SetWithCountHolder<T> getListByStrictPropertyMatch(String field, Object match);

	void update(T t);

	void delete(T t);

	void deleteById(Serializable id);

	void deleteAll();

	boolean exists(Serializable id);
}
