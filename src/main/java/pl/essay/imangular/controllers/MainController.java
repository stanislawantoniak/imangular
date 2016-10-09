package pl.essay.imangular.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController extends BaseController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String listItems(Model model) {
		this.addGenericDataToModel(model);
		return "main";
	}
}
