package bill.framework.web.log;

import java.util.Set;

public interface RequestLogConsumer {

    Set<String> excludePaths();

    void consume(RequestLog requestLog);

}
