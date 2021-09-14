package com.yiban.framework.core.util;

import java.util.Iterator;
import java.util.List;
import com.yiban.framework.core.domain.base.AbstractEntity;
import com.yiban.framework.core.domain.base.TreeEntity;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;

/**
 * @author swing
 * @date 2018年2月1日 下午2:47:20 类说明
 */
public class EntityUtil {
	//过滤掉未激活的，已删除的记录
	public static void filterEntity(List<AbstractEntity> entityList) {
		Iterator<AbstractEntity> ite = entityList.iterator();
		while (ite.hasNext()) {
			AbstractEntity obj = ite.next();
			if (obj.getIsActived() == ActiveEnum.NO.getValue() || obj.getIsDeleted() == DeleteEnum.YES.getValue()) {
				ite.remove();
			}
			if(obj instanceof TreeEntity){
				TreeEntity treeEntity =(TreeEntity)obj;
				List<AbstractEntity> child = treeEntity.getChildren();
				if (child != null && !child.isEmpty()) {
					filterEntity(child);
				}
			}
		}
	}
}
