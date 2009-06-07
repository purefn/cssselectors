package com.threelevers.css;

import static java.util.Arrays.*;

enum AttributeComparator {
    EQ("=", new Comparator() { public boolean compare(String lhs, String rhs) {
        return lhs.equals(rhs);
    }}),
    
    STARTS_WITH("^=", new Comparator() { public boolean compare(String lhs, String rhs) {
        return lhs.startsWith(rhs);
    }}),
    
    ENDS_WITH("$=", new  Comparator() { public boolean compare(String lhs, String rhs) {
        return lhs.endsWith(rhs);
    }}),
    
    CONTAINS("*=",  new  Comparator() { public boolean compare(String lhs, String rhs) {
        return lhs.contains(rhs);
    }}),
    
    HAS("~=",  new  Comparator() { public boolean compare(String lhs, String rhs) {
        return asList(lhs.split("\\s")).contains(rhs);
    }}),
    
    EQUALS_LANG_SUBCODE("|=",  new  Comparator() { public boolean compare(String lhs, String rhs) {
        return lhs.equals(rhs) || lhs.startsWith(rhs + "-");
    }});
    
    private final String symbol;
    private final Comparator comparator;

    private AttributeComparator(String symbol, Comparator comparator) {
        this.symbol = symbol;
        this.comparator = comparator;
    }
    
    public String toString() {
        return symbol;
    }

    public boolean compare(String lhs, String rhs) {
        return comparator.compare(lhs, rhs);
    }
    
    private interface Comparator {
        boolean compare(String lhs, String rhs);
    }
}
