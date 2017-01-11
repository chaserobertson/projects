package com.example.chaserobertson.familymap.model;

/**
 * Created by chaserobertson on 6/11/16.
 */
public class Filter {
    public String name;
    public String description;

    public Filter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter filter = (Filter) o;

        if (name != null ? !name.equals(filter.name) : filter.name != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
