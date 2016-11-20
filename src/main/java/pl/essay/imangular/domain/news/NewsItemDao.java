package pl.essay.imangular.domain.news;

import pl.essay.generic.dao.GenericDaoHbn;
import pl.essay.generic.dao.SetWithCountHolder;

public interface NewsItemDao extends GenericDaoHbn<NewsItem> {
	public SetWithCountHolder<NewsItem> listNewsItemsByCategory(String cat);
}
