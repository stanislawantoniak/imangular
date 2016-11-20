package pl.essay.generic.dao;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
	
	private Session session;
	
	public Criteria get(){

		return this.criteria;
	}

	/*
	 * creates blank criteria (for all rows) 
	 */
	public CriteriaBuilder(Session session, Class<T> c){
		this.domainClass = c;
		this.session = session;
		this.criteria = this.session.createCriteria(this.domainClass);
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
	 * key - domain property to filter, associated entities are accepted but only first level
	 * 		like forItem.name is ok, but forItem.gameRelease.name is not 
	 *	 
	 * value == searchstring - string to be filtered by
	 * 
	 * filtersstring fields only adding like %querystring% for each field
	 * 
	 */
	public CriteriaBuilder<T> addAndLikeFilters(Map<String,String> filters) { 

		for (Map.Entry<String, String> pair: filters.entrySet()){

			String key = pair.getKey();
			if (key.contains(".")){
				
				String association = StringUtils.substringBefore(key, ".");
				String alias = StringUtils.substringBefore(key, ".")+"Association";
				String associationProperty = StringUtils.substringAfter(key, ".");
				
				logger.trace("adding association and like filter for:: "+key+" / "+pair.getValue());
				logger.trace("association::",association);
				logger.trace("alias::",alias);
				logger.trace("associationProperty::",associationProperty);
				
				this.criteria
				.createAlias(association, alias)
			    .add( Restrictions
			    		.like( 
			    				alias+"."+associationProperty, 
			    				"%"+pair.getValue()+"%"
			    				) 
			    		);
				
			} else {
			
				logger.trace("adding and like filters :: "+key+" / "+pair.getValue());
				this.criteria
				.add(Restrictions
						.like(key, "%"+pair.getValue()+"%")
						.ignoreCase());
			}
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
