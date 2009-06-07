package com.threelevers.css;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.threelevers.css.Elements.previous;
import static com.threelevers.css.Nodes.isElement;

import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.threelevers.css.CssSelectors.CombinatorSelector;
import com.threelevers.css.CssSelectors.SimpleSelector;

enum Combinator {
    ADJACENT("+", new Function<Element, Element>() { public Element apply(Element element) {
        return previous(element);
    }}, once()),
    
    CHILDOF(">", new Function<Element, Element>() { public Element apply(Element element) {
        return isElement(element.getParentNode()) ? (Element) element.getParentNode() : null;
    }}, once()),
    
    SIBLING("~", new Function<Element, Element>() { public Element apply(Element element) {
        return previous(element);
    }}),
    
    DESCENDANT(" ", new Function<Element, Element>() { public Element apply(Element element) {
        return isElement(element.getParentNode()) ? (Element) element.getParentNode() : null;
    }});
    
    private final String symbol;
    private final Function<Element, Element> traverser;
    private final int times;
    
    private Combinator(String symbol, Function<Element, Element> traverser) {
        this(symbol, traverser, infinite());
    }

    private Combinator(String symbol, Function<Element, Element> traverser, int times) {
        this.symbol = symbol;
        this.traverser = traverser;
        this.times = times;
    }
    
    boolean matches(CombinatorSelector lhs, SimpleSelector rhs, Element element) {
        checkNotNull(lhs);
        checkNotNull(rhs);
        checkNotNull(element);
        
        if (!rhs.matches(element)) {
            return false;
        }
        Element prev = element;
        int i = 0;
        while ((prev = traverser.apply(prev)) != null && i++ < times) {
            if (lhs.matches(prev)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return symbol;
    }

    public String toString(CombinatorSelector lhs, SimpleSelector rhs) {
        return lhs.toString() + (this == DESCENDANT ? DESCENDANT : " " + symbol + " ") + rhs.toString();
    }
    
    private static int once() {
        return times(1);
    }
    
    private static int infinite() {
        return Integer.MAX_VALUE;
    }
    
    private static int times(int times) {
        return times;
    }
}
