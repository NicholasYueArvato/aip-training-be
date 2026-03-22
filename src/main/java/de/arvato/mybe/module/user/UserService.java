package de.arvato.mybe.module.user;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.arvato.mybe.dao.EmployeeDTO;
import de.arvato.mybe.dao.UserDAO;
import de.arvato.mybe.dao.UserDTO;


@Service
public class UserService {
	@Autowired
	UserDAO userDAO;
	
    public List<UserDTO> getUser(long id){ 
        return userDAO.getUser(id);
    }
    
    public List<UserDTO> getAllUsers(){
        return userDAO.getAllUsers();
    }
    
    public boolean addUser(UserDTO user) throws Exception{
    	if(userDAO.checkUser(user.getRacf())!=null) {
    		throw new Exception("User already exist");
    	}
    	return (userDAO.addUser(user)>0);
    	
    }
    
  
    public boolean updateUser(UserDTO user, long id) throws Exception{	
    	if(userDAO.getUser(user.getId())==null) {
    		throw new Exception("User does not exist");
    	}
    	return (userDAO.updateUser(user,id)>0);
    	
    }
    
    public boolean deleteUser(long user) throws Exception{    
    	if(userDAO.getUser(user)==null) {
    		throw new Exception("User does not exist");
    	}
    	return (userDAO.deleteUser(user)>0);
    	
    }

}
