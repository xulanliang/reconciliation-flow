package com.yiban.framework.core.domain.base;

import java.util.List;



import com.google.common.collect.Lists;

/**
 * @author swing 一个没有child子树结构的平级数据结构
 * @date 2018年4月28日 上午9:32:05 类说明
 */
public class NullChildTreeEntity<T extends NullChildTreeEntity<?, U>, U> extends TreeEntity<T, U> {

	private static final long serialVersionUID = -847527510368085796L;

	//覆盖父类方法，直接返回空子树
/*	@Override
	public List<T> getChildren() {
		return Lists.newArrayList();
	}

	@Override
	@Deprecated
	public void setChildren(List<T> children) {

	}*/

}
