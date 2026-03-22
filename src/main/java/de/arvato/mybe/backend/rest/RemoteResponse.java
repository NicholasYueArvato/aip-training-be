package de.arvato.mybe.backend.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.arvato.mybe.backend.exception.BaseException;
import de.arvato.mybe.backend.general.ErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemoteResponse implements Serializable
{
    private static final long serialVersionUID = 8449244286184255183L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteResponse.class);


    @SuppressWarnings("unchecked")
    private List resultList;

    private String errorCode;
    private String errorDescription;
    private Map<String, Object> errorParam;

    private boolean succeeded;

    /**
     * constructor setting default values
     */
    public RemoteResponse()
    {
        succeeded = false;
        errorCode = null;
        errorDescription = null;
        errorParam = null;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public boolean isSucceeded()
    {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded)
    {
        this.succeeded = succeeded;
    }

    public String getErrorDescription()
    {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription)
    {
        this.errorDescription = errorDescription;
    }

    public Map<String, Object> getErrorParam()
    {
        return errorParam;
    }

    public void setErrorParam(Map<String, Object> errorParam)
    {
        this.errorParam = errorParam;
    }

    @SuppressWarnings("unchecked")
    public List getResultList()
    {
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public void setResultList(List resultList)
    {
        this.resultList = resultList;
    }


    public static RemoteResponse fillResponseDefaultError(Throwable e)
    {
        LOGGER.error(e.toString(), e);

        RemoteResponse remoteResponse = new RemoteResponse();
        String errorCode = ErrorCodes.GENERAL_EXCEPTION;
        String errMsg = e.toString();
        if (e instanceof BaseException)
        {
            BaseException be = (BaseException) e;
            if (StringUtils.isNotEmpty(be.getErrorCode()))
            {
                errorCode = be.getErrorCode();
            }
            if (StringUtils.isNotEmpty(be.getMessage()))
            {
                errMsg = be.getMessage();
            }
        }

        return fillResponseFailed(errorCode, errMsg);
    }

    public static RemoteResponse fillResponseFailed()
    {
        return fillResponseFailed(ErrorCodes.GENERAL_EXCEPTION);
    }


    public static RemoteResponse fillResponseFailed(String code)
    {
        return fillResponseFailed(code, "Es ist leider ein genereller Fehler aufgetreten!");
    }

    public static RemoteResponse fillResponseFailed(BaseException be)
    {
        return fillResponseFailed(be.getErrorCode(), be.getMessage());
    }

    public static RemoteResponse fillResponseFailed(String code, String description)
    {
        RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setErrorCode(code);
        remoteResponse.setErrorDescription(description);
        remoteResponse.setSucceeded(false);

        return remoteResponse;
    }

    public static RemoteResponse fillResponseFailed(String code, String description, Map<String, Object> param)
    {
        RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setErrorCode(code);
        remoteResponse.setErrorDescription(description);
        remoteResponse.setErrorParam(param);
        remoteResponse.setSucceeded(false);

        return remoteResponse;
    }

    public static RemoteResponse fillResponseSuccess()
    {
        return fillResponseSuccess(true);
    }

    public static RemoteResponse fillResponseSuccess(boolean successed)
    {
        RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setSucceeded(successed);

        return remoteResponse;
    }

    public static RemoteResponse fillResponseSuccess(List list)
    {
        RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setResultList(list);
        remoteResponse.setSucceeded(true);

        return remoteResponse;
    }
}
