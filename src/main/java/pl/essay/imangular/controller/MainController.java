package pl.essay.imangular.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class MainController extends BaseController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String dashboard(Model model) {
		return "index.html";
	}

}
