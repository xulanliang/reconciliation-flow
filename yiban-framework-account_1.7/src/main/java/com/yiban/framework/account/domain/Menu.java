package com.yiban.framework.account.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import com.yiban.framework.core.domain.base.TreeEntity;

/**
 * 菜单.
 * 
 */
@Entity
@Table(name = Menu.TABLE_NAME)
public class Menu extends TreeEntity<Menu, User> {

    private static final long serialVersionUID = -3958623162747858717L;
    public static final String TABLE_NAME = "t_menu";

    @Column(length = 200)
    @Size(max = 200)
    private String url;

    @Column(length = 50)
    @Size(max = 50)
    private String target;

    // 对应权限.
    @Column(length = 200)
    @Size(max = 200)
    private String perm;

    @Column(length = 200)
    @Size(max = 200)
    private String iconCls;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    @Override
    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

}
