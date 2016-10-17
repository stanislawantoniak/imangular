package pl.essay.imangular.controller;

import java.beans.PropertyEditorSupport;


import org.springframework.beans.factory.annotation.Autowired;

import pl.essay.imangular.model.Item;
import pl.essay.imangular.service.ItemService;

public class ItemMakerPropertyEditor extends PropertyEditorSupport {

	@Autowired private ItemService itemService;

	public ItemMakerPropertyEditor(ItemService ps){
		this.itemService = ps;
	}
	
	//transform item id into Item object 
    public void setAsText(String id) {
    	//System.out.println("inside property editor id: "+id);
    	//System.out.println(this.itemService);
    	
    	try {
    		int i = Integer.valueOf(id);
    		Item item = this.itemService.getItemById(i); 
            setValue(item);
            System.out.println(item);
            
    	} catch (Exception e) {
    		setValue(null);
    	}
    	    	
    }
}