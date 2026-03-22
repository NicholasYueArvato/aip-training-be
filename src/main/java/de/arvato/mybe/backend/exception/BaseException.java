package de.arvato.mybe.backend.exception;

public class BaseException extends RuntimeException
{

    final public static String ERROR_CODE_UNKNOWN_ERROR = "error.UNKNOWN_ERROR";

    protected String errorCode = ERROR_CODE_UNKNOWN_ERROR;

    public BaseException(String errorCode)
    {
        super("");
        this.errorCode = errorCode;
    }

    public BaseException(String errorCode, String errorMessage)
    {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BaseException(Throwable cause)
    {
        super(cause.getMessage(), cause);
        this.errorCode = ERROR_CODE_UNKNOWN_ERROR;
    }

    public String getErrorCode()
    {
        return this.errorCode;
    }
}
