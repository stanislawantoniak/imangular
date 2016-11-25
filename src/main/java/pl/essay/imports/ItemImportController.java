package pl.essay.imports;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.domain.item.Item;
import pl.essay.imangular.domain.item.ItemComponent;
import pl.essay.imangular.domain.item.ItemService;

/*
 * controller used for import items 
 * 
 * uses non secure data insert methods (insert associated components not updating items)
 */


@RestController
public class ItemImportController {

	@Autowired
	public ItemService itemService;

	@RequestMapping(value = "/importitems", method = RequestMethod.GET)
	public ResponseEntity<String> importItems() {

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
				item.setCanBeSplit( ! product.isBuilding );
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
			System.gc();
		}

		System.gc();

		Collection<ItemComponent> icList = new LinkedHashSet<ItemComponent>();

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

								String remarks = component.desc;

								//if in remarks there is a number then convert string to "1 Etap", "2 Etap" etc
								try { 

									Integer.parseInt(remarks);
									remarks += " Etap"; //this will run only if the remarksholds a number

								} catch( Exception e){
									//just ignore
								}

								ic.setRemarks(remarks);

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
				System.gc();
			}
		}

		this.itemService.addItemComponentFastNotSecure(icList);

		//memory cleanup
		importer = null;
		icList = null;
		System.gc();

		SetWithCountHolder<Item> holder = this.itemService.listItems();
		for (Item item : holder.getCollection()){
			Item i  = this.itemService.getItemById(item.getId());
			i.setIsComposed(!i.getComponents().isEmpty());
			for (ItemComponent ic : i.getUsedIn()){
				if (ic.getParent().getCanBeSplit()){
					i.setIsUsed(true);
					break;
				}
			}
			if (!i.getIsComposed())
				i.setCanBeSplit(false);
			this.itemService.updateItem(i);
			System.gc();

		}

		System.out.println("import done!!!!!!!!!!!!!!!");
		System.out.println(s);

		return new ResponseEntity<String>(s, HttpStatus.OK);
	}

}