package de.arvato.mybe.module.user;

import de.arvato.mybe.backend.rest.RemoteResponse;

import de.arvato.mybe.dao.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@GetMapping(path = "/{id}")
    //@PreAuthorize("hasAuthority('MANAGE_SITES')")
    @ResponseBody
    @Operation(
            summary = "Get user",
            description = "Get user by id"
    )
    public RemoteResponse getUser(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(userService.getUser(id));
    }
    
    @GetMapping(path = "")
    //@PreAuthorize("hasAuthority('MANAGE_SITES')")
    @ResponseBody
    @Operation(
            summary = "Get all users",
            description = "Get all users"
    )
    public RemoteResponse getAllUsers()
    {
        return RemoteResponse.fillResponseSuccess(userService.getAllUsers());
        
    }
    
    @PostMapping(path="")
    @ResponseBody
    @Operation(
            summary = "Add user",
            description = "Add user"
    )
    public RemoteResponse addUser(@RequestBody UserDTO user) {
    	try {
    		userService.addUser(user);
    		return RemoteResponse.fillResponseSuccess();
    	}catch(Exception ex) {
    		LOGGER.error("Error in User,{}",ex.getMessage(),ex);
    		return RemoteResponse.fillResponseFailed();
    	}
    	
    }
    
    @PutMapping(path="/{id}") 
    @ResponseBody
    @Operation(
            summary = "Update user",
            description = "Update user"
    )
    public RemoteResponse updateUser(@RequestBody UserDTO user, @PathVariable long id) {
    	try {
    		userService.updateUser(user, id);
    		return RemoteResponse.fillResponseSuccess();
    	}catch(Exception ex) {
    		LOGGER.error("Error in User,{}",ex.getMessage(),ex);
    		return RemoteResponse.fillResponseFailed();
    	}
    }
    
    @DeleteMapping(path="/{id}")
    @ResponseBody
    @Operation(
            summary = "Delete user",
            description = "Delete user by id"
    )
    public RemoteResponse deleteUser(@PathVariable long id) {
    	try {
    		userService.deleteUser(id);
    		return RemoteResponse.fillResponseSuccess();
    	}catch(Exception ex) {
    		LOGGER.error("Error in User,{}",ex.getMessage(),ex);
    		return RemoteResponse.fillResponseFailed();
    	}
    	
    }

}
