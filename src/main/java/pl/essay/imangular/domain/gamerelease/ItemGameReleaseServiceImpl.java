package pl.essay.imangular.domain.gamerelease;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.servicefacade.GenericServiceImpl;

@Service
@Transactional
public class ItemGameReleaseServiceImpl extends GenericServiceImpl<ItemGameRelease> implements ItemGameReleaseService {

}
