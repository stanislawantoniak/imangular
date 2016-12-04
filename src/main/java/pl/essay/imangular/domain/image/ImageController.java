package pl.essay.imangular.domain.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
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

		Image img = this.imageService.getEntityById(id);

		response.setContentType("image/jpg");
		SimpleDateFormat dateFormat=new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	
		response.addHeader("Last-Modified", dateFormat.format(img.getDateCreated()));   
		response.addHeader("Expires", dateFormat.format(DateUtils.addMonths(new Date(), 3) ));

		InputStream in1 = new ByteArrayInputStream(img.getImage());
		IOUtils.copy(in1, response.getOutputStream());  

	}
}