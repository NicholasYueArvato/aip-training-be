package de.arvato.mybe.module.sitemanagement;

import de.arvato.mybe.dao.SiteDAO;
import de.arvato.mybe.dao.SiteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService
{
    @Autowired
    SiteDAO siteDAO;

    public List<SiteDTO> findActiveSites(){
        return siteDAO.findSitesByStatus(SiteContants.ACTIVE);
    }
    //call the create function here?
}
