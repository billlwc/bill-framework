package bill.framework.message;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageSourceService {

    private final MessageSource messageSource;

    public String getMessage(String messageKey, Object[] args){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageKey, args,messageKey, locale);
    }

}
