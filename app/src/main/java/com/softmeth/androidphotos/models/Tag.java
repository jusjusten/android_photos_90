package com.softmeth.photosandroid.models;

import java.io.Serializable;

/**
 * Represents a tag with name-value pair.
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String value;
    
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return name.equalsIgnoreCase(tag.name) && value.equalsIgnoreCase(tag.value);
    }
    
    @Override
    public int hashCode() {
        return (name.toLowerCase() + value.toLowerCase()).hashCode();
    }
    
    @Override
    public String toString() {
        return name + ": " + value;
    }
}
