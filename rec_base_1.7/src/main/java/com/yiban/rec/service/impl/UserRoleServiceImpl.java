package com.yiban.rec.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yiban.rec.service.UserRoleService;
import com.yiban.rec.service.base.BaseOprService;

@Service
public class UserRoleServiceImpl extends BaseOprService implements UserRoleService {

	
	@Override
	public List<Object> getRoleName(String id) {
		String sql="select t.name from t_role t LEFT JOIN t_user_role tu on t.id=tu.role_id LEFT JOIN t_user tr on tr.id = tu.user_id where tr.id=\'"+id+"\'";
		List<Object> list = handleNativeSql4SingleCol(sql,null);
		return list;
	}

}
