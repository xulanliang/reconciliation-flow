package com.yiban.framework.core.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.yiban.framework.core.domain.base.NullChildTreeEntity;

/**
 * 将模糊搜索的机构数据重新组装一颗新树
* @author swing
* @date 2018年4月28日 上午9:23:07
* 类说明
*/
@SuppressWarnings({"rawtypes","unchecked"})
public class SearchTree<T extends NullChildTreeEntity> {
	final private Set<T> orgSet = new HashSet<T>();
	//遍历结果，处理父机构
	
	public final Set<T> parseToSet(List<T> sourceList){
		Iterator<T> ite = sourceList.iterator();
		while (ite.hasNext()) {
			T org = ite.next();
			orgSet.add(org);
			addParentToSet((T)org.getParent());
		}
		return orgSet;
	}
	
	//递归将父对象放入集合
	private final void addParentToSet(T org) {
		if(org != null){
			orgSet.add(org);
			addParentToSet((T)org.getParent());
		}
	}
}
