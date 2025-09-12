package bill.framework.web.exception;

import bill.framework.enums.ResponseCode;
import bill.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class ExceptionUtil {
    public static void exception(boolean b, ResponseCode responseCode) {
        if (b) {
            throw new BusinessException(responseCode);
        }
    }

    public static void exception(boolean b, String msg) {
        if (b) {
            throw new BusinessException(msg);
        }
    }

    public static void isNull(Object object,ResponseCode responseCode) {
        exception(object == null, responseCode);
    }

    public static void isNull(Object object,String responseMsg) {
        exception(object == null, responseMsg);
    }

    public static <T> T handleException(Supplier<T> supplier, T defaultValue) {
        try {
            return (T)supplier.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return defaultValue;
        }
    }

    public static void handleException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
