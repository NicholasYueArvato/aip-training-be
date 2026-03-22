package de.arvato.mybe.backend.general;

/**
 * ErrorCodes
 */
public class ErrorCodes
{
    /**
     * Since this is a static utility class it should not be constructed => hide the constructor
     */
    private ErrorCodes()
    {

    }

    // Module Code - SubModule Code + Error Number
    // Example, CEN-10100000
    // CEN
    //   10 = General
    //   11 = Employee
    //   12 = Organisation Unit
    //   13 = Email
    //   14 = Holiday

    public static final String GENERAL_EXCEPTION = "CEN-10100000";
    public static final String FORBIDDEN = "CEN-10100001";

    public static final String DUPLICATE_KEY_EXCEPTION = "CEN-10100014";
    public static final String EMPLOYEE_NOT_FOUND = "CEN-11100002";
    public static final String EMPLOYEE_LAST_DATE_IS_INVALID = "CEN-11100003";
    public static final String EMPLOYEE_JOIN_DATE_IS_INVALID = "CEN-11100015";
    public static final String EMPLOYEE_EXIST_IN_ORGUNIT = "CEN-11100005";
    public static final String EMPLOYEE_REFERENCE_SUPERVISOR = "CEN-11100006";
    public static final String DUPLICATE_EMPLOYEE_RACF = "CEN-11100004";
    public static final String DUPLICATE_EMPLOYEE_EMAIL = "CEN-11100019";
    public static final String DUPLICATE_EMPLOYEE_HR_ID = "CEN-11100020";
    public static final String DUPLICATE_EMPLOYEE_CATS_ID = "CEN-11100021";
    public static final String EMPLOYEE_DATA_EXPORT_FAILED = "CEN-11100022";
    public static final String ORGUNIT_NOT_FOUND = "CEN-12100007";
    public static final String ORGUNIT_DUPLICATE_EMPLOYEE = "CEN-12100008";
    public static final String ORGUNIT_DUPLICATE_NAME = "CEN-12100009";
    public static final String ORGUNIT_REFERENCE_BY_CHILD = "CEN-12100010";
    public static final String EMPLOYEE_NO_SUPERVISOR = "CEN-11100011";

    public static final String EMPLOYEE_FILE_INCOMPLETE = "CEN-11100012";

    public static final String ORGUNIT_HAS_EMPLOYEE = "CEN-12100013";

    public static final String EMAIL_DISTRIBUTION_NOT_FOUND = "CEN-13100001";

    public static final String EMAIL_DISTRIBUTION_DATA_INCOMPLETE = "CEN-13100002";
    public static final String EMAIL_SETTING_DATA_CONVERSION_ERROR = "CEN-13100003";

    public static final String DISTRIBUTION_LIST_EXPORT_FAILED = "CEN-13100004";
    public static final String HOLIDAY_NOT_FOUND = "CEN-14100016";
    public static final String HOLIDAY_FILE_INCOMPLETE = "CEN-14100017";
    public static final String HOLIDAY_DUPLICATE = "CEN-14100018";
}
