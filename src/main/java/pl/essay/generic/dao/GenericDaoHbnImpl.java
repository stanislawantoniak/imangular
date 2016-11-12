package pl.essay.generic.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import pl.essay.imangular.model.BillOfMaterial;

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

	protected CriteriaBuilder<T> getCriteriaBuilder(){
		CriteriaBuilder<T> criteriaBuilder = new CriteriaBuilder<T>(this.getSession(),this.getDomainClass());
		return criteriaBuilder;
	}


	/*
	 * get set with list and total count
	 */
	@SuppressWarnings("unchecked")
	protected SetWithCountHolder<T> getSetWithCountHolder(Criteria finalCriteria, long count){
		return new SetWithCountHolder<T>( 
				finalCriteria
				.list(),
				count
				);
	}
	

	protected long getTotalRowsOnCriteria(Criteria criteria){
		return (long) criteria
				.setProjection(Projections.rowCount())
				.uniqueResult();
	}

	public SetWithCountHolder<T> getAll() {
		
		long totalRows = this.getTotalRowsOnCriteria(this.getCriteriaBuilder().get());
		
		return this.getSetWithCountHolder(this.getCriteriaBuilder().get(), totalRows);
	}


	public SetWithCountHolder<T> getAll(ListingParamsHolder params) {

		Criteria criteriaForCount = this
				.getCriteriaBuilder()
				.addAndLikeFilters(params.filterFields)
				.get();

		long totalRows = this.getTotalRowsOnCriteria(criteriaForCount);
		
		Criteria finalCriteria = this
				.getCriteriaBuilder()
				.addAndLikeFilters(params.filterFields)
				.addSortOrder(params.sortOrderFields)
				.addPagination(params.pageNo, params.pageSize)
				.get();

		return this.getSetWithCountHolder(finalCriteria, totalRows);
	}
	
	public SetWithCountHolder<T> getListByStrictPropertyMatch(String field, Object match){

		return this.getSetWithCountHolder(
						
						this.getCriteriaBuilder()
						.addStrictMatchingFilter(field, match)
						.get(), 

						this.getTotalRowsOnCriteria(
								this.getCriteriaBuilder()
								.addStrictMatchingFilter(field, match)
								.get()
								)
						);

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

	public boolean exists(Serializable id) { return (get(id) != null); }

}