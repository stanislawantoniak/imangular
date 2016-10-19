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

	@RequestMapping(value= "/items/item/update", method = RequestMethod.POST)
	public String updateItem(@Valid @ModelAttribute("item") Item item,  
			BindingResult result, //must follow modelattribute!!!!
			RedirectAttributes redirectAttributes,
			Model model){
		logger.trace("update item data: "+item);
		logger.trace("has errors?:"+result.hasFieldErrors());
		if (result.hasErrors()) {
			//disable language selector - because staying on the same url and it doesnt support RequestMethod.GET
			model.addAttribute("languageSelectorClass","disabled"); 

			model.addAttribute("item", item);

			model.addAttribute("itemComponents", this.itemService.getItemComponentsByParent(item.getId()));
			return "items/itemEdit";
		} else {
			if (item.getId() == 0)
				this.itemService.addItem(item);
			else
				this.itemService.updateItem(item);
			return  "redirect:/items" ;
		}
	}

	//update or add component
	@RequestMapping(value= "/items/component/update", method = RequestMethod.POST)
	public String updateItemComponent(@Valid @ModelAttribute("itemComponent") ItemComponent itemComponent,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes){

		if (result.hasErrors()){
			System.out.println("component: "+itemComponent.getComponent());
			System.out.println("parent: "+itemComponent.getParent());

			//disable language selector - because staying on the same url and it doesnt support RequestMethod.GET
			model.addAttribute("languageSelectorClass","disabled"); 

			model.addAttribute("allItems", getItemListForSelect(itemComponent.getParent()));
			model.addAttribute("item", itemComponent.getParent());
			model.addAttribute("itemComponent", itemComponent);

			return "items/componentEdit";
		} else {

			this.itemService.addItemComponent(itemComponent);

			return  "redirect:/items/item/edit/"+itemComponent.getParent().getId();
		}
	}

	@RequestMapping("/items/component/delete/{itemId}-{componentId}")
	public String deleteItemComponent(
			@PathVariable("itemId") int itemId,
			@PathVariable("componentId") int componentId
			){

		this.itemService.removeItemComponent(componentId);

		return "redirect:/items/item/edit/"+itemId;
	}

	//edit existing component
	//in path id component id passed
	@RequestMapping("/items/component/add/{id}")
	public String addItemComponent(@PathVariable("id") int itemId, Model model){
		logger.trace("from controller.addItemComponent");


		ItemComponent ic = new ItemComponent();
		Item item = this.itemService.getItemById(itemId);
		ic.setParent(item);

		model.addAttribute("allItems", getItemListForSelect(item));
		model.addAttribute("item", item);
		model.addAttribute("itemComponent", ic);

		return "items/componentEdit";
	}

	//edit existing component
	//in path id item id passed
	@RequestMapping("/items/component/edit/{id}")
	public String editItemComponent(@PathVariable("id") int id, Model model){

		ItemComponent ic = this.itemService.getItemComponent(id);
		System.out.println("component in controller: "+ic);


		int itemId = ic.getParent().getId();
		Item item = this.itemService.getItemById( itemId );

		model.addAttribute("allItems", getItemListForSelect(item));
		model.addAttribute("item", item);
		model.addAttribute("itemComponent", ic);
		return "items/componentEdit";
	}

	@RequestMapping(value = "/itemrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Item> getItem(@PathVariable("id") int id) {
		System.out.println("Fetching Item with id " + id);
		Item item = (id != 0 ? this.itemService.getItemById(id) : new Item());//init item or get from db 
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Item> updateItem(@PathVariable int id, @RequestBody Item item){

		logger.trace("update itemform data: "+item);

		Item test = this.itemService.getItemById(id);
		if (test == null){
			System.out.println("Item "+id+" does not exist, update failed");
			return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
		}
		
		logger.trace("before update item data: "+item);
		this.itemService.updateItem( item );
		logger.trace("after update item data: "+item);
		
		return new ResponseEntity<Item>(HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/", method = RequestMethod.POST)
	public ResponseEntity<Void> createItem(@RequestBody Item item){

		logger.trace("create itemform data: "+item);
		
		if ( this.itemService.existsItem( item.getName() ) ){
			 System.out.println("Item with name " + item.getName() + " already exist and requested create");
	            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
		
		logger.trace("before create item data: "+item);
		this.itemService.addItem( item );
		logger.trace("after create item data: "+item);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
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

	//#todo
	//check for any circular reference in item components
	//a is composed of b, b is composed of a
	protected Map<String, String> getItemListForSelect(Item exclude) {
		Map<String,String> allItems = new LinkedHashMap<String, String>();
		Map<String,String> notSorted = new TreeMap<String, String>();
		for (Item i: this.itemService.listItems()){
			if (i.getId() != exclude.getId()) 
				notSorted.put(i.getName(), ""+i.getId()); //sort by names
		}
		for (Map.Entry<String, String> i : notSorted.entrySet())
			allItems.put(i.getValue(), i.getKey()+" (#"+i.getValue()+")");//put in linked map to save order
		return allItems;
	}

}
