package de.arvato.mybe.module.role;

import de.arvato.mybe.backend.rest.RemoteResponse;
import de.arvato.mybe.dao.EmployeeDTO;
import de.arvato.mybe.dao.RoleDTO;
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
@RequestMapping("/api/role")
public class RoleController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping(path = "/{id}")
    //@PreAuthorize("hasAuthority('MANAGE_SITES')")
    @ResponseBody
    @Operation(
            summary = "Get role",
            description = "Get role by id"
    )
    public RemoteResponse getRole(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(roleService.getRole(id));
    }
    
    @GetMapping(path = "")
    //@PreAuthorize("hasAuthority('MANAGE_SITES')")
    @ResponseBody
    @Operation(
            summary = "Get all roles",
            description = "Get all roles"
    )
    public RemoteResponse getAllRoles()
    {
        return RemoteResponse.fillResponseSuccess(roleService.getAllRoles());
        
    }
    
    @PostMapping(path="")
    @ResponseBody
    @Operation(
            summary = "Add role",
            description = "Add role"
    )
    public RemoteResponse addRole(@RequestBody RoleDTO role) {
    	try {
    		roleService.addRole(role);
    		return RemoteResponse.fillResponseSuccess();
    		
    	}catch(Exception ex){
    		LOGGER.error("Error in Role,{}",ex.getMessage(),ex);
    		return RemoteResponse.fillResponseFailed();
    	}
    }
    
    @PutMapping(path="/{id}") 
    @ResponseBody
    @Operation(
            summary = "Update role",
            description = "Update role"
    )
    public RemoteResponse updateRole(@RequestBody RoleDTO role, @PathVariable long id) {
    	try {
    		roleService.updateRole(role,id);
    		return RemoteResponse.fillResponseSuccess();
    		
    	}catch(Exception ex){
    		LOGGER.error("Error in Role,{}",ex.getMessage(),ex);
    		return RemoteResponse.fillResponseFailed();
    	}
    	
    }
    
    @DeleteMapping(path="/{id}")
    @ResponseBody
    @Operation(
            summary = "Delete role",
            description = "Delete role by id"
    )
    public RemoteResponse deleteRole(@PathVariable long id) {
    	try {
    		roleService.deleteRole(id);
    		return RemoteResponse.fillResponseSuccess();
    		
    	}catch(Exception ex){
    		LOGGER.error("Error in Role,{}",ex.getMessage(),ex);
    		return RemoteResponse.fillResponseFailed();
    	}
    	
    }

}
