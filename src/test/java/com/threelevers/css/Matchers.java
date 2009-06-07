package com.threelevers.css;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class Matchers {
    private Matchers() {}
    
    static Matcher<CssSelector> matches(final Element element) {
        return new Matches(element);
    }
    
    static Matcher<CssSelector> matchesAll(final Element... elements) {
        return new MatchesAll(elements);
    }
    
    static From<Element[]> elements(String... elementIds) {
        return new ElementsFrom(elementIds);
    }
    
    static From<Element> element(String elementId) {
        return new ElementFrom(elementId);
    }

    private static final class Matches extends TypeSafeDiagnosingMatcher<CssSelector> {
        private final Element element;

        private Matches(Element element) {
            this.element = element;
        }

        @Override
        protected boolean matchesSafely(CssSelector matcher, Description mismatchDescription) {
            if (!matcher.matches(element)) {
                mismatchDescription.appendText("element ").appendValue(element).appendText(" is not matched by ").appendValue(matcher);
                return false;
            }
            return true;
        }

        public void describeTo(Description description) {
            description.appendText("matches ").appendValue(element);
        }
    }

    private static final class MatchesAll extends TypeSafeDiagnosingMatcher<CssSelector> {
        private final Element[] elements;

        private MatchesAll(Element[] elements) {
            this.elements = elements;
        }

        @Override
        protected boolean matchesSafely(CssSelector cssMatcher, Description mismatchDescription) {
            for (Element element : elements) {
                Matches matcher = new Matches(element);
                if (!matcher.matchesSafely(cssMatcher, mismatchDescription)) {
                    return false;
                }
            }
            return true;
        }

        public void describeTo(Description description) {
            description.appendText("matches all ").appendValueList("[", ",", "]", elements);
        }
    }
    
    interface From<T> {
        T from(Document doc);
    }
    
    private static final class ElementFrom implements From<Element> {

        private final String elementId;

        ElementFrom(String elementId) {
            this.elementId = elementId;
        }

        public Element from(Document doc) {
            return doc.getElementById(elementId);
        }
    }
    
    private static final class ElementsFrom implements From<Element[]> {
        private final String[] elementIds;

        ElementsFrom(String[] elementIds) {
            this.elementIds = elementIds;
        }

        public Element[] from(Document doc) {
            Element[] elements = new Element[elementIds.length];
            for (int i = 0; i < elementIds.length; i++) {
                elements[i] = doc.getElementById(elementIds[i]);
            }
            return elements;
        }
    }
}
