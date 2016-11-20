package pl.essay.imangular.domain.news;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

public interface NewsItemService {

	public int addNewsItem(NewsItem i);
	public void updateNewsItem(NewsItem i);
	public SetWithCountHolder<NewsItem> listNewsItems();
	public SetWithCountHolder<NewsItem> listNewsItemsByCategory(String cat);
	public SetWithCountHolder<NewsItem> listNewsItemsPaginated(ListingParamsHolder params);
	public NewsItem getNewsItemById(int id);
	public void removeNewsItem(int id);
	
}
