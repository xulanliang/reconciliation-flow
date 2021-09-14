package com.yiban.framework.core.domain.base;

public class ValueText<T> implements ValueTextable<T> {

    private T value;
    private String text;
    private String description;
    private boolean selected;

    public ValueText() {
    }

    public ValueText(T value, String text) {
        this(value, text, "");
    }

    public ValueText(T value, String text, String description) {
        this.value = value;
        this.text = text;
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.modules.core.util.ValueTextable#getValue()
     */
    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.modules.core.util.ValueTextable#getText()
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
     * @see com.nconverge.modules.core.util.ValueTextable#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nconverge.modules.core.util.ValueTextable#isSelected()
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
