package com.threelevers.css;

import static com.threelevers.css.CssSelectors.any;
import static com.threelevers.css.CssSelectors.attrib;
import static com.threelevers.css.CssSelectors.cssClass;
import static com.threelevers.css.CssSelectors.disabled;
import static com.threelevers.css.CssSelectors.empty;
import static com.threelevers.css.CssSelectors.enabled;
import static com.threelevers.css.CssSelectors.firstChild;
import static com.threelevers.css.CssSelectors.firstOfType;
import static com.threelevers.css.CssSelectors.id;
import static com.threelevers.css.CssSelectors.lastChild;
import static com.threelevers.css.CssSelectors.lastOfType;
import static com.threelevers.css.CssSelectors.nthChild;
import static com.threelevers.css.CssSelectors.nthLastChild;
import static com.threelevers.css.CssSelectors.nthLastOfType;
import static com.threelevers.css.CssSelectors.nthOfType;
import static com.threelevers.css.CssSelectors.onlyChild;
import static com.threelevers.css.CssSelectors.onlyOfType;
import static com.threelevers.css.CssSelectors.selector;
import static com.threelevers.css.CssSelectors.selectors;
import static com.threelevers.css.CssSelectors.*;
import static com.threelevers.css.DocumentBuilder.doc;
import static com.threelevers.css.Matchers.element;
import static com.threelevers.css.Matchers.elements;
import static com.threelevers.css.Matchers.matches;
import static com.threelevers.css.Matchers.matchesAll;
import static com.threelevers.css.NthExpressions.even;
import static com.threelevers.css.NthExpressions.odd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import org.junit.Test;
import org.w3c.dom.Document;

public class CssSelectorsTest {
    static final Document doc = doc(
            "<html>" +
            "  <body>" +
            "    <a id=\"en-link\" hreflang=\"en-US\" lang=\"en\">US</a>" +
            "    <a id=\"fr-link\" hreflang=\"fr\" lang=\"fr\">Fr</a>" +
            "    <h1 id=\"title\" class=\"main title\">Title</h1>" +
            "    <div id=\"section1\" class=\"section\">" +
            "      <h2 id=\"subtitle\" class=\"sub title\">Subtitle</h2>" +
            "    </div>" +
            "    <form id=\"info-form\">" +
            "      <fieldset id=\"info-form-fieldset1\" class=\"form-fields\">" +
            "        <label id=\"first-name-label\" for=\"first-name\">First Name:</label> <input type=\"text\" id=\"first-name\" name=\"first-name\" class=\"text\">" +
            "        <label id=\"last-name-label\" for=\"last-name\">Last Name:</label> <input type=\"text\" id=\"last-name\" name=\"last-name\" class=\"text\">" +
            "        <label id=\"yes-no-label\" for=\"yes-no\">Yes or No?</label> <input type=\"checkbox\" id=\"yes-no\" name=\"yes-no\" class=\"checkbox\" checked>" +
            "        <label id=\"high-label\" for=\"high\">High</label> <input type=\"radio\" id=\"high\" name=\"high-medium-low\" class=\"radio\">" +
            "        <label id=\"medium-label\" for=\"medium\">Medium</label> <input type=\"radio\" id=\"medium\" name=\"high-medium-low\" class=\"radio\" checked>" +
            "        <label id=\"low-label\" for=\"low\">Low</label> <input type=\"radio\" id=\"low\" name=\"high-medium-low\" class=\"radio\">" +
            "        <label id=\"how-many-label\" for=\"how-many\">How many?</label> <select id=\"how-many\" name=\"how-many\">" +
            "            <option id=\"option-0\" value=\"0\">0-5</option>" +
            "            <option id=\"option-1\" value=\"1\" selected>6-20</option>" +
            "            <option id=\"option-2\" value=\"2\">21-50</option>" +
            "            <option id=\"option-3\" value=\"3\">51-100</option>" +
            "        </select>" +
            "        <input type=\"submit\" value=\"Submit\" id=\"info-form-submit\" class=\"info form submit button\" disabled> " +
            "      </fieldset>" +
            "    </form>" +
            "    <p id=\"empty-p\"></p>" +
            "  </body>" +
            "</html>"
    );

    @Test
    public void assertThatTagSelectorMatchesElementWithSameTag() {
        assertThat(tag("form"), matches(element("info-form").from(doc)));
    }
    
    @Test
    public void assertThatAnyTagSelectorMatchesAnyTag() {
        assertThat(any(), matches(doc.createElement("randomtag")));
    }
    
    @Test
    public void assertThatClassSelectorMatchesElementContainingClass() {
        assertThat(cssClass("section"), matches(element("section1").from(doc)));
    }
    
    @Test
    public void assertThatCompoundMatcherMatchesElementFittingMultipleSelectors() {
        assertThat(selector(tag("h1").and(cssClass("main")).and(cssClass("title"))), matches(element("title").from(doc)));
    }
    
    @Test
    public void assertThatDecendantCombinatorMatchesElementWithMatchingGrandparent() {
        assertThat(selector(tag("body")).ancestorOf(tag("h2")), matches(element("subtitle").from(doc)));
    }
    
    @Test
    public void assertThatDecendantCombinatorDoesNotMatchWhenElementDoesNotHaveAMatchingAncestor() {
        assertThat(selector(tag("h2")).ancestorOf(tag("form")), not(matches(element("subtitle").from(doc))));
    }
    
    @Test
    public void assertThatAdjacentCombinatorMatches() {
        assertThat(selector(tag("div")).adjacentTo(tag("form")), matches(element("info-form").from(doc)));
    }
    
    @Test
    public void assertThatAdjacentCombinatorDoesNotMatchNoneAdjacentElement() {
        assertThat(selector(tag("h1")).adjacentTo(tag("form")), not(matches(element("info-form").from(doc))));
    }
    
    @Test
    public void assertThatParentOfCombinatorMatches() {
        assertThat(selector(tag("body")).parentOf(tag("form")), matches(element("info-form").from(doc)));
    }

    @Test
    public void assertThatParentOfCombinatorDoesNotMatchWhenThereIsNotAnImmediateMatchingChild() {
        assertThat(selector(tag("body")).parentOf(tag("h2")), not(matches(element("subtitle").from(doc))));
    }
    
    @Test
    public void assertThatMultipleDecendantCombinatorMatches() {
        assertThat(selector(id("info-form")).ancestorOf(tag("fieldset")).ancestorOf(cssClass("checkbox")), matches(element("yes-no").from(doc)));
    }

    @Test
    public void assertThatHasAttributeSelectorMatchesElementWithGivenAttribute() {
        assertThat(attrib("name"), matches(element("first-name").from(doc)));
    }

    @Test
    public void assertThatMultipleClassSelectorsMatchMultipleElements() {
        assertThat(
            selectors(selector(cssClass("section"))).or(selector(cssClass("checkbox"))),
            matchesAll(elements("section1", "yes-no").from(doc))
        );
    }
    
    @Test
    public void assertThatAttribSelectorMatchesElementWithGivenAttribute() {
        assertThat(attrib("name"), matchesAll(elements("first-name", "last-name", "yes-no").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsWithGivenAttributeEqualToAString() {
        assertThat(attrib("name").equalTo("first-name"), matchesAll(elements("first-name").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsWithGivenAttributeEqualToAnotherAttrib() {
        assertThat(attrib("id").equalTo(attrib("name")), matchesAll(elements("last-name").from(doc)));
    }
    
    @Test
    public void assertThatAttribSelectorComparingAttributesDoesNotMatchElementsWithMissingAttributes() {
        assertThat(attrib("attrib1").equalTo(attrib("attrib2")), not(matches(element("last-name").from(doc))));
    }
    
    @Test
    public void assertThatAttribSelectorMatchesElementsStartingWithGivenPrefix() {
        assertThat(attrib("name").startsWith("first"), matches(element("first-name").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsStartingWithAnotherAttributesValue() {
        assertThat(attrib("id").startsWith(attrib("for")), matchesAll(elements("first-name-label", "last-name-label", "yes-no-label").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsEndingWithGivenSuffix() {
        assertThat(attrib("id").endsWith("label"), matchesAll(elements("first-name-label", "last-name-label", "yes-no-label").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsEndingWithAnotherAttributesValue() {
        assertThat(attrib("class").endsWith(attrib("id")), matches(element("title").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsContainingGivenSubstring() {
        assertThat(attrib("id").contains("name"), matchesAll(elements("first-name-label", "first-name", "last-name-label", "last-name").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsContainingAnotherAttributesValueAsASubstring() {
        assertThat(attrib("id").contains(attrib("class")), matchesAll(elements("section1", "info-form-fieldset1").from(doc)));
    }
    
    @Test
    public void assertThatAttribSelectorMatchesElementsThatHaveASpecificValueInTheList() {
        assertThat(attrib("class").has("form"), matches(element("info-form-submit").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsThatHaveAnotherAttributesValueInTheList() {
        assertThat(attrib("class").has(attrib("type")), matches(element("info-form-submit").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsThatHaveAnExactValue() {
        assertThat(attrib("hreflang").equalsLangSubcode("fr"), matchesAll(elements("fr-link").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsThatBeginsWithAnExactValueFollowedByDash() {
        assertThat(attrib("hreflang").equalsLangSubcode("en"), matchesAll(elements("en-link").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsThatHaveAnExactValueFromAnotherAttribute() {
        assertThat(attrib("hreflang").equalsLangSubcode(attrib("lang")), matchesAll(elements("fr-link").from(doc)));
    }

    @Test
    public void assertThatAttribSelectorMatchesElementsThatHaveValueFromAnotherAttributeFollowedByDash() {
        assertThat(attrib("hreflang").equalsLangSubcode(attrib("lang")), matchesAll(elements("en-link").from(doc)));
    }
    
    @Test
    public void assertThatNotPseudoSelectorMatchesElementsWhereAttribSelectorDoesNot() {
        assertThat(CssSelectors.not(attrib("type=\"text\"")), matchesAll(elements("yes-no", "info-form-submit").from(doc)));
    }
    
    @Test
    public void assertThatNthChildPseudoSelectorMatchesElementsWhenOnlyATermIsPresent() {
        assertThat(nthChild(2), matchesAll(elements("first-name", "last-name", "yes-no").from(doc)));
    }

    @Test
    public void assertThatNthChildPseudoSelectorMatchesElementsOnlySingleElementWhenBTermIsPresent() {
        assertThat(nthChild(0, 2), matches(element("first-name").from(doc)));
        assertThat(nthChild(0, 2), not(matches(element("last-name").from(doc))));
    }
    
    @Test
    public void assertThatNthChildPseudoSelectorMatchesWithATermAndBTermArePresent() {
        assertThat(nthChild(4, 3), matchesAll(elements("last-name-label").from(doc)));
    }
    
    @Test
    public void assertThatNthChildPseudoSelectorMatchesWithATermAndNegativeBTermArePresent() {
        assertThat(nthChild(4, -1), matchesAll(elements("last-name-label").from(doc)));
    }

    @Test
    public void assertThatNthChildPseudoSelectorMatchesOddElements() {
        assertThat(nthChild(odd), matchesAll(elements("en-link", "title", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "last-name-label", "yes-no-label", "info-form-submit").from(doc)));
    }
    
    @Test
    public void assertThatNthChildPseudoSelectorForOddDoesNotMatchEvenElements() {
        assertThat(nthChild(odd), not(matchesAll(elements("fr-link", "section1", "first-name", "last-name", "yes-no").from(doc))));
    }

    @Test
    public void assertThatNthChildPseudoSelectorMatchesEvenElements() {
        assertThat(nthChild(even), matchesAll(elements("fr-link", "section1", "first-name", "last-name", "yes-no").from(doc)));
    }
    
    @Test
    public void assertThatNthChildPseudoSelectorForEvensDoesNotMatchOddElements() {
        assertThat(nthChild(even), not(matchesAll(elements("en-link", "title", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "last-name-label", "yes-no-label", "info-form-submit").from(doc))));
    }
    
    @Test
    public void assertThatNthLastChildPseudoSelectorForLast2MatchesOnlyLast2Elements() {
        assertThat(nthLastChild(-1, 2), matchesAll(elements("info-form-submit", "how-many").from(doc)));
        assertThat(nthLastChild(-1, 2), not(matches(element("low-label").from(doc))));
    }
    
    @Test
    public void assertThatFirstChildPseudoSelectorMatchesFirstElement() {
        assertThat(firstChild(), matchesAll(elements("en-link", "subtitle", "info-form-fieldset1", "first-name-label").from(doc)));
    }
    
    @Test
    public void assertThatFirstChildPseudoSelectorDoesNotMatchNonFirstElements() {
        assertThat(firstChild(), allOf(not(matches(element("fr-link").from(doc))), (not(matches(element("info-form").from(doc))))));
    }
    
    @Test
    public void assertThatLastChildPseudoSelectorMatchesLastElement() {
        assertThat(lastChild(), matchesAll(elements("subtitle", "info-form-fieldset1", "info-form-submit", "empty-p").from(doc)));
    }

    @Test
    public void assertThatLastChildPseudoSelectorDoesNotMatchNonLastElements() {
        assertThat(lastChild(), allOf(not(matches(element("fr-link").from(doc))), not(matches(element("section1").from(doc))), not(matches(element("first-name-label").from(doc)))));
    }

    @Test
    public void assertThatOnlyChildPseudoSelectorOnlyMatchesOnlyChildren() {
        assertThat(onlyChild(), matchesAll(elements("subtitle", "info-form-fieldset1").from(doc)));
    }

    @Test
    public void assertThatOnlyChildPseudoSelectorDoesNotMatchNonOnlyChildren() {
        assertThat(onlyChild(), allOf(not(matches(element("fr-link").from(doc))), not(matches(element("section1").from(doc))), not(matches(element("first-name-label").from(doc)))));
    }
    
    @Test
    public void assertThatNthOfTypePseudoSelectorMatchesNthTypeElements() {
        assertThat(nthOfType(3, 1), matchesAll(elements("en-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "first-name", "info-form-submit").from(doc)));
    }

    @Test
    public void assertThatNthOfTypePseudoSelectorDoesNotMatchNonNthOfTypeChildren() {
        assertThat(nthOfType(3, 1), not(matchesAll(elements("fr-link", "last-name-label", "last-name").from(doc))));
    }

    @Test
    public void assertThatNthLastOfTypePseudoSelectorMatchesNthTypeElements() {
        assertThat(nthLastOfType(3, 1), matchesAll(elements("first-name", "high-label", "info-form-submit").from(doc)));
    }

    @Test
    public void assertThatNthLastOfTypePseudoSelectorDoesNotMatchNonNthOfTypeChildren() {
        assertThat(nthLastOfType(3, 1), not(matchesAll(elements("first-name-label", "last-name-label", "last-name", "yes-no").from(doc))));
    }

    @Test
    public void assertThatFirstOfTypePseudoSelectorMatchesFirstOfTypeElements() {
        assertThat(firstOfType(), matchesAll(elements("en-link", "first-name-label", "first-name").from(doc)));
    }

    @Test
    public void assertThatFirstOfTypePseudoSelectorDoesNotMatchNonFirstOfTypeElements() {
        assertThat(firstOfType(), not(matchesAll(elements("fr-link", "last-name-label", "yes-no").from(doc))));
    }

    @Test
    public void assertThatLastOfTypePseudoSelectorMatchesLastOfTypeElements() {
        assertThat(lastOfType(), matchesAll(elements("fr-link", "how-many-label", "info-form-submit").from(doc)));
    }

    @Test
    public void assertThatLastOfTypePseudoSelectorDoesNotMatchNonLastOfTypeElements() {
        assertThat(lastOfType(), not(matchesAll(elements("en-link", "first-name-label", "yes-no").from(doc))));
    }

    @Test
    public void assertThatOnlyOfTypePseudoSelectorMatchesOnlyOfTypeElements() {
        assertThat(onlyOfType(), matchesAll(elements("title", "section1", "subtitle", "info-form", "info-form-fieldset1").from(doc)));
    }

    @Test
    public void assertThatOnlyOfTypePseudoSelectorDoesNotMatchNonOnlyOfTypeElements() {
        assertThat(onlyOfType(), not(matchesAll(elements("en-link", "first-name-label", "yes-no").from(doc))));
    }

    @Test
    public void assertThatEmptyPseudoSelectorMatchesEmptyElements() {
        assertThat(empty(), matchesAll(elements("first-name", "last-name", "yes-no", "empty-p").from(doc)));
    }

    @Test
    public void assertThatEmptyPseudoSelectorDoesNotMatchNonEmptyElements() {
        assertThat(empty(), not(matchesAll(elements("en-link", "first-name-label", "info-form").from(doc))));
    }

    @Test
    public void assertThatEnabledPseudoSelectorMatchesEnabledElements() {
        assertThat(enabled(), matchesAll(elements("first-name", "last-name", "yes-no").from(doc)));
    }

    @Test
    public void assertThatEnabledPseudoSelectorDoesNotMatchDisabledElements() {
        assertThat(enabled(), not(matchesAll(elements("info-form-submit").from(doc))));
    }

    @Test
    public void assertThatDisabledPseudoSelectorMatchesDisabledElements() {
        assertThat(disabled(), matchesAll(elements("info-form-submit").from(doc)));
    }

    @Test
    public void assertThatDisabledPseudoSelectorDoesNotMatchEnabledElements() {
        assertThat(disabled(), not(matchesAll(elements("first-name", "last-name").from(doc))));
    }
    
    @Test
    public void assertThatCheckedPseudoSelectorMatchesCheckedElements() {
        assertThat(checked(), matchesAll(elements("yes-no", "medium").from(doc)));
    }
    
    @Test
    public void assertThatCheckedPseudoSelectorDoesNotMatchNonCheckedElements() {
        assertThat(checked(), not(matchesAll(elements("high", "low").from(doc))));
    }

    @Test
    public void assertThatSelectedPseudoSelectorMatchesSelectedElements() {
        assertThat(selected(), matchesAll(elements("option-1").from(doc)));
    }
    
    @Test
    public void assertThatSelectedPseudoSelectorDoesNotMatchNonSelectedElements() {
        assertThat(selected(), not(matchesAll(elements("option-0", "option-3").from(doc))));
    }
}
