package pl.essay.generic.dao;

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CriteriaBuilder<T extends Object> {

	private Class<T> domainClass;

	private Criteria criteria; 
	public Criteria get(){
		
		return this.criteria;
	}

	/*
	 * creates blank criteria (for all rows) 
	 */
	public CriteriaBuilder(Session session, Class<T> c){
		this.domainClass = c;
		this.criteria = session.createCriteria(this.domainClass);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
	};

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * accepts map for string pairs:
	 * sortColumn - domain property 
	 * sortDirection - asc for ascending, any other for descending order, it is always uppercased so do not worry about case
	 * 
	 * if the map is null or empty = ignored, returns builder
	 ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	public CriteriaBuilder<T> addSortOrder(Map<String,String> sortFields) { 

		if (sortFields == null || sortFields.isEmpty())
			return this;

		for (Map.Entry<String, String> pair: sortFields.entrySet()){
			Order order; 
			if ("ASC".equals(pair.getValue().toUpperCase())){ 
				order = Order.asc(pair.getKey()); 
			} else { 
				order = Order.desc(pair.getKey()); 
			} 
			this.criteria
			.addOrder(order); 

			System.out.println("adding sort order :: "+order.getPropertyName()+" / is ascending ::"+order.isAscending());
		}

		return this;
	} 

	/*
	 * accepts map for string pairs:
	 * field - domain property to filter
	 * searchstring - string to be filtered by
	 * 
	 * filtersstring fields only adding like %querystring% for each field
	 * 
	 */
	public CriteriaBuilder<T> addFilters(Map<String,String> filters) { 

		for (Map.Entry<String, String> pair: filters.entrySet()){

			Criterion restriction = Restrictions
					.like(pair.getKey(), "%"+pair.getValue()+"%")
					.ignoreCase();

			System.out.println("adding sort order :: "+pair.getKey()+" / "+pair.getValue());
			this.criteria
			.add(restriction); 
		} 
		return this;
	} 

	public CriteriaBuilder<T> addPagination(int pageNo, int pageSize) { 

		this.criteria
		.setFirstResult((pageNo - 1) * pageSize)
		.setMaxResults(pageSize);

		return this;
	}


}
