package pl.essay.generic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.generic.servicefacade.GenericService;

@RestController
public class GenericRestControllerImpl<T extends Object, S extends GenericService<T>> {
		
	@Autowired
	private GenericService<T> service;
	
	private Class<T> domainClass;
	
	private String restAll;
	private String restSingle;
	
	public String getDomainClassName(){
		Class<?>[] classArr = GenericTypeResolver.resolveTypeArguments(getClass(), GenericRestControllerImpl.class);
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < classArr.length; i++)
			s.append("1:: "+classArr[i].getName()+"/n");
		return s.toString();
	}
	
}
