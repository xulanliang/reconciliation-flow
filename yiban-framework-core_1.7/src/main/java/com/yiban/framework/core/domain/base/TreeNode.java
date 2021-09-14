package com.yiban.framework.core.domain.base;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TreeNode implements TreeNodeable<TreeNode> {

    private Long id;
    private String text;
    private String iconCls;
    private boolean selected;

    private String state;

    private String description;
    private Map<String, Object> attributes;

    private List<TreeNode> children = Lists.newArrayList();
    private TreeNode parent;

    public TreeNode() {
    }

    public TreeNode(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#getText()
     */
    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#getIconCls()
     */
    @Override
    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#isSelected()
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#isChecked()
     */
    @Override
    public boolean isChecked() {
        return this.isSelected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#getChildren()
     */
    @Override
    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#getState()
     */
    @Override
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.pms.omp.util.TreeNodeable#getAttributes()
     */
    @Override
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = Maps.newLinkedHashMap();
        }
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TreeNode other = (TreeNode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }
}
