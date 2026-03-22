package de.arvato.mybe.base.security;

/**
 * Data for remote application access
 */
public class RemoteApplicationDTO
{
    /**
     * Remote system name
     */
    private String remoteSystemName;

    /**
     * Provider baseURI
     */
    private String providerBaseUri;

    /**
     * Login user
     */
    private String loginUser;

    /**
     * Authentication token
     */
    private String authenticationToken;

    /**
     * Local system_app_pk for this remote application
     */
    private String systemAppPk;

    /**
     * Default constructor
     */
    public RemoteApplicationDTO()
    {
    }

    /**
     * Constructor with given attributes
     *
     * @param remoteSystemName
     * @param providerBaseUri
     * @param loginUser
     * @param authenticationToken
     * @param systemAppPk
     */
    public RemoteApplicationDTO(String remoteSystemName, String providerBaseUri, String loginUser, String authenticationToken, String systemAppPk)
    {
        setRemoteSystemName(remoteSystemName);
        setProviderBaseUri(providerBaseUri);
        setLoginUser(loginUser);
        setAuthenticationToken(authenticationToken);
        setSystemAppPk(systemAppPk);
    }

    public String getRemoteSystemName()
    {
        return remoteSystemName;
    }

    public void setRemoteSystemName(String remoteSystemName)
    {
        this.remoteSystemName = remoteSystemName;
    }

    public String getProviderBaseUri()
    {
        return providerBaseUri;
    }

    private void setProviderBaseUri(String providerBaseUri)
    {
        this.providerBaseUri = providerBaseUri;
    }

    public String getLoginUser()
    {
        return loginUser;
    }

    public void setLoginUser(String loginUser)
    {
        this.loginUser = loginUser;
    }

    public String getAuthenticationToken()
    {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken)
    {
        this.authenticationToken = authenticationToken;
    }

    public String getSystemAppPk()
    {
        return systemAppPk;
    }

    private void setSystemAppPk(String systemAppPk)
    {
        this.systemAppPk = systemAppPk;
    }
}
