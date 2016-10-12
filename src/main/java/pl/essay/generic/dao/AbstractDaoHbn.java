package pl.essay.generic.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

//from Spring in Practice::Joshua White,Willie Wheeler

@Transactional
public abstract class AbstractDaoHbn<T extends Object> implements Dao<T> {

	//@Autowired 
	private SessionFactory sessionFactory;
	private Class<T> domainClass;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public SessionFactory getSessionFactory() {
	    if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
	        throw new NullPointerException("Not a hibernate factory exception");
	    }
	    return entityManagerFactory.unwrap(SessionFactory.class);
	}
	
	public void init(){
		this.sessionFactory = this.getSessionFactory();
		System.out.println("in init @@@@@@@@@@@@@@@@@@@@@@");
	}

	protected Session getSession() {
		try {
			return getSessionFactory().getCurrentSession();
		} 
		catch (HibernateException e) 
		{
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
		System.out.println("adding entity "+this.getDomainClassName()+"::"+t);
	}

	public T get(Serializable id) {
		return (T) getSession().get(getDomainClass(), id);
	}

	public T load(Serializable id) {
		return (T) getSession().load(getDomainClass(), id);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return getSession()
				.createQuery("from " + getDomainClassName())
				.getResultList();
	}

	public void update(T t) { getSession().update(t); }

	public void delete(T t) { getSession().delete(t); }

	public void deleteById(Serializable id) { delete(load(id)); }

	public void deleteAll() {
		getSession()
		.createQuery("delete " + getDomainClassName())
		.executeUpdate();
	}

	public long count() {
		return (Long) getSession()
				.createQuery("select count(*) from " + getDomainClassName())
				.getSingleResult();
	}

	public boolean exists(Serializable id) { return (get(id) != null); }

}
