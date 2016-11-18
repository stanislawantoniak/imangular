package pl.essay.imangular.model;

import pl.essay.generic.dao.CriteriaBuilder;
import pl.essay.generic.dao.GenericDaoHbnImpl;
import pl.essay.generic.dao.SetWithCountHolder;

import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

@Repository
public class NewsItemDaoImpl extends GenericDaoHbnImpl<NewsItem> implements NewsItemDao {

	@Override
	public SetWithCountHolder<NewsItem> listNewsItemsByCategory(String cat){

		CriteriaBuilder<NewsItem> criteriaBuilder = this.getCriteriaBuilder();
		criteriaBuilder.addStrictMatchingFilter("category", cat);

		CriteriaBuilder<NewsItem> criteriaBuilderForCount = this.getCriteriaBuilder();
		criteriaBuilderForCount.addStrictMatchingFilter("category", cat);

		return 	this.getSetWithCountHolder(

				criteriaBuilder.get(), 
				
				(long) criteriaBuilderForCount
				.get()
				.setProjection(Projections.rowCount())
				.uniqueResult()
				);

	}
}
