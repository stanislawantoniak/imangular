package pl.essay.imangular.domain.bom;

import java.util.List;

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

import pl.essay.generic.controller.BaseController;
import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.domain.item.Item;
import pl.essay.imangular.domain.item.ItemService;

@RestController
public class BillOfMaterialController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(BillOfMaterialController.class);

	@Autowired private BillOfMaterialService bomService;
	@Autowired private ItemService itemService;

	@RequestMapping(value = "/boms", method = RequestMethod.GET)
	public SetWithCountHolder<BillOfMaterial> listBoms() {
		if (this.userSession.getUser() != null){
			return this.bomService.listBomsByUser(userSession.getUser());
		} else {
			return this.bomService.listBomsByAnonymousUser(userSession.getAnonymousSessionId());
		}
	}

	@RequestMapping(value= "/bomrest/requirements/{bomId}", method = {RequestMethod.GET})
	public ResponseEntity<List<BomRequirementsQueryResult>> usedInItem(@PathVariable long bomId){

		return new ResponseEntity<List<BomRequirementsQueryResult>>(this.bomService.getBomRequirements(bomId), HttpStatus.OK);

	}


	@RequestMapping(value = "/bomrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BillOfMaterial> getBom(@PathVariable("id") long id) {
	
		BillOfMaterial bom = (id != 0 ? this.bomService.getEntityById( id ) : new BillOfMaterial());//init bom or get from db 
		return new ResponseEntity<BillOfMaterial>(bom, HttpStatus.OK);
	}

	/*
	 * we update only quantity in bom
	 */
	@RequestMapping(value= "/bomrest/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateBillOfMaterial(@PathVariable long id, @RequestBody BillOfMaterial bom){

		this.bomService.updateEntity( bom );

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/bomrest/", method = RequestMethod.POST)
	public ResponseEntity<Long> createBillOfMaterial(@RequestBody BillOfMaterial bom){

		logger.debug("create bom for item: "+bom.getForItem().getId());

		//if ( this.bomService.existsBillOfMaterial( bom.getName() ) ){
		//	System.out.println("BillOfMaterial with name " + bom.getName() + " already exist and requested create");
		//	return new ResponseEntity<Integer>(0,HttpStatus.CONFLICT);
		//}

		Item forItem = this.itemService.getItemById(bom.getForItem().getId());
		bom.setForItem(forItem);
		this.bomService.addEntity( bom );
		logger.debug("after create bom data: "+bom);

		return new ResponseEntity<Long>(bom.getId(), HttpStatus.OK);
	}

	@RequestMapping(value= "/bomrest/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteBillOfMaterial(@PathVariable long id){

		BillOfMaterial bom = this.bomService.getEntityById(id);
		if (bom == null){			 
			logger.debug("BillOfMaterial " +id+ " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.debug("before delete bom: "+bom);
		this.bomService.removeEntity(id);
		logger.debug("after delete bom: "+bom);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/bomstockrest/{idstock}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteStockFromBillOfMaterial(@PathVariable long idbom,@PathVariable long idstock){

		logger.debug("before delete stock: "+idstock);
		this.bomService.removeStockFromBom(idstock);
		logger.debug("after delete stock: "+idstock);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/bomstockrest/{idstock}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateStockInBillOfMaterial(@PathVariable long idstock, @RequestBody BillOfMaterialInStock stock){

		logger.debug("before update stock: "+stock);
		this.bomService.updateStockInBom(stock);
		logger.debug("after update stock: "+stock);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/bomstockrest/", method = RequestMethod.POST)
	public ResponseEntity<Void> createStockInBillOfMaterial(@RequestBody BillOfMaterialInStock stock){

		logger.debug("before create stock: "+stock);
		this.bomService.createStockInBom(stock);
		logger.debug("after create stock: "+stock);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
