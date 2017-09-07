/**
 * 
 */
package com.lyca.api.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lyca.api.model.User;
import com.lyca.api.service.UserService;

/**
 * @author Krishna
 *
 */
@RestController
@RequestMapping("/user")
@SuppressWarnings("unchecked")
public class UserController {

	@Autowired
	@Qualifier("responseMessage")
	private Properties responseMessage;

	@Autowired
	private ServletContext context;

	/**
	 * User Service.
	 */
	@Autowired
	private UserService userService;

	private final static String RESOURCE_PATH = "resources" + File.separator;
	private final static String FILE_URI_PATH = "/resources/";

	/**
	 * Create User.
	 * 
	 * @param user
	 * @return user
	 * @throws SQLException
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody JSONObject addUser(@RequestBody JSONObject user) {

		if (user.get("countryId") == null) {
			user.put("countryId", 99);
		}
		return userService.addUser(user);
	}

	/**
	 * Update User.
	 * 
	 * @param user
	 * @return user
	 */
	@RequestMapping(value = "/update")
	public @ResponseBody JSONObject updateUser(@RequestBody JSONObject user) {
		return userService.updateUser(user);
	}

	/**
	 * Delete user.
	 * 
	 * @param user
	 * @return user
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public @ResponseBody JSONObject deleteUser(@RequestBody JSONObject userId) {
		return userService.deleteUser(userId);
	}

	@RequestMapping(value = "/list")
	public @ResponseBody JSONObject getUserById(@RequestBody JSONObject user) {
		System.out.println(user);
		if (user.get("key") != null && user.get("key").equals("listAll")) {
			return userService.listUser();
		} else {
			return userService.getUserById(user);
		}
	}

	/**
	 * Otp verification.
	 * 
	 * @param otp
	 * @return user
	 */
	@RequestMapping(value = "/otpVerification", method = RequestMethod.POST)
	public @ResponseBody JSONObject otpVerification(@RequestBody JSONObject otp) {
		return userService.otpVerification(otp);
	}

	/**
	 * Re-send otp verification.
	 * 
	 * @param otp
	 * @return user
	 */
	@RequestMapping(value = "/resendOtp", method = RequestMethod.POST)
	public @ResponseBody JSONObject sendOtp(@RequestBody JSONObject otp) {
		return userService.sendOtp(otp);
	}

	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody JSONObject uploadFileHandler(@RequestParam("baseUserId") String baseUserId,
			@RequestParam("file") MultipartFile file) {
		JSONObject status = new JSONObject();
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				Integer timestamp = new java.sql.Timestamp(System.currentTimeMillis()).getNanos();
				// Creating the directory to store file
//				String rootPath = context.getRealPath("/");
				String rootPath = responseMessage.get("upload.path").toString();
				String filePath = FILE_URI_PATH + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMMM-YY"));
				String serverPath = FILE_URI_PATH + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMMM-YY"))
						+ File.separator + baseUserId + "-" + timestamp;
				File dir = new File(rootPath + filePath);
				Boolean state = false;
				if (!dir.exists())
					state = dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + baseUserId + "-" + timestamp);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				System.out.println("Server File Location=" + serverFile.getAbsolutePath());
				JSONObject user = new JSONObject();
				Integer userId = Integer.parseInt(baseUserId.toString());
				user.put("userId", userId);
				user.put("profilePicUrl", serverFile.getAbsolutePath());
				System.out.println("You successfully uploaded file=" + baseUserId);
				return userService.updateUser(user);
			} catch (Exception e) {
				status.put("responseStatus", false);
				status.put("responseMessage", responseMessage.get("failed.to.upload.file"));
				status.put("Error", e.getMessage());
			}
		} else {
			status.put("responseStatus", false);
			status.put("responseMessage", responseMessage.get("file.is.empty"));
		}
		return status;
	}

	@RequestMapping("/pdf/{fileName:.+}")
    public void downloadPDFResource( HttpServletRequest request,
                                     HttpServletResponse response,
                                     @PathVariable("fileName") String fileName)
    {
        //If user is not authorized - he should be thrown out from here itself
         
        //Authorized user will download the file
        String dataDirectory = "/home/kumar/lyca/resources/07-September-17/319-504000000.jpg";
        Path file = Paths.get(dataDirectory, fileName);
        if (Files.exists(file))
        {
            response.setContentType("application/image");
            response.addHeader("Content-Disposition", "; filename="+fileName);
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
	
	@ResponseBody
	@RequestMapping(value = "/getImage/{userId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public Resource testphoto(@PathVariable(value = "userId") Integer userId) throws Exception {
		if (userId != null) {
			User user = userService.find(userId);
			String dataDirectory = user.getProfilePicUrl();
			
			ResourceLoader loader = new DefaultResourceLoader();
			
		    return loader.getResource("file:" + dataDirectory);
		}
		return null;
	}

	
}
