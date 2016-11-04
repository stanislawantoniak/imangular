package pl.essay.generic.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDaoHbn<T extends Object> {
	void create(T t);
	void create(Iterable<T> t);
	T get(Serializable id);
	T load(Serializable id);
	List<T> getAll();
	public List<T> getAll(String sortColumn, String sortDirection);
	public List<T> getWithPagination(int pageNo, int pageSize);
	public List<T> getWithPagination(int pageNo, int pageSize, String sortColumn, String sortDirection);
	void update(T t);
	void delete(T t);
	void deleteById(Serializable id);
	void deleteAll();
	long count();
	boolean exists(Serializable id);
}

