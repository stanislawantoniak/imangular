package pl.essay.imangular.domain.gamerelease;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

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
import pl.essay.generic.controller.BaseController;
import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

@RestController
public class ItemGameReleaseController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(ItemGameReleaseController.class);

	@Autowired
	public ItemGameReleaseService itemGameReleaseService;

	@RequestMapping(value = "/itemgamereleases", method = { RequestMethod.GET })
	public ResponseEntity<SetWithCountHolder<ItemGameRelease>> listItemGameReleases() {

		SetWithCountHolder<ItemGameRelease> holder = this.itemGameReleaseService.listEntities();

		return new ResponseEntity<SetWithCountHolder<ItemGameRelease>>(holder, HttpStatus.OK);

	}

	@RequestMapping(value = "/itemgamereleases/", method = { RequestMethod.POST })
	public ResponseEntity<SetWithCountHolder<ItemGameRelease>> listItemGameReleasesWithParams(
			@RequestBody ListingParamsHolder filter) {

		SetWithCountHolder<ItemGameRelease> holder = this.itemGameReleaseService.listEntitiesPaginated(filter);

		return new ResponseEntity<SetWithCountHolder<ItemGameRelease>>(holder, HttpStatus.OK);

	}

	@RequestMapping(value = "/itemgamereleaserest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemGameRelease> getItemGameRelease(@PathVariable("id") int id) {
		logger.trace("Fetching ItemGameRelease with id " + id);
		ItemGameRelease item = (id != 0 ? this.itemGameReleaseService.getEntityById(id) : new ItemGameRelease());// init
																													// item
																													// or
																													// get
																													// from
																													// db
		return new ResponseEntity<ItemGameRelease>(item, HttpStatus.OK);
	}

	@RequestMapping(value = "/itemgamereleaserest/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('" + UserForm.roleSupervisor + "')")
	public ResponseEntity<Void> updateItemGameRelease(@PathVariable int id, @RequestBody ItemGameRelease item) {

		logger.trace("update item data: " + item);

		ItemGameRelease itemFromDB = this.itemGameReleaseService.getEntityById(id);
		if (itemFromDB == null) {
			logger.trace("ItemGameRelease " + id + " does not exist, update failed");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before update item data: " + itemFromDB);
		this.itemGameReleaseService.updateEntity(item);
		logger.trace("after update item data: " + itemFromDB);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/itemgamereleaserest/", method = RequestMethod.POST)
	@PreAuthorize("hasRole('" + UserForm.roleSupervisor + "')")
	public ResponseEntity<Integer> createItemGameRelease(@RequestBody ItemGameRelease item) {

		logger.trace("before create ItemGameRelease data: " + item);
		this.itemGameReleaseService.addEntity(item);
		logger.trace("after create ItemGameRelease data: " + item);

		return new ResponseEntity<Integer>(item.getId(), HttpStatus.OK);
	}

	@RequestMapping(value = "/itemgamereleaserest/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('" + UserForm.roleSupervisor + "')")
	public ResponseEntity<Void> deleteItemGameRelease(@PathVariable int id) {

		ItemGameRelease item = this.itemGameReleaseService.getEntityById(id);
		if (item == null) {
			logger.trace("ItemGameRelease " + id + " does not exist but requested delete");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		logger.trace("before delete ItemGameRelease: " + item);
		this.itemGameReleaseService.removeEntity(id);
		logger.trace("after delete ItemGameRelease: " + item);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/gamereleasesteprest/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('" + UserForm.roleSupervisor + "')")
	public ResponseEntity<Void> deleteGameReleaseStep(@PathVariable int id) {

		this.itemGameReleaseService.deleteStep(id);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/gamereleasesteprest/fileupload/{id}", method = { RequestMethod.POST })
	@PreAuthorize("hasRole('" + UserForm.roleSupervisor + "')")
	public ResponseEntity<Void> uploadImage(@RequestBody String file, @PathVariable int id) throws IOException {

		this.itemGameReleaseService.setImageOnStep(id, DatatypeConverter.parseBase64Binary(file));

		// System.out.println("save img:: "+ file );

		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
