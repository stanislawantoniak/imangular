package pl.essay.imangular.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.essay.angular.security.UserT;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;
import pl.essay.imangular.service.ItemService;

@RestController
public class ItemController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(ItemController.class);

	@Autowired private ItemService itemService;


	@InitBinder
	public void registerCustomEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Item.class, null, new ItemMakerPropertyEditor(this.itemService));
	}

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public List<Item> listItems() {
		return this.itemService.listItems();
	}

	//update or add component
	@RequestMapping(value= "/items/component", method = {RequestMethod.POST})
	public ResponseEntity<Integer> createItemComponent(@RequestBody ItemComponent itemComponent){

		//Item parent = itemComponent.getParent();
		//parent.addComponent(itemComponent);

		this.itemService.addOrUpdateItemComponent(itemComponent);
		//this.itemService.addOrUpdateItemComponent(itemComponent);

		return new ResponseEntity<Integer>(itemComponent.getId(), HttpStatus.OK);
	}

	//update or add component
	@RequestMapping(value= "/items/component/{id}", method = {RequestMethod.PUT})
	public ResponseEntity<Void> updateItemComponent(@PathVariable int id, @RequestBody ItemComponent itemComponent){

		this.itemService.addOrUpdateItemComponent(itemComponent);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/items/component/{componentId}", method = {RequestMethod.DELETE})
	public ResponseEntity<Void> deleteItemComponent( @PathVariable("componentId") int componentId	){

		this.itemService.removeItemComponent(componentId);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/itemrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Item> getItem(@PathVariable("id") int id) {
		System.out.println("Fetching Item with id " + id);
		Item item = (id != 0 ? this.itemService.getItemById(id) : new Item());//init item or get from db 
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateItem(@PathVariable int id, @RequestBody Item item){

		logger.trace("update item data: "+item);

		Item itemFromDB = this.itemService.getItemById(id);
		if (itemFromDB == null){
			System.out.println("Item "+id+" does not exist, update failed");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		itemFromDB.setName(item.getName());

		logger.trace("before update item data: "+itemFromDB);
		this.itemService.updateItem( itemFromDB );
		logger.trace("after update item data: "+itemFromDB);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/", method = RequestMethod.POST)
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
	public ResponseEntity<Void> deleteItem(@PathVariable int id){

		Item item = this.itemService.getItemById(id);
		if (item == null){			 
			System.out.println("Item " +id+ " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before delete item: "+item);
		this.itemService.removeItem(id);
		logger.trace("after delete item: "+item);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping("/items/item/edit/{id}")
	public String editItem(@PathVariable("id") int id, Model model){
		logger.trace("from controller.editItem");

		Item item =  (id != 0 ? this.itemService.getItemById(id) : new Item());
		model.addAttribute("item", item);

		if (id != 0 ){
			Set<ItemComponent> set =  this.itemService.getItemComponentsByParent(id);
			model.addAttribute("itemComponents", set);
		}
		return "items/itemEdit";
	}

	@RequestMapping(value = "/items/forselect/{id}", method = RequestMethod.GET)
	public ResponseEntity<Map<String,String>> itemsForSelect(@PathVariable("id") int id) {
		return new ResponseEntity<Map<String,String>>(this.getItemListForSelect(id),  HttpStatus.OK);
	}

	//#todo
	//check for any circular reference in item components
	//a is composed of b, b is composed of a
	protected Map<String, String> getItemListForSelect(int id) {
		Map<String,String> allItems = new LinkedHashMap<String, String>();
		Map<String,String> notSorted = new TreeMap<String, String>();
		for (Item i: this.itemService.listItems()){
			if (i.getId() != id ) 
				notSorted.put(i.getName(), ""+i.getId()); //sort by names
		}
		for (Map.Entry<String, String> i : notSorted.entrySet())
			allItems.put(i.getValue(), i.getKey()+" (#"+i.getValue()+")");//put in linked map to save order
		return allItems;
	}

}
