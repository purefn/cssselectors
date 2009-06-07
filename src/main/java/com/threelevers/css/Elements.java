package com.threelevers.css;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.threelevers.css.CssSelectors.any;
import static com.threelevers.css.Nodes.asIterable;
import static com.threelevers.css.Nodes.isElement;
import static java.util.Collections.unmodifiableList;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.base.Function;

public final class Elements {
    private Elements() {}

    static Element first(Element element) {
        return first(element, any());
    }
    
    static Element first(Element element, CssSelector selector) {
        checkNotNull(element, "element");
        checkNotNull(selector, "selector");
        Node first = element.getFirstChild();
        while (first != null && (!isElement(first) || !selector.matches((Element) first))) {
            first = first.getNextSibling();
        }
        return (Element) first;
    }
    
    static Element last(Element element) {
        return last(element, any());
    }
    
    static Element last(Element element, CssSelector selector) {
        checkNotNull(element, "element");
        checkNotNull(selector, "selector");
        Node last = element.getLastChild();
        while (!isElement(last) || !selector.matches((Element) last)) {
            last = last.getPreviousSibling();
        }
        return (Element) last;
    }
    
    static Element previous(Element element) {
        checkNotNull(element, "element");
        Node previous = element.getPreviousSibling();
        while (previous != null && !Nodes.isElement(previous)) {
            previous = previous.getPreviousSibling();
        }
        return (Element) previous;
    }
    
    static Element next(Element element) {
        return next(element, any());
    }
    
    static Element next(Element element, CssSelector selector) {
        checkNotNull(element, "element");
        checkNotNull(selector, "selector");
        Node next = element.getNextSibling();
        while (next != null && (!isElement(next) || !selector.matches((Element) next))) {
            next = next.getNextSibling();
        }
        return (Element) next;
    }
    
    static Element parent(Element element) {
        checkNotNull(element, "element");
        if (element.getParentNode() == null || !isElement(element.getParentNode())) {
            return null;
        }
        return (Element) element.getParentNode();
    }
    
    static Iterable<Element> children(Element element) {
        checkNotNull(element, "element");
        return transform(filter(asIterable(element.getChildNodes()), isElement), Elements.<Node, Element>to(Element.class));
    }

    static int indexOf(Element element) {
        checkNotNull(element, "element");
        if (element.getParentNode() == null || !isElement(element.getParentNode())) {
            return 0;
        }
        int index = 0;
        Element e = first(parent(element));
        while (e != element) {
            index++;
            e = next(e);
        }
        return index;
    }
    
    static Iterable<Element> nextAll(Element element) {
        return nextAll(element, any());
    }

    static Iterable<Element> nextAll(Element element, CssSelector selector) {
        checkNotNull(element, "element");
        checkNotNull(selector, "selector");
        List<Element> nextAll = new LinkedList<Element>();
        Element next = element;
        while ((next = next(next, selector)) != null && next != null) {
            nextAll.add(next);
        }
        return unmodifiableList(nextAll);
    }
    
    private static <FROM, TO extends FROM> Function<FROM, TO> to(final Class<TO> to) {
        return new Function<FROM, TO>() {
            public TO apply(FROM from) {
                return to.cast(from);
            }
        };
    }
}
