/**
 * 
 */
package com.lyca.api.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
				String rootPath = context.getRealPath("/");
				String filePath = FILE_URI_PATH + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM-YY"));
				String serverPath = FILE_URI_PATH + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM-YY"))
						+ File.separator + baseUserId + "-" + timestamp;
				File dir = new File(rootPath + filePath);
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + baseUserId + "-" + timestamp);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				System.out.println("Server File Location=" + serverFile.getAbsolutePath());
				JSONObject user = new JSONObject();
				Integer userId = Integer.parseInt(baseUserId.toString());
				user.put("userId", userId);
				user.put("profilePicUrl", serverPath);
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

}
