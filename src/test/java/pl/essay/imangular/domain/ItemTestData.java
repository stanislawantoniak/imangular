package pl.essay.imangular.domain;

import java.util.HashMap;
import java.util.Map;

import pl.essay.imangular.domain.bom.BillOfMaterial;
import pl.essay.imangular.domain.item.Item;
import pl.essay.imangular.domain.item.ItemComponent;


/*
 * A 
 * 		2 * B
 * 		3 * C
 * 		1 * D
 * B
 * 		2 * D
 * 		3 * E
 * C
 * 		2 * B
 * 		1 * F
 * 
 * Full A
 * 		2 * B (2)	
 * 				2 * D (4)
 * 				3 * E (6)
 * 		3 * C (3)
 * 				2 * B (6)
 * 						2 * D (12)
 * 						3 * E (18)
 * 				1 * F (3)
 * 		1 * D (1)
 * 
 * flat list for A required quantity 1:
 *  B = 2+6 = 8
 *  C = 3
 *  D = 4+12+1=17
 *  E = 6+18=24
 * 	F = 3 
 * */

public class ItemTestData {

	IdCounter counter = new IdCounter();

	Map<String,Item> items = new HashMap<String,Item>();
	Map<Integer,Item> itemsById = new HashMap<Integer,Item>();

	public ItemTestData(){
		//make new items
		String[] itemNames = new String[]{"A","B","C","D","E","F"};
		for (String name : itemNames)
			item(name); 
		/*make structure A 
		 * 		2 * B
		 * 		3 * C
		 * 		1 * D
		 */
		item("A").addComponent(ic(item("B"), 2));
		item("A").addComponent(ic(item("C"), 3));
		item("A").addComponent(ic(item("D"), 1));
		/* 
		 *  B
		 * 		2 * D
		 * 		3 * E
		 */
		item("B").addComponent(ic(item("D"), 2));
		item("B").addComponent(ic(item("E"), 3));
		/*
		 * C
		 * 		2 * B
		 * 		1 * F
		 */
		item("C").addComponent(ic(item("B"), 2));
		item("C").addComponent(ic(item("F"), 1));
		
		System.out.println("Test data:");
		print("A",0);
	}

	public void print(String name, int level){
		dots(level);
		System.out.println("Item "+item(name).getName());
		for(ItemComponent ic: item(name).getComponents()){
			dots(level);
			System.out.println("----"+ic.getQuantity()+" * "+ic.getComponent().getName());
			if (ic.getComponent().getIsComposed()) 
				print(ic.getComponent().getName(), level+2);
		}
			
	}
	
	private void dots(int level){
		for (int i = 0; i < level; i++)
			System.out.print("----");
	}

	BillOfMaterial getSampleBom(){
		BillOfMaterial bom = new BillOfMaterial();

		return bom;
	}

	public Item item(String name){
		//get from map if exists
		if (items.containsKey(name))
			return items.get(name);
		//make new item
		Item item = new Item();
		item.setId(counter.get());
		item.setName(name);
		//put item to maps
		items.put(name, item);
		itemsById.put(item.getId(), item);
		return item;
	}

	private ItemComponent ic(Item item, int qty){
		ItemComponent ic = new ItemComponent();
		ic.setId(counter.get());
		ic.setComponent(item);
		ic.setQuantity(qty);
		return ic;
	}

	public Item item(Integer id){
		return itemsById.get(id); 
	}
}
