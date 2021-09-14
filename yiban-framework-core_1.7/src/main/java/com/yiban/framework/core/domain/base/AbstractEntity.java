package com.yiban.framework.core.domain.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;

@MappedSuperclass
public abstract class AbstractEntity extends IdEntity {

    private static final long serialVersionUID = -863095561995028355L;

  
    @Version
    private long version;
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastModifiedDate;
    
    @Column
    @NotNull
    private int isDeleted=0;
    
    @Transient
    private String isDeletedDisplay;
    
    @Column
    @NotNull
    private int isActived=1;
    
	@Transient
    private String isActivedDisplay;

 

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public String getIsDeletedDisplay() {
		if(this.getIsDeleted() == DeleteEnum.NO.getValue()){
			return DeleteEnum.NO.getText();
		}else if(this.getIsDeleted() == DeleteEnum.YES.getValue()){
			return DeleteEnum.YES.getText();
		}
		return "";
	}

	public void setIsDeletedDisplay(String isDeletedDisplay) {
		this.isDeletedDisplay = isDeletedDisplay;
	}

	public int getIsActived() {
		return isActived;
	}

	public void setIsActived(int isActived) {
		this.isActived = isActived;
	}

	public String getIsActivedDisplay() {
		if(this.getIsActived() == ActiveEnum.YES.getValue()){
			return ActiveEnum.YES.getText();
		}else if(this.getIsActived() == ActiveEnum.NO.getValue()){
			return ActiveEnum.NO.getText();
		}
		return "";
	}

	public void setIsActivedDisplay(String isActivedDisplay) {
		this.isActivedDisplay = isActivedDisplay;
	}
    
}
