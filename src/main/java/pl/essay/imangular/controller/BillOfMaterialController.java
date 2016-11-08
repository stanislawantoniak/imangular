package pl.essay.imangular.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.BillOfMaterial;
import pl.essay.imangular.model.BillOfMaterialInStock;
import pl.essay.imangular.model.BomRequirementsQueryResult;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;
import pl.essay.imangular.service.BillOfMaterialService;
import pl.essay.imangular.service.ItemService;

@RestController
public class BillOfMaterialController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(BillOfMaterialController.class);

	@Autowired private BillOfMaterialService bomService;
	@Autowired private ItemService itemService;


	@RequestMapping(value = "/kick/", method = RequestMethod.GET)
	public ResponseEntity<Void>  kick() {
		//this.kickCreate();
		SetWithCountHolder<BillOfMaterial> boms = this.bomService.listBoms();
		for(BillOfMaterial bom : boms.getCollection()){
			BillOfMaterial b = this.bomService.getBomById(bom.getId());
			b.setRequiredQuantity(2);
			this.bomService.updateBom(bom);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	//for testing- creates boms for all items
	private void kickCreate() {
		SetWithCountHolder<Item> items = this.itemService.listItems();
		for (Item item : items.getCollection()){
			if (item.getIsComposed()){
				BillOfMaterial bom = new BillOfMaterial();
				bom.setForItem(item);
				bom.setRequiredQuantity(item.getId());
				this.bomService.addBom(bom);
			}
		}
	}

	@RequestMapping(value = "/boms", method = RequestMethod.GET)
	public SetWithCountHolder<BillOfMaterial> listBoms() {
		return this.bomService.listBoms();
	}
	
	@RequestMapping(value= "/bomrest/requirements/{bomId}", method = {RequestMethod.GET})
	public ResponseEntity<List<BomRequirementsQueryResult>> usedInItem(@PathVariable long bomId){

		return new ResponseEntity<List<BomRequirementsQueryResult>>(this.bomService.getBomRequirements(bomId), HttpStatus.OK);

	}


	@RequestMapping(value = "/bomrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BillOfMaterial> getBom(@PathVariable("id") long id) {
		System.out.println("Fetching Bom with id " + id);
		BillOfMaterial bom = (id != 0 ? this.bomService.getBomById(id) : new BillOfMaterial());//init bom or get from db 
		return new ResponseEntity<BillOfMaterial>(bom, HttpStatus.OK);
	}

	@RequestMapping(value= "/bomrest/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateBillOfMaterial(@PathVariable long id, @RequestBody BillOfMaterial bom){

		logger.trace("before update bom data: "+bom);
		this.bomService.updateBom( bom );
		logger.trace("after update bom data: "+bom);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/bomrest/", method = RequestMethod.POST)
	public ResponseEntity<Long> createBillOfMaterial(@RequestBody BillOfMaterial bom){

		logger.trace("create bom for item: "+bom.getForItem().getId());

		//if ( this.bomService.existsBillOfMaterial( bom.getName() ) ){
		//	System.out.println("BillOfMaterial with name " + bom.getName() + " already exist and requested create");
		//	return new ResponseEntity<Integer>(0,HttpStatus.CONFLICT);
		//}

		Item forItem = this.itemService.getItemById(bom.getForItem().getId());
		bom.setForItem(forItem);
		this.bomService.addBom( bom );
		logger.trace("after create bom data: "+bom);

		return new ResponseEntity<Long>(bom.getId(), HttpStatus.OK);
	}

	@RequestMapping(value= "/bomrest/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteBillOfMaterial(@PathVariable long id){

		BillOfMaterial bom = this.bomService.getBomById(id);
		if (bom == null){			 
			System.out.println("BillOfMaterial " +id+ " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before delete bom: "+bom);
		this.bomService.removeBom(id);
		logger.trace("after delete bom: "+bom);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value= "/bomrest/stock/{idbom}/{idstock}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteStockFromBillOfMaterial(@PathVariable long idbom,@PathVariable long idstock){

		BillOfMaterial bom = this.bomService.getBomById(idbom);
		if (bom == null){			 
			logger.trace("BillOfMaterial " +idbom+ " does not exist but requested delete stock from it");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before delete bom: "+bom);
		this.bomService.removeStockFromBom(idbom, idstock);
		logger.trace("after delete bom: "+bom);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/bomrest/stock/{idbom}/{idstock}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateStockInBillOfMaterial(@PathVariable long idbom,@PathVariable long idstock, @RequestBody BillOfMaterialInStock stock){

		BillOfMaterial bom = this.bomService.getBomById(idbom);
		if (bom == null){			 
			logger.trace("BillOfMaterial " +idbom+ " does not exist but requested update stock in it");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before update bom: "+bom);
		this.bomService.updateStockInBom(stock);
		logger.trace("after update bom: "+bom);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value= "/bomrest/stock/{idbom}/{idstock}", method = RequestMethod.POST)
	public ResponseEntity<Void> createStockInBillOfMaterial(@PathVariable long idbom,@PathVariable long idstock, @RequestBody BillOfMaterialInStock stock){

		BillOfMaterial bom = this.bomService.getBomById(idbom);
		if (bom == null){			 
			logger.trace("BillOfMaterial " +idbom+ " does not exist but requested update stock in it");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before create bom: "+bom);
		this.bomService.createStockInBom(stock);
		logger.trace("after create bom: "+bom);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
