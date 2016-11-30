package pl.essay.imangular.domain.gamerelease;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.servicefacade.GenericServiceImpl;

@Service
@Transactional
public class ItemGameReleaseServiceImpl extends GenericServiceImpl<ItemGameRelease> implements ItemGameReleaseService {
	
	@Autowired 
	private GameReleaseStepDao stepDao; 

	public void deleteStep(Serializable id){
		
		GameReleaseStep step = this.stepDao.get(id);
		ItemGameRelease gr = step.getGameRelease();
		gr.getSteps().remove(step);
		step.setGameRelease(null);
		this.templateEntityDao.update(gr);
	}
}
