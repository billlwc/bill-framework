package com.bill.test.validation;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 手机号校验器
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private boolean required;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果不是必填且值为空，则通过校验
        if (!required && StrUtil.isBlank(value)) {
            return true;
        }

        // 如果是必填或值不为空，则校验格式
        if (StrUtil.isBlank(value)) {
            return false;
        }

        return PHONE_PATTERN.matcher(value).matches();
    }
}
