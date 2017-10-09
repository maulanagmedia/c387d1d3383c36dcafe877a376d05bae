package com.maulana.custommodul;

/**
 * Created by Shin on 07/09/2017.
 */

public class OptionItem {

    private String value, text, att1, att2, att3, att4, att5;
    private boolean selected;

    @Override
    public String toString() {
        return text;
    }

    public OptionItem(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public OptionItem(String value, String text, String att1, String att2, String att3, String att4, String att5, boolean selected) {
        this.value = value;
        this.text = text;
        this.att1 = att1;
        this.att2 = att2;
        this.att3 = att3;
        this.att4 = att4;
        this.att5 = att5;
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAtt1() {
        return att1;
    }

    public void setAtt1(String att1) {
        this.att1 = att1;
    }

    public String getAtt2() {
        return att2;
    }

    public void setAtt2(String att2) {
        this.att2 = att2;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAtt3() {
        return att3;
    }

    public void setAtt3(String att3) {
        this.att3 = att3;
    }

    public String getAtt4() {
        return att4;
    }

    public void setAtt4(String att4) {
        this.att4 = att4;
    }

    public String getAtt5() {
        return att5;
    }

    public void setAtt5(String att5) {
        this.att5 = att5;
    }
}
