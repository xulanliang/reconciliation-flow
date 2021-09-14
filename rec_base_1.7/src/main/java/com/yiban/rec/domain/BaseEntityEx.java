
package com.yiban.rec.domain;

import javax.persistence.MappedSuperclass;

import com.yiban.framework.core.domain.base.AbstractEntity;
/**
 * 
* @ClassName: BaseEntityEx 
* @Description: 实体扩展基类
* @author tuchun@clearofchina.com 
* @date 2017年3月28日 下午7:03:02 
* @version V1.0 
*
 */
@MappedSuperclass
public abstract class BaseEntityEx extends AbstractEntity {

  
	private static final long serialVersionUID = -4961754329356035504L;

    private Long createdById=1L;

    private Long lastModifiedById;

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Long getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(Long lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	
    
}
