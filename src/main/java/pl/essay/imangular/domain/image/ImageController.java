package pl.essay.imangular.domain.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.essay.generic.controller.BaseController;

@Controller
public class ImageController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(ImageController.class);

	@Autowired
	private ImageService imageService;

	@RequestMapping(value="/imagerest/{id}.jpg")
	public void getImage(HttpServletResponse response , @PathVariable("id") int id) throws IOException {

		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		
		Image img = this.imageService.getEntityById(id);
		
		//String imgString = DatatypeConverter.printBase64Binary(img.getImage());
		//System.out.println("get img:: "+imgString);
				
		InputStream in1 = new ByteArrayInputStream(img.getImage());
		IOUtils.copy(in1, response.getOutputStream());  
		
	}
}