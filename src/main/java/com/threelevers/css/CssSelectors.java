package com.threelevers.css;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.threelevers.css.AttributeComparator.CONTAINS;
import static com.threelevers.css.AttributeComparator.ENDS_WITH;
import static com.threelevers.css.AttributeComparator.EQ;
import static com.threelevers.css.AttributeComparator.EQUALS_LANG_SUBCODE;
import static com.threelevers.css.AttributeComparator.HAS;
import static com.threelevers.css.AttributeComparator.STARTS_WITH;
import static com.threelevers.css.Combinator.ADJACENT;
import static com.threelevers.css.Combinator.CHILDOF;
import static com.threelevers.css.Combinator.DESCENDANT;
import static com.threelevers.css.Combinator.SIBLING;
import static com.threelevers.css.Nodes.*;
import static com.threelevers.css.Elements.*;

import java.util.Iterator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.w3c.dom.Element;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.*;

final class CssSelectors {
    private CssSelectors() {}
    
    static GroupingSelector selectors(String selectors) {
        checkNotNull(selectors);
        
        CssSelectorsLexer lex = new CssSelectorsLexer(new ANTLRStringStream(selectors));
        CommonTokenStream tokens = new CommonTokenStream(lex);

        CssSelectorsParser parser = new CssSelectorsParser(tokens);
        try {
            return parser.selectors();
        } catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
    }
    
    static GroupingSelector selectors(CombinableSelector selector) {
        checkNotNull(selector);
        return new GroupingSelectorImpl(selector);
    }
    
    static CombinableSelector selector(SimpleSelector selector) {
        checkNotNull(selector, "selector");
        return new CombinableSelectorImpl(selector);
    }
        
    static TypeSelector tag(String tagName) {
        checkNotBlank(tagName);
        return new TypSelectorImpl(tagName);
    }
        
    static TypeSelector any() {
        return any;
    }

    static ElementSelector id(String id) {
        checkNotBlank(id);
        return new IdSelector(id);
    }

    static ElementSelector cssClass(String className) {
        checkNotBlank(className);
        return new ClassSelector(className);
    }
    
    static AttributeSelector attrib(String attribName) {
        checkNotBlank(attribName);
        return new HasAttributeSelectorImpl(attribName);
    }
    
    static ElementSelector not(ElementSelector selector) {
        checkNotNull(selector);
        return new NotPseudoSelector(selector);
    }
    
    static ElementSelector nthChild(int a) {
        return nthChild(a, 0);
    }
    
    static ElementSelector nthChild(int a, int b) {
        return nthChild(new NthExpression(a, b));
    }
    
    static ElementSelector nthChild(NthExpression expr) {
        return new NthChildPseudoSelector(expr);
    }
    
    static ElementSelector nthLastChild(int a) {
        return nthLastChild(a, 0);
    }
    
    static ElementSelector nthLastChild(int a, int b) {
        return nthLastChild(new NthExpression(a, b));
    }
    
    static ElementSelector nthLastChild(NthExpression expr) {
        return new NthLastChildPseudoSelector(expr);
    }
    
    static ElementSelector firstChild() {
        return firstChild;
    }
    
    static ElementSelector lastChild() {
        return lastChild;
    }
    
    static ElementSelector onlyChild() {
        return onlyChild;
    }
    
    static ElementSelector nthOfType(int a, int b) {
        return nthOfType(new NthExpression(a, b));
    }
    
    static ElementSelector nthOfType(NthExpression expr) {
        return new NthOfTypePseudoSelector(expr);
    }

    static ElementSelector nthLastOfType(int a, int b) {
        return nthLastOfType(new NthExpression(a, b));
    }
    
    static ElementSelector nthLastOfType(NthExpression expr) {
        return new NthLastOfPseudoSelector(expr);
    }
    
    static ElementSelector firstOfType() {
        return firstOfType;
    }
    
    static ElementSelector lastOfType() {
        return lastOfType;
    }
    
    static ElementSelector onlyOfType() {
        return onlyOfType;
    }
    
    static ElementSelector empty() {
        return empty;
    }
    
    static ElementSelector enabled() {
        return enabled;
    }
    
    static ElementSelector disabled() {
        return disabled;
    }
    
    static ElementSelector checked() {
        return checked;
    }
    
    static ElementSelector selected() {
        return selected;
    }
    
    interface GroupingSelector extends CssSelector {
        /**
         * Combines CssSelectorMatchers together to allow for grouping selectors the same as "h1, h2, h3".
         *  
         * @param selector CssSelectorMatcher to combine with existing selectors in this CssSelectorsMatcher
         * @return new CssSelectorsMatcher with all the selectors from this or with the new selector.
         */
        GroupingSelector or(CombinableSelector selector);
    }
    
    private static class GroupingSelectorImpl implements GroupingSelector {

        private final Iterable<CombinableSelector> selectors;
        
        GroupingSelectorImpl(CombinableSelector selector) {
            selectors = ImmutableList.of(selector);
        }
        
        private GroupingSelectorImpl(Iterable<CombinableSelector>  selectors) {
            this.selectors = selectors;
        }

        public boolean matches(Element element) {
            checkNotNull(element);
            for (CssSelector selector : selectors) {
                if (selector.matches(element)) {
                    return true;
                }
            }
            return false;
        }
                
        public GroupingSelector or(CombinableSelector selector) {
            checkNotNull(selector);
            return new GroupingSelectorImpl(concat(selectors, ImmutableList.of(selector)));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Iterator<? extends CssSelector> it = selectors.iterator(); it.hasNext(); ) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            return sb.toString();
        }
    }

    interface CombinableSelector extends CssSelector {
        /**
         * Equivalent to using the CSS descendant combinator ".someclass .otherclass".
         * 
         * @param selector Matcher to use to find matching ancestor element.
         * @return new CssSelectorMatcher with the combinator
         */
        CombinableSelector ancestorOf(SimpleSelector selector);
        
        /**
         * Equivalent to using the CSS adjacent combinator ".someclass + .otherclass".
         * 
         * @param selector Matcher to use to find matching adjacent element.
         * @return new CssSelectorMatcher with the combinator
         */
        CombinableSelector adjacentTo(SimpleSelector selector);

        /**
         * Equivalent to using the CSS childof combinator ".someclass > .otherclass".
         * 
         * @param selector Matcher to use to find matching adjacent element.
         * @return new CssSelectorMatcher with the combinator
         */
        CombinableSelector parentOf(SimpleSelector selector);
        
        /**
         * Equivalent to using the CSS sibling combinator ".someclass ~ .otherclass".
         * 
         * @param selector Matcher to use to find matching adjacent element.
         * @return new CssSelectorMatcher with the combinator
         */
        CombinableSelector siblingOf(SimpleSelector selector);
    }
    
    private static class CombinableSelectorImpl implements CombinableSelector {
        private final CombinatorSelector selector;

        public CombinableSelectorImpl(SimpleSelector selector) {
            this(new NoCombinatorSelectorImpl(selector));
        }
        
        private CombinableSelectorImpl(CombinatorSelector selector) {
            this.selector = selector;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element);
            return selector.matches(element);
        }
        
        public CombinableSelector ancestorOf(SimpleSelector selector) {
            checkNotNull(selector);
            return new CombinableSelectorImpl(new CombinatorSelectorImpl(this.selector, DESCENDANT, selector));
        }
        
        public CombinableSelector adjacentTo(SimpleSelector selector) {
            checkNotNull(selector);
            return new CombinableSelectorImpl(new CombinatorSelectorImpl(this.selector, ADJACENT, selector));
        }

        public CombinableSelector parentOf(SimpleSelector selector) {
            checkNotNull(selector);
            return new CombinableSelectorImpl(new CombinatorSelectorImpl(this.selector, CHILDOF, selector));
        }

        public CombinableSelector siblingOf(SimpleSelector selector) {
            checkNotNull(selector);
            return new CombinableSelectorImpl(new CombinatorSelectorImpl(this.selector, SIBLING, selector));
        }
        
        public String toString() {
            return selector.toString();
        }
    }
    
    interface CombinatorSelector extends CssSelector {}
    
    private static class NoCombinatorSelectorImpl implements CombinatorSelector {
        private final SimpleSelector selector;

        NoCombinatorSelectorImpl(SimpleSelector selector) {
            this.selector = selector;
        }

        public boolean matches(Element element) {
            checkNotNull(element);
            return selector.matches(element);
        }
        
        public String toString() {
            return selector.toString();
        }
    }
    
    private static class CombinatorSelectorImpl implements CombinatorSelector {
        private final CombinatorSelector lhs;
        private final Combinator combinator;
        private final SimpleSelector rhs;

        CombinatorSelectorImpl(CombinatorSelector lhs, Combinator combinator, SimpleSelector rhs) {
            this.lhs = lhs;
            this.combinator = combinator;
            this.rhs = rhs;
        }

        public boolean matches(Element element) {
            return combinator.matches(lhs, rhs, element);
        }
        
        public String toString() {
            return combinator.toString(lhs, rhs);
        }
    }
    
    interface ElementSelector extends SimpleSelector {}

    private static abstract class AbstractElementSelector implements ElementSelector {
        public SimpleSelector and(ElementSelector selector) {
            checkNotNull(selector);
            return new SimpleSelectorImpl(this).and(selector);
        }
    }

    interface SimpleSelector extends CssSelector {
        /**
         * Allows you to combine multiple selectors as in ".someclass.otherclass"
         * 
         * @param selector Matcher to combine with existing selectors
         * @return new SimpleCssMatcher with the selector anded in.
         */
        SimpleSelector and(ElementSelector selector);
    }
        
    private static class SimpleSelectorImpl implements SimpleSelector {
        private final Iterable<SimpleSelector> selectors;
        
        SimpleSelectorImpl(SimpleSelector selector) {
            selectors = ImmutableList.of(selector);
        }
        
        private SimpleSelectorImpl(Iterable<SimpleSelector> selectors) {
            this.selectors = selectors;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element);
            for (CssSelector selector : selectors) {
                if (!selector.matches(element)) {
                    return false;
                }
            }
            return true;
        }        
        
        public SimpleSelector and(ElementSelector selector) {
            checkNotNull(selector);
            return new SimpleSelectorImpl(concat(selectors, ImmutableList.of(selector)));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (CssSelector selector : selectors) {
                sb.append(selector);
            }
            return sb.toString();
        }
    }
    
    interface TypeSelector extends ElementSelector {}
    
    private static class TypSelectorImpl extends AbstractElementSelector implements TypeSelector {
        private final String tagName;
        
        TypSelectorImpl(String tagName) {
            this.tagName = tagName;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element);
            return element.getTagName().equalsIgnoreCase(tagName);
        }
        
        public String toString() {
            return tagName;
        }
    }

    private static final TypeSelector any = new AnySelector();
    private static class AnySelector extends TypSelectorImpl {
        AnySelector() {
            super("*");
        }

        public boolean matches(Element element) {
            checkNotNull(element);
            return true;
        }
    }

    private static class IdSelector extends AbstractElementSelector implements ElementSelector {
        private final String id;

        public IdSelector(String id) {
            this.id = id;
        }
        
        public boolean matches(Element element) {
            return attrib("id").equalTo(id).matches(element);
        }
        
        public String toString() {
            return "#" + id;
        }
    }
    
    private static class ClassSelector extends AbstractElementSelector implements ElementSelector {
        private final String className;

        public ClassSelector(String className) {
            this.className = className;
        }
        
        public boolean matches(Element element) {
            return attrib("class").has(className).matches(element);
        }
        
        public String toString() {
            return "." + className;
        }
    }
    
    interface AttributeSelector extends ElementSelector {

        /**
         * The returned selector will check if this attribute is equal to a string value.
         * 
         * @param value value to check if the attribute is equal to
         * @return selector which checks if this attribute is equal to a string value
         */
        ElementSelector equalTo(String value);
        
        /**
         * The returned selector will check if this attributes value is equal to another attributes value.
         * 
         * @param attrib attribute to check if this attribute is equal to
         * @return selector which checks if this attributes value is equal to another attributes value.
         */
        ElementSelector equalTo(AttributeSelector attrib);

        /**
         * The returned selector will check if this attributes value begins with a prefix.
         * 
         * @param prefix String to check if the attributes value starts with
         * @return selector which checks if this attributes value begins with a prefix
         */
        ElementSelector startsWith(String prefix);

        /**
         * The returned selector will check if this attributes value begins with another attributes value.
         * 
         * @param attrib attribute whose value we'll use as a prefix
         * @return selector which checks if this attributes value begins with another attributes value.
         */
        ElementSelector startsWith(AttributeSelector attrib);

        /**
         * The returned selector will check if this attributes value ends with a suffix.
         * 
         * @param suffix String to check if the attributes value ends with
         * @return selector which checks if this attributes value ends with a suffix
         */
        ElementSelector endsWith(String suffix);

        /**
         * The returned selector will check if this attributes value ends with another attributes value.
         * 
         * @param attrib attribute whose value we'll use as a suffix
         * @return selector which checks if this attributes value ends with another attributes value.
         */
        ElementSelector endsWith(AttributeSelector attrib);

        /**
         * The returned selector will check if this attributes value contains a given substring.
         * 
         * @param substring String to check if the attributes value contains as a substring
         * @return selector which checks if this attributes value contains the substring.
         */
        ElementSelector contains(String substring);

        /**
         * The returned selector will check if this attributes value contains another attributes value as a substring.
         * 
         * @param attrib attribute whose value we'll use as a substring
         * @return selector which checks if this attributes value contains another attributes value as a substring.
         */
        ElementSelector contains(AttributeSelector attrib);

        /**
         * The returned selector will check if this attributes list of values has a given element.
         * 
         * @param element Value to check is in the attributes list of values
         * @return selector which checks if this attributes list of value contains the element.
         */
        ElementSelector has(String element);

        /**
         * The returned selector will check if this attributes list of values has another attributes value as an element.
         * 
         * @param attrib Attribute whose value we need to check is in the attributes list of values
         * @return selector which checks if this attributes list of value has the attributes value as an element.
         */
        ElementSelector has(AttributeSelector attrib);

        /**
         * The returned selector will check if this attribute is either exactly langSubcode or begins with langSubcode
         * immediately followed by a "-"
         * 
         * @param langSubcode value to check if the attributes value is exactly or begins with immediately followed by
         *                    a "-"
         * @return selector which checks if this attributes value is equal to langSubcode or beings with langSubcode
         *         immediately followed by a "-"
         */
        ElementSelector equalsLangSubcode(String langSubcode);

        /**
         * The returned selector will check if this attribute is either exactly equal to the value of another attribute
         * or begins with the attributes value immediately followed by a "-"
         * 
         * @param attrib attributes whose value we'll use to check if this attributes value matches exactly or begins 
         *               with immediately followed by a "-"
         * @return selector which checks if this attributes value is equal to another attribute
         *         or begins with the attributes value immediately followed by a "-"
         */
        ElementSelector equalsLangSubcode(AttributeSelector attrib);
    }
    
    private static final class HasAttributeSelectorImpl extends AbstractElementSelector implements AttributeSelector {
        private final String attribName;

        HasAttributeSelectorImpl(String attribName) {
            this.attribName = attribName;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element);
            return element.hasAttribute(attribName);
        }
        
        public ElementSelector equalTo(String value) {
            checkNotNull(value);
            return new AttributeComparedToStringSelectorImpl(attribName, EQ, value);
        }
        
        public ElementSelector equalTo(AttributeSelector attrib) {
            checkNotNull(attrib);
            return compareToAttribute(EQ, attrib);
        }
        
        public ElementSelector startsWith(String prefix) {
            checkNotNull(prefix);
            return new AttributeComparedToStringSelectorImpl(attribName, STARTS_WITH, prefix);
        }

        public ElementSelector startsWith(AttributeSelector attrib) {
            checkNotNull(attrib);
            return compareToAttribute(STARTS_WITH, attrib);
        }
        
        public ElementSelector endsWith(String suffix) {
            checkNotNull(suffix);
            return new AttributeComparedToStringSelectorImpl(attribName, ENDS_WITH, suffix);
        }
        
        public ElementSelector endsWith(AttributeSelector attrib) {
            checkNotNull(attrib);
            return compareToAttribute(ENDS_WITH, attrib);
        }
        
        public ElementSelector contains(String substring) {
            checkNotNull(substring);
            return new AttributeComparedToStringSelectorImpl(attribName, CONTAINS, substring);
        }
        
        public ElementSelector contains(AttributeSelector attrib) {
            checkNotNull(attrib);
            return compareToAttribute(CONTAINS, attrib);
        }
        
        public ElementSelector has(String element) {
            checkNotNull(element);
            return new AttributeComparedToStringSelectorImpl(attribName, HAS, element);
        }
        
        public ElementSelector has(AttributeSelector attrib) {
            checkNotNull(attrib);
            return compareToAttribute(HAS, attrib);
        }
        
        public ElementSelector equalsLangSubcode(String langSubcode) {
            checkNotNull(langSubcode);
            return new AttributeComparedToStringSelectorImpl(attribName, EQUALS_LANG_SUBCODE, langSubcode);
        }

        public ElementSelector equalsLangSubcode(AttributeSelector attrib) {
            checkNotNull(attrib);
            return compareToAttribute(EQUALS_LANG_SUBCODE, attrib);
        }
        
        public String toString() {
            return '[' + attribName + ']';
        }
        
        private ElementSelector compareToAttribute(AttributeComparator comp, AttributeSelector attrib) {
            return new AttributeComparedToAttributeSelectorImpl(attribName, comp, ((HasAttributeSelectorImpl) attrib).attribName);
        }
    }
    
    private static final class AttributeComparedToStringSelectorImpl extends AbstractElementSelector {
        private final String attribName;
        private final String value;
        private final AttributeComparator comparator;

        public AttributeComparedToStringSelectorImpl(String attribName, AttributeComparator comparator, String value) {
            this.attribName = attribName;
            this.comparator = comparator;
            this.value = value;
        }

        public boolean matches(Element element) {
            checkNotNull(element);
            if (!element.hasAttribute(attribName)) {
                return false;
            }
            return comparator.compare(element.getAttribute(attribName), value);
        }

        public String toString() {
            return '[' + attribName + comparator + '"' + value + "\"]";
        }
    }
    
    private static final class AttributeComparedToAttributeSelectorImpl extends AbstractElementSelector {
        private final String lhsAttribName;
        private final AttributeComparator comparator;
        private final String rhsAttribName;

        public AttributeComparedToAttributeSelectorImpl(String lhsAttribName, AttributeComparator comparator, String rhsAttribName) {
            this.lhsAttribName = lhsAttribName;
            this.comparator = comparator;
            this.rhsAttribName = rhsAttribName;
        }

        public boolean matches(Element element) {
            checkNotNull(element);
            if (!element.hasAttribute(lhsAttribName) || !element.hasAttribute(rhsAttribName)) {
                return false;
            }
            return comparator.compare(element.getAttribute(lhsAttribName), element.getAttribute(rhsAttribName));
        }

        public String toString() {
            return '[' + lhsAttribName + comparator + rhsAttribName + "]";
        }
    }
    
    private static final class NotPseudoSelector extends AbstractElementSelector {
        private final ElementSelector selector;

        public NotPseudoSelector(ElementSelector selector) {
            this.selector = selector;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return !selector.matches(element);
        }
        
        public String toString() {
            return ":not(" + selector.toString() + ")";
        }
    }
    
    private static class NthChildPseudoSelector extends AbstractElementSelector {
        private final NthExpression expr;

        NthChildPseudoSelector(NthExpression expr) {
            this.expr = expr;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (element.getParentNode() == null || !isElement(element.getParentNode())) {
                return false;
            }
            // TODO this isn't the most efficient, but it works. It can definitely be improved if we change the passed
            //      in element to be a custom Element type that stores some location information
            return expr.matches(indexOf(element) + 1);
        }

        public String toString() {
            return ":nth-child(" + expr + ")";
        }
    }
    
    private static class NthLastChildPseudoSelector extends AbstractElementSelector {
        private final NthExpression expr;

        NthLastChildPseudoSelector(NthExpression expr) {
            this.expr = expr;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (element.getParentNode() == null || !isElement(element.getParentNode())) {
                return false;
            }
            // TODO this isn't the most efficient, but it works. It can definitely be improved if we change the passed
            //      in element to be a custom Element type that stores some location information
            return expr.matches(size(nextAll(element)) + 1);
        }

        public String toString() {
            return ":nth-last-child(" + expr + ")";
        }
    }
    
    private static final ElementSelector firstChild = new FirstChildPseudoSelector();
    private static final class FirstChildPseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            return first(parent(element)) == element;
        }
        
        public String toString() {
            return ":first-child";
        }
    }
    
    private static final ElementSelector lastChild = new LastChildPseudoSelector();
    private static final class LastChildPseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            return last(parent(element)) == element;
        }
        
        public String toString() {
            return ":last-child";
        }
    }
    
    private static final ElementSelector onlyChild = new OnlyChildPseudoSelector();
    private static final class OnlyChildPseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            return first(parent(element)) == element && last(parent(element)) == element;
        }
        
        public String toString() {
            return ":only-child";
        }
    }
    
    private static final class NthOfTypePseudoSelector extends AbstractElementSelector {
        private final NthExpression expr;

        public NthOfTypePseudoSelector(NthExpression expr) {
            this.expr = expr;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            // TODO this isn't the most efficient, but it works. It can definitely be improved if we change the passed
            //      in element to be a custom Element type that stores some location information
            int index = 0;
            CssSelector selector = tag(element.getTagName());
            Element e = first(parent(element), selector);
            while (e != element) {
                e = next(e, selector);
                index++;
            }
            return expr.matches(index + 1);
        }
        
        public String toString() {
            return ":nth-of-type(" + expr + ")";
        }
    }
    
    private static class NthLastOfPseudoSelector extends AbstractElementSelector {
        private final NthExpression expr;

        public NthLastOfPseudoSelector(NthExpression expr) {
            this.expr = expr;
        }
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            // TODO this isn't the most efficient, but it works. It can definitely be improved if we change the passed
            //      in element to be a custom Element type that stores some location information
            return expr.matches(size(nextAll(element, tag(element.getTagName()))) + 1);
        }

        public String toString() {
            return ":nth-last-of-type(" + expr + ")";
        }
    }
    
    private static final ElementSelector firstOfType = new FirstOfTypePseudoSelector();
    private static final class FirstOfTypePseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            return first(parent(element), tag(element.getTagName())) == element;
        }
        
        public String toString() {
            return ":first-of-type";
        }
    }

    private static final ElementSelector lastOfType = new LastOfTypePseudoSelector();
    private static final class LastOfTypePseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            if (parent(element) == null) {
                return false;
            }
            return last(parent(element), tag(element.getTagName())) == element;
        }
        
        public String toString() {
            return ":last-of-type";
        }
    }
    
    private static final ElementSelector onlyOfType = new OnlyOfTypePseudoSelector();
    private static final class OnlyOfTypePseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return firstOfType.matches(element) && lastOfType.matches(element);
        }
        
        public String toString() {
            return ":only-of-type";
        }
    }
    
    private static final ElementSelector empty = new EmptyPseudoSelector();
    private static final class EmptyPseudoSelector extends AbstractElementSelector {
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return !element.hasChildNodes();
        }
        
        public String toString() {
            return ":empty";
        }
    }
    
    private static final ElementSelector enabled = new EnabledPseudoSelector();
    private static final class EnabledPseudoSelector extends AbstractElementSelector {
        private static final CssSelector selector = 
            selectors(selector(tag("input").and(not(attrib("disabled"))))).
            or(selector(tag("textarea").and(not(attrib("disabled"))))).
            or(selector(tag("select").and(not(attrib("disabled"))))).
            or(selector(tag("button").and(not(attrib("disabled")))));
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return selector.matches(element);
        }
        
        public String toString() {
            return ":enabled";
        }
    }
    
    private static final ElementSelector disabled = new DisabledPseudoSelector();
    private static final class DisabledPseudoSelector extends AbstractElementSelector {
        private static final CssSelector selector = 
            selectors(selector(tag("input").and(attrib("disabled")))).
            or(selector(tag("textarea").and(attrib("disabled")))).
            or(selector(tag("select").and(attrib("disabled")))).
            or(selector(tag("button").and(attrib("disabled"))));
        
        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return selector.matches(element);
        }
        
        public String toString() {
            return ":disabled";
        }
    }
    
    private static final ElementSelector checked = new CheckedPseudoSelector();
    private static final class CheckedPseudoSelector extends AbstractElementSelector {
        private static final CssSelector selector = 
            selectors(selector(attrib("type").equalTo("checkbox").and(attrib("checked")))).
            or(selector(attrib("type").equalTo("radio").and(attrib("checked"))));

        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return selector.matches(element);
        }
        
        public String toString() {
            return ":checked";
        }
    }

    private static final ElementSelector selected = new SelectedPseudoSelector();
    private static final class SelectedPseudoSelector extends AbstractElementSelector {
        private static final CssSelector selector = tag("option").and(attrib("selected"));

        public boolean matches(Element element) {
            checkNotNull(element, "element");
            return selector.matches(element);
        }
        
        public String toString() {
            return ":selected";
        }
    }

    private static String checkNotBlank(String s) {
        checkArgument(s != null && s.trim().length() > 0, "String cannot be blank");
        return s;
    }
}
