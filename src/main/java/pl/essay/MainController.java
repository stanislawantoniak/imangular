package pl.essay;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class MainController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String mainPage() {

		return "/entry-html/index.html";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {

		return "/entry-html/loginpage.html";
	}
	
	@RequestMapping(value = "/changepass/{hash}", method = RequestMethod.GET)
	public String changePassPage() {
		
		return "/entry-html/changepasspage.html";
	}
	
	
}
