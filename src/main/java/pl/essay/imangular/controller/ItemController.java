package pl.essay.imangular.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
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
import pl.essay.imangular.model.IdNameIsComposedQueryResult;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;
import pl.essay.imangular.service.ItemService;
import pl.essay.imports.Product;
import pl.essay.imports.XLSProductsImporter;
import pl.essay.imports.Product.Component;

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
	
	@RequestMapping(value = "/items/{pageNo}/{pageSize}/{sortBy}", method = RequestMethod.GET)
	public List<Item> listItems( @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize
			, @PathVariable("sortBy") String sortBy) {
	
		String direction = ( "+".equals( StringUtils.substring(sortBy,0, 1) ) ? "asc" : "desc" );
		sortBy = StringUtils.substring(sortBy, 1);
		
		return this.itemService.listItemsPaginated(pageNo, pageSize, sortBy, direction);
	}
	
	@RequestMapping(value = "/itemscount", method = RequestMethod.GET)
	public ResponseEntity<Long> countItems() {
		Long count = this.itemService.getCount();
		System.out.println("items count:: " + count);
		return new ResponseEntity<Long>(count, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/importitems", method = RequestMethod.GET)
	public ResponseEntity<Void> importItems() {

		XLSProductsImporter importer = new XLSProductsImporter();
		importer.importFile("skladniki.xlsx");

		String s = "";
		//add items with no components - they will be added in step 2- when all item are existing
		for (Map.Entry<String,Product> p : importer.getProducts().entrySet()){
			System.out.print(p.getKey()+":: "+p.getValue());
			Product product = p.getValue();
			if (! this.itemService.existsItem(product.name)){
				Item item = new Item();
				item.setName(product.name);
				item.setIsBuilding( product.isBuilding);
				item.setIsAvailableInOtherSources(product.otherSources);
				item.setOtherSources(product.otherSourceName);
				item.setWhereManufactured(product.whereManufactured);
				int id = this.itemService.addItem(item);
				product.itemId = id;
			} else {
				Item item = this.itemService.getItemByName(product.name);
				product.itemId = item.getId();
				s += "product exists: "+product.id+"\n";
			}
		}
		
		List<ItemComponent> icList = new ArrayList<ItemComponent>();

		//add components - fast not secure
		for (Map.Entry<String,Product> p : importer.getProducts().entrySet()){
			System.out.print(p.getKey()+":: "+p.getValue());
			Product product = p.getValue();
			if (product.isComposed){

				Item parent = this.itemService.getItemById(product.itemId);

				if (parent != null){

					for (Map.Entry<String, Product.Component> c : product.components.entrySet()){
						Product.Component component = c.getValue();
						ItemComponent ic = new ItemComponent();
						if (!component.cid.equals("") && component.quantity > 0){
							Product componentProduct = importer.getProducts().get(component.cid);
							if (componentProduct != null){
								Item componentItem = this.itemService.getItemById(componentProduct.itemId);

								ic.setParent(parent);
								ic.setComponent(componentItem);
								ic.setQuantity(component.quantity);
								ic.setRemarks(component.desc);

								if (ic.getParent() != null 
										&& ic.getComponent()!= null
										&& ic.getQuantity() > 0 
										&& ic.getRemarks() != null)
									icList.add(ic);
								else 
									s += "error in component "+componentProduct+"\n";
							} 
						}
					}
				} else 
					s+= "item for product not found "+product+"\n";
			}
		}
		
		this.itemService.addItemComponentFastNotSecure(icList);
		
		List<Item> list = this.itemService.listItems();
		for (Item item : list){
			item.setIsComposed(item.getComponents().size() > 0);
			this.itemService.updateItem(item);
		}

		System.out.println("import done!!!!!!!!!!!!!!!");
		System.out.println(s);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	//update or add component
	@RequestMapping(value= "/componentrest", method = {RequestMethod.POST})
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public ResponseEntity<Integer> createItemComponent(@RequestBody ItemComponent itemComponent){

		//Item parent = itemComponent.getParent();
		//parent.addComponent(itemComponent);

		this.itemService.addOrUpdateItemComponent(itemComponent);
		//this.itemService.addOrUpdateItemComponent(itemComponent);

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
		System.out.println("Fetching Item with id " + id);
		Item item = (id != 0 ? this.itemService.getItemById(id) : new Item());//init item or get from db 
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}

	@RequestMapping(value= "/itemrest/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
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
			System.out.println("Item " +id+ " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before delete item: "+item);
		this.itemService.removeItem(id);
		logger.trace("after delete item: "+item);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/items/forselect/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<IdNameIsComposedQueryResult>> itemsForSelect(@PathVariable("id") int id) {
		return new ResponseEntity<List<IdNameIsComposedQueryResult>>(this.getItemListForSelect(id),  HttpStatus.OK);
	}

	//#todo
	//check for any circular reference in item components
	//a is composed of b, b is composed of a
	protected List<IdNameIsComposedQueryResult> getItemListForSelect(int id) {
		List<IdNameIsComposedQueryResult> allItems = new LinkedList<IdNameIsComposedQueryResult>();
		for (IdNameIsComposedQueryResult i : this.itemService.getAllItemsInShort())
			if (i.id != id ) 
				allItems.add(i);//put in linked map to save order
		return allItems;
	}

}
