package pl.essay.imangular.domain.gamerelease;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.servicefacade.GenericServiceImpl;
import pl.essay.imangular.domain.image.Image;
import pl.essay.imangular.domain.image.ImageDao;

@Service
@Transactional
public class ItemGameReleaseServiceImpl extends GenericServiceImpl<ItemGameRelease> implements ItemGameReleaseService {
	
	@Autowired 
	private GameReleaseStepDao stepDao; 
	@Autowired 
	private ImageDao imageDao;
	
	@Override
	public void deleteStep(Serializable id){
		
		GameReleaseStep step = this.stepDao.get(id);
		ItemGameRelease gr = step.getGameRelease();
		gr.getSteps().remove(step);
		step.setGameRelease(null);
		this.templateEntityDao.update(gr);
	}
	
	@Override
	public void setImageOnStep(Serializable id, byte[] i){
		GameReleaseStep step = this.stepDao.get(id);
		if (step.getImage() != null)
			this.imageDao.deleteById(step.getImage());
		Image img = new Image();
		img.setImage( i );
		this.imageDao.create(img);
		step.setImage( img.getId() );
		this.stepDao.update(step);
	}
	
}
