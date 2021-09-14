package com.yiban.framework.account.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Iterator;
import java.util.List;

/**
 * Created by tantian
 *
 * @date 2018/1/30 10:20
 */
public class ErrorValidate {
    public ErrorValidate() {
    }

    public static String convertErrorMessage(BindingResult result) {
        String message = "输入有误<br>";
        List<FieldError> errorList = result.getFieldErrors();
        Iterator var3 = errorList.iterator();

        while(var3.hasNext()) {
            FieldError fieldError = (FieldError)var3.next();
            message = message + fieldError.getField() + " " + fieldError.getDefaultMessage();
            int index = errorList.indexOf(message);
            if (index != errorList.size() - 1) {
                message = message + "<br>";
            }
        }

        return message;
    }
}
