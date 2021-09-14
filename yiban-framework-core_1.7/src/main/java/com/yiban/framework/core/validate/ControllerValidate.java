package com.yiban.framework.core.validate;

import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
* @author swing
* @date 2018年1月9日 下午2:25:08
* 类说明
*/
public class ControllerValidate {
	public static String convertErrorMessage(BindingResult result){
		String message = "";
    	List<FieldError> errorList = result.getFieldErrors();
    	for(FieldError fieldError : errorList){
    		message = message + fieldError.getField()+" "+fieldError.getDefaultMessage();
    		int index = errorList.indexOf(message);
    		if(index != errorList.size()-1){
    			message = message +",";
    		}
    	}
		return message;
	}
}
