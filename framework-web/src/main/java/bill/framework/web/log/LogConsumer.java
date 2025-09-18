package bill.framework.web.log;

import java.util.Set;

public interface LogConsumer {

    Set<String> excludePaths();

    void requestLog(RequestLogInfo requestLog);


    void methodLog(MethodLogInfo requestLog);

}
