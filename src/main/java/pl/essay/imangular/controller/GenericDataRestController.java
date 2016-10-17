package pl.essay.imangular.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.languages.Language;

@RestController
public class GenericDataRestController extends BaseController{
	
	@RequestMapping("/common/labels")
	public Map<String,String> getLabels() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("labels caller pricipal: "+auth.getPrincipal());
		return this.getUserTranslations();
	}
	
	@RequestMapping("/common/languages")
	public List<Language> getLanguages() {
		return this.languages.getLanguages();
	}
	
}
