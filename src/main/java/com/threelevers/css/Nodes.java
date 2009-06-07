package com.threelevers.css;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;

public final class Nodes {
    private Nodes() {}
    
    static boolean isElement(Node node) {
        return isElement.apply(node);
    }
    
    static Iterable<Node> asIterable(final NodeList nodes) {
        return new Iterable<Node>() {
            public Iterator<Node> iterator() {
                return new AbstractIterator<Node>() {
                    int index = -1;
                    
                    @Override
                    protected Node computeNext() {
                        if (nodes.getLength() == index + 1) {
                            endOfData();
                            return null;
                        }
                        return nodes.item(++index);
                    }
                };
            }
        };
    }
    
    static final Predicate<Node> isElement = new Predicate<Node>() {
        public boolean apply(Node node) {
            return node.getNodeType() == Node.ELEMENT_NODE;
        }
    };
}
