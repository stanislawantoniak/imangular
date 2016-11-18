package pl.essay.imangular.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.security.UserForm;
import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.NewsItem;
import pl.essay.imangular.service.NewsItemService;

@RestController
public class NewsItemController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(NewsItemController.class);

	@Autowired private NewsItemService newsItemService;

	@RequestMapping(value = "/newsitems", method = RequestMethod.GET)
	public SetWithCountHolder<NewsItem> listNewsItems() {
		return this.newsItemService.listNewsItems();
	}
	
	@RequestMapping(value= "/newsitems", method = {RequestMethod.POST})
	public SetWithCountHolder<NewsItem>listNewsItemsWithParams(@RequestBody ListingParamsHolder filter){

		return this.newsItemService.listNewsItemsPaginated(filter); 

	}

	@RequestMapping(value = "/newsitemrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NewsItem> getnews(@PathVariable("id") int id) {
		NewsItem news = (id != 0 ? this.newsItemService.getNewsItemById(id) : new NewsItem());
		return new ResponseEntity<NewsItem>(news, HttpStatus.OK);
	}

	@RequestMapping(value= "/newsitemrest/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<Void> updateNewsItem(@PathVariable long id, @RequestBody NewsItem news){

		this.newsItemService.updateNewsItem(news);;

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/newsitemrest/", method = RequestMethod.POST)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<Integer> createNewsItem(@RequestBody NewsItem news){

		this.newsItemService.addNewsItem(news);
		
		return new ResponseEntity<Integer>(news.getId(), HttpStatus.OK);
	}

	@RequestMapping(value= "/newsitemrest/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<Void> deleteNewsItem(@PathVariable int id){

		NewsItem news = this.newsItemService.getNewsItemById(id);
		if (news == null){			 
			logger.debug("NewsItem " +id+ " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.debug("before delete news: "+news);
		this.newsItemService.removeNewsItem(id);
		logger.debug("after delete news: "+news);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}


}
