package com.yiban.rec.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 字典和系统编码的对应关系维护表
 * @author Administrator
 *
 */
@Entity
@Table(name = "t_system_metadata")
public class SystemMetadata implements Serializable {
	
	private static final long serialVersionUID = 1289385936387223353L;
	
	/** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=20)
    private Long id; 
    /** 字典值*/
    @Column(length=64)
    private String metaDataCode;
    /** 系统编码*/
    @Column(length=64)
    private String systemCode;
    /** 排序字段*/
    @Column(length=10)
    private int sortKey;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMetaDataCode() {
		return metaDataCode;
	}
	public void setMetaDataCode(String metaDataCode) {
		this.metaDataCode = metaDataCode;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public int getSortKey() {
		return sortKey;
	}
	public void setSortKey(int sortKey) {
		this.sortKey = sortKey;
	}
    
}
