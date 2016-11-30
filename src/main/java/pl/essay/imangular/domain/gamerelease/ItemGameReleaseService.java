package pl.essay.imangular.domain.gamerelease;

import java.io.Serializable;

import pl.essay.generic.servicefacade.GenericService;

public interface ItemGameReleaseService extends GenericService<ItemGameRelease> {

	public void deleteStep(Serializable id);
	
}
	
