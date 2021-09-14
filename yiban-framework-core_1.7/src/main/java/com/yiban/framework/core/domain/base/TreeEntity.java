package com.yiban.framework.core.domain.base;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

@MappedSuperclass
public class TreeEntity<T extends TreeEntity<?, U>, U> extends BaseEntity<U> implements TreeNodeable<T> {

    private static final long serialVersionUID = -4444987216485316814L;

    // 名称.
    @Column(length = 200, nullable = false)
    @Size(max = 200)
    private String name;

    // 描述.
    @Column(length = 500)
    @Size(max = 500)
    private String description;

    // 父节点.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonSerialize(using = IdSerializer.class)
    private T parent;

    // 子节点.
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent")
    @OrderBy("sort ASC, id ASC")
    private List<T> children = Lists.newArrayList();

    // 排序号
    private Long sort = 0L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    @Override
    public String getText() {
        return this.getName();
    }

    @Override
    public String getIconCls() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public String getState() {
        return "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }
}
