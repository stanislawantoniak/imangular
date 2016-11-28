package pl.essay.imangular.domain.news;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.servicefacade.GenericServiceImpl;

@Service
@Transactional
public class NewsItemServiceImpl extends GenericServiceImpl<NewsItem> implements NewsItemService{

}
