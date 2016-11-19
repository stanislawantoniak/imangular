package pl.essay.imangular.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.NewsItem;
import pl.essay.imangular.model.NewsItemDao;

@Service
@Transactional
public class NewsItemServiceImpl implements NewsItemService{
	
	@Autowired
	private NewsItemDao newsItemDao;

	@Override
	public int addNewsItem(NewsItem news) {
		this.newsItemDao.create(news);
		return news.getId();
	}

	@Override
	public void updateNewsItem(NewsItem news) {
		this.newsItemDao.update(news);
	}

	@Override
	public SetWithCountHolder<NewsItem> listNewsItems() {
		return this.newsItemDao.getAll();
	}

	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<NewsItem> listNewsItemsByCategory(String cat) {
		return this.newsItemDao.getAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<NewsItem> listNewsItemsPaginated(ListingParamsHolder params){
		return this.newsItemDao.getAll(params);
	}

	@Override
	@Transactional(readOnly = true)
	public NewsItem getNewsItemById(int id) {
		return this.newsItemDao.get(id);
	}

	@Override
	public void removeNewsItem(int id) {
		this.newsItemDao.deleteById(id);
	} 

}
