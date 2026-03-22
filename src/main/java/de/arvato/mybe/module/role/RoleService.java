package de.arvato.mybe.module.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import de.arvato.mybe.dao.RoleDAO;
import de.arvato.mybe.dao.RoleDTO;
import de.arvato.mybe.dao.UserDTO;

@Service
public class RoleService {
	@Autowired
	RoleDAO roleDAO;
	
    public List<RoleDTO> getRole(long id){ 
        return roleDAO.getRole(id);
    }
    
    public List<RoleDTO> getAllRoles(){
        return roleDAO.getAllRoles();
    }
    
    public boolean addRole(RoleDTO role) throws Exception{
    	if (roleDAO.checkRole(role.getName())!=null) {
    		throw new Exception("Role already exist");
    	}
    	return (roleDAO.addRole(role)>0);
    	
    }
    
 
    public boolean updateRole(RoleDTO role,long id) throws Exception{
    	if (roleDAO.checkRole(role.getName())==null) {
    		throw new Exception("Role does not exist");
    	}
    	return (roleDAO.updateRole(role,id)>0);
    }
    
    public boolean deleteRole(long role) throws Exception{
    	if (roleDAO.getRole(role)==null){
    		throw new Exception("Employee does not exist");
    	}
    	return (roleDAO.deleteRole(role)>0);
    }

}
