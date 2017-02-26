package com.itu;

public class ErrorType {
    public static final int ERR_SUCCESS = 0;
    public static final int ERR_PARAMETER_NULL = -1;
    public static final int ERR_INPUT_DATA_TOO_MUCH = -2;
    public static final int ERR_INPUT_DATA_TOO_LESS = -3;
    public static final int ERR_INPUT_DATA_ILLEGALITY = -4;
    public static final int ERR_USB_WRITE_DATA = -5;
    public static final int ERR_USB_READ_DATA = -6;
    public static final int ERR_READ_NO_DATA = -7;
    public static final int ERR_OPEN_DEVICE = -8;
    public static final int ERR_CLOSE_DEVICE = -9;
    public static final int ERR_EXECUTE_CMD = -10;
    public static final int ERR_SELECT_DEVICE = -11;
    public static final int ERR_DEVICE_OPENED = -12;
    public static final int ERR_DEVICE_NOTOPEN = -13;
    public static final int ERR_BUFFER_OVERFLOW = -14;
    public static final int ERR_DEVICE_NOTEXIST = -15;
    public static final int ERR_LOAD_KERNELDLL = -16;
    public static final int ERR_CMD_FAILED = -17;
    public static final int ERR_BUFFER_CREATE = -18;
    public static final int ERR_NO_PERMISSIONS = -19;
}
