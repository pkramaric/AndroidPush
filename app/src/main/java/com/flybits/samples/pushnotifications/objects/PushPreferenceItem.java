package com.flybits.samples.pushnotifications.objects;

public class PushPreferenceItem {

    private String header;
    private String description;
    private boolean isSelected;

    public PushPreferenceItem(String header, String description) {
        this.description = description;
        this.header = header;
        this.isSelected = true;
    }

    public String getHeader() {
        return header;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getDescription() {
        return description;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
