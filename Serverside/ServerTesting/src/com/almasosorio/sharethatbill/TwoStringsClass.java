package com.almasosorio.sharethatbill;


public class TwoStringsClass {
    public String string1;
    public String string2;

    public TwoStringsClass(String string1, String string2) {
        this.string1 = string1;
        this.string2 = string2;
    }

    public String toString() {
        return string1 + ", " + string2;
    }
}
