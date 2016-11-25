package pl.essay.imangular.domain.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.security.UserForm;
import pl.essay.generic.controller.BaseController;
import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;


@RestController
public class ItemController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	public ItemService itemService;

	@InitBinder
	public void registerCustomEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Item.class, null, new ItemMakerPropertyEditor(this.itemService));
	}

	@RequestMapping(value = "/itemsxx", method = RequestMethod.GET)
	public SetWithCountHolder<Item> listItems() {
		return this.itemService.listItems();
	}

	/*
	 * this service is called on validate edit entity form
	 * - id => id of edited added item (0 for new entity)
	 * - itemname -> name entered by user
	 * 
	 * service returns ok if entity is found and its id is different than supplied in request
	 * otherwise it returns http_notfound
	 * 
	 */
	@RequestMapping(value = "/itemexists/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Void> userExists(@RequestBody String itemname, @PathVariable int id) {

		ResponseEntity<Void> notFound = new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		ResponseEntity<Void> found = new ResponseEntity<Void>(HttpStatus.OK);

		if (this.itemService.existsItem(itemname))
			if (this.itemService.getItemByName(itemname).getId() != id)
				return found;
			else
				return notFound;
		else
			return notFound;	
	}

	@RequestMapping(value= "/items", method = {RequestMethod.POST})
	public ResponseEntity<SetWithCountHolder<Item>> listItemsWithParams(@RequestBody ListingParamsHolder filter){

		SetWithCountHolder<Item> holder = this.itemService.listItemsPaginated(filter);

		return new ResponseEntity<SetWithCountHolder<Item>>(holder, HttpStatus.OK);

	}


	@RequestMapping(value= "/itemrest/associations/{itemId}", method = {RequestMethod.GET})
	public ResponseEntity<Map<String, Set<ItemComponent>>> usedInItem(@PathVariable int itemId){

		Item item = this.itemService.getItemById(itemId);
		Map<String, Set<ItemComponent>> map = new HashMap<String, Set<ItemComponent>>();
		map.put("components", item.getComponents());
		map.put("usedIn", item.getUsedIn());

		return new ResponseEntity<Map<String, Set<ItemComponent>>>(map, HttpStatus.OK);

	}
	
	//update or add component
	@RequestMapping(value= "/componentrest", method = {RequestMethod.POST})
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Integer> createItemComponent(@RequestBody ItemComponent itemComponent){

		this.itemService.addOrUpdateItemComponent(itemComponent);

		return new ResponseEntity<Integer>(itemComponent.getId(), HttpStatus.OK);
	}

	//update or add component
	@RequestMapping(value= "/componentrest/{id}", method = {RequestMethod.PUT})
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Void> updateItemComponent(@PathVariable int id, @RequestBody ItemComponent itemComponent){

		this.itemService.addOrUpdateItemComponent(itemComponent);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/componentrest/{componentId}", method = {RequestMethod.DELETE})
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Void> deleteItemComponent( @PathVariable("componentId") int componentId	){

		this.itemService.removeItemComponent(componentId);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/itemrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Item> getItem(@PathVariable("id") int id) {
		logger.trace("Fetching Item with id " + id);
		Item item = (id != 0 ? this.itemService.getItemById(id) : new Item());//init item or get from db 
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Void> updateItem(@PathVariable int id, @RequestBody Item item){

		logger.trace("update item data: "+item);

		Item itemFromDB = this.itemService.getItemById(id);
		if (itemFromDB == null){
			logger.trace("Item "+id+" does not exist, update failed");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before update item data: "+itemFromDB);
		this.itemService.updateItem( item );
		logger.trace("after update item data: "+itemFromDB);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/", method = RequestMethod.POST)
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Integer> createItem(@RequestBody Item item){

		logger.trace("create itemform data: "+item);

		if ( this.itemService.existsItem( item.getName() ) ){
			System.out.println("Item with name " + item.getName() + " already exist and requested create");
			return new ResponseEntity<Integer>(0,HttpStatus.CONFLICT);
		}

		logger.trace("before create item data: "+item);
		this.itemService.addItem( item );
		logger.trace("after create item data: "+item);

		return new ResponseEntity<Integer>(item.getId(), HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Void> deleteItem(@PathVariable int id){

		Item item = this.itemService.getItemById(id);
		if (item == null){			 
			logger.trace("Item " +id+ " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before delete item: "+item);
		this.itemService.removeItem(id);
		logger.trace("after delete item: "+item);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/items/forselect/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<ItemIdNameIsComposedQueryResult> > itemsForSelect(@PathVariable("id") int id) {

		return new ResponseEntity<List<ItemIdNameIsComposedQueryResult>>(
				this.itemService.getAllItemsInShort(id, "term"),  
				HttpStatus.OK
				);
	}
}
