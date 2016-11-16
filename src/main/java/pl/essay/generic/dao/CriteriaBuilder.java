package pl.essay.generic.dao;

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriteriaBuilder<T extends Object> {
	
	protected static final Logger logger = LoggerFactory.getLogger(CriteriaBuilder.class);

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

			logger.trace("adding sort order :: "+order.getPropertyName()+" / is ascending ::"+order.isAscending());
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
	public CriteriaBuilder<T> addAndLikeFilters(Map<String,String> filters) { 

		for (Map.Entry<String, String> pair: filters.entrySet()){

			Criterion restriction = Restrictions
					.like(pair.getKey(), "%"+pair.getValue()+"%")
					.ignoreCase();

			logger.trace("adding and like filters :: "+pair.getKey()+" / "+pair.getValue());
			this.criteria
			.add(restriction); 
		} 
		return this;
	} 

	/*
	 * add a filter for strict matching
	 * field - domain property to filter
	 * match - string to be filtered by
	 * 
	 */
	public CriteriaBuilder<T> addStrictMatchingFilter(String field, Object match) { 


		Criterion restriction = Restrictions
				.eq(field, match);

		logger.trace("adding strict matching filter for field :: "+field+" == "+match);
		this.criteria
		.add(restriction); 

		return this;
	} 


	public CriteriaBuilder<T> addPagination(int pageNo, int pageSize) { 

		this.criteria
		.setFirstResult((pageNo - 1) * pageSize)
		.setMaxResults(pageSize);

		return this;
	}


}
