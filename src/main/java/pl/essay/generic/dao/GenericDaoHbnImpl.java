package pl.essay.generic.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.poi.ss.usermodel.Row;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

//from Spring in Practice::Joshua White,Willie Wheeler

@Transactional
public abstract class GenericDaoHbnImpl<T extends Object> implements GenericDaoHbn<T> {

	private Class<T> domainClass;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {

		if (this.sessionFactory == null){
			if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
				throw new NullPointerException("Not a hibernate factory exception");
			}
			this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		}

		return this.sessionFactory;
	}

	private String getDefaultSortColumn(){

		String sortColumn = "";

		Method method = ReflectionUtils
				.findMethod(
						getDomainClass(), 
						"getDafaulSortColumn"
						);
		if (method != null) {
			try {
				T t = this.getDomainClass().newInstance(); //get instance of generic class
				sortColumn = (String) method.invoke( t );
			} catch (Exception e) {} //ignore
		}

		return sortColumn;
	}

	protected Session getSession() {
		try {
			//System.out.println("get current session success");
			return getSessionFactory().getCurrentSession();
		} 
		catch (HibernateException e) 
		{
			//System.out.println("get current session fail");
			return getSessionFactory().openSession();
		}
	}

	@SuppressWarnings("unchecked")
	private Class<T> getDomainClass() {
		if (domainClass == null) {
			ParameterizedType thisType =
					(ParameterizedType) getClass().getGenericSuperclass();
			this.domainClass =
					(Class<T>) thisType.getActualTypeArguments()[0];
		}
		return domainClass;
	}

	protected String getDomainClassName() {
		return getDomainClass().getName();
	}

	public void create(T t) {
		Method method = ReflectionUtils.findMethod(
				getDomainClass(), "setDateCreated",
				new Class[] { Date.class });
		if (method != null) {
			try {
				method.invoke(t, new Date());
			} catch (Exception e) { /* Ignore */ }
		}
		getSession().save(t);
		//System.out.println("adding entity "+this.getDomainClassName()+"::"+t);
	}

	public void create(Iterable<T> entities) {
		Iterator<T> iterator = entities.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			this.create(t);
			//System.out.println("adding entity "+this.getDomainClassName()+"::"+t);
		}
	}

	public T get(Serializable id) {
		return (T) getSession().get(getDomainClass(), id);
	}

	public T load(Serializable id) {
		return (T) getSession().load(getDomainClass(), id);
	}

	private Query getSimpleQuery(){
		return getSession()
				.createQuery("from " + getDomainClassName());
	}

	private Criteria getCriteria(String sortColumn, String sortDirection){
		CriteriaBuilder<T> criteriaBuilder = new CriteriaBuilder<T>(this.getSession(),this.getDomainClass());
		return criteriaBuilder
				.addSortOrder(sortColumn, sortDirection);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {

		String defaultSortColumn = this.getDefaultSortColumn();

		if (!"".equals(defaultSortColumn))
			return this
					.getAll(defaultSortColumn, "asc");
		else
			return this
					.getSimpleQuery()
					.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(String sortColumn, String sortDirection) {
		return this
				.getCriteria(sortColumn, sortDirection)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> getWithPagination(int pageNo, int pageSize) {

		String defaultSortColumn = this.getDefaultSortColumn();

		System.out.println("default order:: "+defaultSortColumn);
		if (!"".equals(defaultSortColumn))
			return this
					.getWithPagination(pageNo, pageSize, defaultSortColumn, "asc");
		else
			return this
					.getSimpleQuery()
					.setFirstResult((pageNo - 1) * pageSize)
					.setMaxResults(pageSize)
					.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> getWithPagination(int pageNo, int pageSize, String sortColumn, String sortDirection) {

		System.out.println("getWithPagination params:: "+ pageNo +" : "+pageSize+" : "+sortColumn+" : "+sortDirection);
			return this
					.getCriteria(sortColumn, sortDirection)
					.setFirstResult((pageNo - 1) * pageSize)
					.setMaxResults(pageSize)
					.list();
	}

	public void update(T t) { 
		//System.out.println("updating object "+t.getClass()+"::"+t.toString());
		Session session = getSession();
		session.update(t);
		//session.flush();
	}

	public void delete(T t) { 
		Session session = getSession();
		session.delete(t); 
		session.flush();
	}

	public void deleteById(Serializable id) { 
		Session session = getSession();
		T obj = session.load(getDomainClass(), id);
		session.delete(obj);
	}

	public void deleteAll() {
		getSession()
		.createQuery("delete " + getDomainClassName())
		.executeUpdate();
	}

	public long count() {
		return (Long) getSession()
				.createQuery("select count(*) from " + getDomainClassName())
				.uniqueResult();
	}

	public boolean exists(Serializable id) { return (get(id) != null); }

}
