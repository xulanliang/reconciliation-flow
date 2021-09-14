package com.yiban.framework.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.yiban.framework.core.domain.base.AbstractEntity;
import com.yiban.framework.core.domain.base.TreeEntity;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;

/**
 * @author swing
 * @date 2018年2月6日 下午1:43:09 类说明 
 */
public class YiBanEntityUtils {
	
	/**前端使用两种树形插件：bootstraptable 和ztree,前者使用平级但是允许有child,后者使用平级但是不允许有child节点（会导致重复数据）因此java domain里面必须兼容两者
	 * 因为ztree插件不能同时支持平级和有层次机构的树，所以这里需要将child节点都去除
	 * 
	 * @param sourceList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final static  void filterChild(final List<? extends TreeEntity> sourceList){
		Iterator<? extends TreeEntity> treeTte = sourceList.iterator();
		while(treeTte.hasNext()){
			TreeEntity treeNode = treeTte.next();
			treeNode.setCreatedBy(null);
			treeNode.setLastModifiedBy(null);
			if(treeNode.getChildren() !=null && treeNode.getChildren().size() >0){
				filterChild(treeNode.getChildren());
				treeNode.setChildren(new ArrayList<>());
			}
		}
	}

	/**
	 * 过滤已经删除的，没有激活的实体对象
	 * @param sourceList
	 */
	@SuppressWarnings("rawtypes")
	public static  final void filterEntity(final List<? extends AbstractEntity> sourceList){
		Iterator<? extends AbstractEntity> ite = sourceList.iterator();
		while(ite.hasNext()){
			AbstractEntity entity =ite.next();
			if(entity.getIsDeleted() == DeleteEnum.YES.getValue() || entity.getIsActived() == ActiveEnum.NO.getValue()){
				ite.remove();
			}
			if(entity instanceof TreeEntity){
				TreeEntity  treeEntity =(TreeEntity)entity;
				if(treeEntity.getChildren() != null && !treeEntity.getChildren().isEmpty()){
					filterEntity(treeEntity.getChildren());
				}
			}
		}
	}
	
	

}
