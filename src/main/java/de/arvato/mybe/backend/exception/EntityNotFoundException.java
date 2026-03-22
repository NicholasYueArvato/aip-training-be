package de.arvato.mybe.backend.exception;

/**
 * exception that indicates an object is not found from DB.
 * use this for validation, find functions.
 */
public class EntityNotFoundException extends BaseException
{
    public EntityNotFoundException(String errorCode)
    {
        super(errorCode);
    }

    public EntityNotFoundException(String errorCode, String entity, Object entityId)
    {
        super(errorCode, String.format("Entity %s with ID %s is not found!", entity, entityId));
    }

    public EntityNotFoundException(Throwable cause)
    {
        super(cause);
    }

    @Override
    public String getErrorCode()
    {
        return super.getErrorCode();
    }
}
