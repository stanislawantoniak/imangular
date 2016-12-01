package pl.essay.imangular.domain.image;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.servicefacade.GenericServiceImpl;

@Service
@Transactional
public class ImageServiceImpl extends GenericServiceImpl<Image> implements ImageService {
	

}
