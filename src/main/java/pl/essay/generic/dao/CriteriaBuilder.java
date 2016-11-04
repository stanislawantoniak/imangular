package pl.essay.generic.dao;

import java.lang.reflect.ParameterizedType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.util.StringUtils;

public class CriteriaBuilder<T extends Object> {

	private Class<T> domainClass;

	private Criteria criteria; 
	public Criteria getCriteria(){
		return this.criteria;
	}
	

	public CriteriaBuilder(Session session, Class<T> c){
		this.domainClass = c;
		this.criteria = session.createCriteria(this.domainClass);
	};

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * sortColumn - domain property 
	 * sortDirection - asc for ascending, any other fordescending order, it is always uppercased so do not worry about case
	 * 
	 ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	public Criteria addSortOrder(String sortColumn, String sortDirection) { 

		if (StringUtils.hasText(sortColumn)) { 
			Order order; 
			if ("ASC".equals(sortDirection.toUpperCase())){ 
				order = Order.asc(sortColumn); 
			} else { 
				order = Order.desc(sortColumn); 
			} 
			System.out.println("adding cort order "+sortColumn+" / "+sortDirection);
			this.criteria.addOrder(order); 
		} 
		return this.criteria;
	} 
}
