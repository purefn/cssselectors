package com.threelevers.css;

import static com.google.common.collect.Iterables.toArray;
import static com.threelevers.css.DocumentBuilder.doc;
import static com.threelevers.css.Matchers.elements;
import static com.threelevers.css.Selector.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SelectorTest {
    final static Document doc = doc(
            "<html>" +
            "  <body id=\"body\">" +
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
    public void assertThatElementsCanBeSelectedById() {
        assertThat(elementsSelectedWith("#title"), is(equalTo(elements("title").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedByClass() {
        assertThat(elementsSelectedWith(".section"), is(equalTo(elements("section1").from(doc))));
    }

    @Test
    public void assertThatAllElementsMatchingClassSelectorAreReturned() {
        assertThat(elementsSelectedWith(".title"), is(equalTo(elements("title", "subtitle").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedByTagName() {
        assertThat(elementsSelectedWith("h1"), is(equalTo(elements("title").from(doc))));
    }
    
    @Test
    public void assertThatAllElementsMatchingTagNameAreReturned() {
        assertThat(elementsSelectedWith("label"), is(equalTo(elements("first-name-label", "last-name-label", "yes-no-label", "high-label", "medium-label", "low-label", "how-many-label").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedByTagNameAndClass() {
        assertThat(elementsSelectedWith("div.section"), is(equalTo(elements("section1").from(doc))));
    }
    

    @Test
    public void assertThatAllElementsMatchingTagNameAndClassAreReturned() {
        assertThat(elementsSelectedWith("input.text"), is(equalTo(elements("first-name", "last-name").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedByMultipleSelectors() {
        assertThat(elementsSelectedWith("#title, div.section"), is(equalTo(elements("title", "section1").from(doc))));
//        assertThat(elementsSelectedWith("#title, div.section, input[type='checkbox']"), is(equalTo(elements("title", "section1", "yes-no").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithDescendantCombinator() {
        assertThat(elementsSelectedWith(".section .title"), is(equalTo(elements("subtitle").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithChildOfCombinator() {
        assertThat(elementsSelectedWith(".section > .title"), is(equalTo(elements("subtitle").from(doc))));
    }
    
    @Test
    public void assertThatElementsThatAreNotImmediateChildrenAreNotSelectedWithChildOfCombinator() {
        assertThat(elementsSelectedWith("form > #first-name"), is(emptyArray()));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithMultipleDescendantCombinators() {
        assertThat(elementsSelectedWith("#info-form fieldset .checkbox"), is(equalTo(elements("yes-no").from(doc))));
    }

    @Test
    public void assertThatNoSelectorsAreIgnoredInMultipleDescendantCombinators() {
        assertThat(elementsSelectedWith("#info-form div .checkbox"), is(emptyArray()));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithAdjacentCombinator() {
        assertThat(elementsSelectedWith("label + .checkbox"), is(equalTo(elements("yes-no").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithSiblingCombinator() {
        assertThat(elementsSelectedWith(".text ~ .checkbox"), is(equalTo(elements("yes-no").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithMultipleCombinators() {
        assertThat(elementsSelectedWith("h1 ~ form label + input"), is(equalTo(elements("first-name", "last-name", "yes-no", "high", "medium", "low").from(doc))));
    }
    
    @Test
    public void assertThatElementsWithAnAttributeCanBeSelected() {
        assertThat(elementsSelectedWith("[name]"), is(equalTo(elements("first-name", "last-name", "yes-no", "high", "medium", "low", "how-many").from(doc))));
    }
    
    @Test
    public void assertThatElementsWithAnAttributeEqualToAStringCanBeSelected() {
        assertThat(elementsSelectedWith("[name=\"first-name\"]"), is(equalTo(elements("first-name").from(doc))));
    }

    @Test
    public void assertThatElementsWithAnAttributeEqualToAnotherAttributeCanBeSelected() {
        assertThat(elementsSelectedWith("[id=name]"), is(equalTo(elements("first-name", "last-name", "yes-no", "how-many").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributePrefix() {
        assertThat(elementsSelectedWith("[name^=\"first\"]"), is(equalTo(elements("first-name").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributePrefixedByTheValueOfAnotherAttribute() {
        assertThat(elementsSelectedWith("[id^=for]"), is(equalTo(elements("first-name-label", "last-name-label", "yes-no-label", "high-label", "medium-label", "low-label", "how-many-label").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeSuffix() {
        assertThat(elementsSelectedWith("[id$=\"label\"]"), is(equalTo(elements("first-name-label", "last-name-label", "yes-no-label", "high-label", "medium-label", "low-label", "how-many-label").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeSuffixedByTheValueOfAnotherAttribute() {
        assertThat(elementsSelectedWith("[class$=id]"), is(equalTo(elements("title").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeSubstring() {
        assertThat(elementsSelectedWith("[id*=\"name\"]"), is(equalTo(elements("first-name-label", "first-name", "last-name-label", "last-name").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeValueThatContainsAnotherAttributesValueAsASubstring() {
        assertThat(elementsSelectedWith("[id*=class]"), is(equalTo(elements("section1", "info-form-fieldset1").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWhenTheyHaveAListValue() {
        assertThat(elementsSelectedWith("[class~=\"submit\"]"), is(equalTo(elements("info-form-submit").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWhenTheyHaveAListValueFromAnotherAttribute() {
        assertThat(elementsSelectedWith("[class~=type]"), is(equalTo(elements("first-name", "last-name", "yes-no", "high", "medium", "low", "info-form-submit").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeValueBeingAnExactValue() {
        assertThat(elementsSelectedWith("[hreflang|=\"fr\"]"), is(equalTo(elements("fr-link").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeValueBeginningWithValueImmediatelyFollowedByDash() {
        assertThat(elementsSelectedWith("[hreflang|=\"en\"]"), is(equalTo(elements("en-link").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithAnAttributeValueBeingAnExactValueOrIsPrefixedWithAValueFromAnotherAttribute() {
        assertThat(elementsSelectedWith("[hreflang|=lang]"), is(equalTo(elements("en-link", "fr-link").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNotPsuedoSelector() {
        assertThat(elementsSelectedWith("input:not([type=\"text\"])"), is(equalTo(elements("yes-no", "high", "medium", "low", "info-form-submit").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthChildPseudoSelectorWithATermAndBTerm() {
        assertThat(elementsSelectedWith(":nth-child(4n+3)"), is(equalTo(elements("title", "last-name-label", "high-label", "low-label", "option-2", "info-form-submit").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthChildPseudoSelectorWithATermAndNegativeBTerm() {
        assertThat(elementsSelectedWith(":nth-child(4n-1)"), is(equalTo(elements("title", "last-name-label", "high-label", "low-label", "option-2", "info-form-submit").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthChildPseudoSelectorWithNegativeATermAndBTerm() {
        assertThat(elementsSelectedWith(":nth-child(-2n+3)"), is(equalTo(elements("body", "en-link", "title", "subtitle", "info-form-fieldset1", "first-name-label", "last-name-label", "option-0", "option-2").from(doc))));
    }
    
    @Test
    public void assertThatElementsFirstNChidlrenOfAParentCanBeSelected() {
        assertThat(elementsSelectedWith("fieldset :nth-child(-n+4)"), is(equalTo(elements("first-name-label", "first-name", "last-name-label", "last-name", "option-0", "option-1", "option-2", "option-3").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithNthChildPseudoSelectorWithOnlyANegativeATerm() {
        assertThat(elementsSelectedWith(":nth-child(-4n)"), is(equalTo(elements().from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithNthChildPseudoSelectorWithOddType() {
        assertThat(elementsSelectedWith(":nth-child(odd)"), is(equalTo(elements("body", "en-link", "title", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "last-name-label", "yes-no-label", "high-label", "medium-label", "low-label", "how-many-label", "option-0", "option-2", "info-form-submit").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthChildPseudoSelectorWithEvenType() {
        assertThat(elementsSelectedWith(":nth-child(even)"), is(equalTo(elements("fr-link", "section1", "first-name", "last-name", "yes-no", "high", "medium", "low", "how-many", "option-1", "option-3", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthLastChildPseudoSelectorWithNegativeATermAndBTerm() {
        assertThat(elementsSelectedWith(":nth-last-child(-n+2)"), is(equalTo(elements("body", "subtitle", "info-form", "info-form-fieldset1", "how-many", "option-2", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthLastChildPseudoSelectorWithOddType() {
        assertThat(elementsSelectedWith(":nth-last-child(odd)"), is(equalTo(elements("body", "fr-link", "section1", "subtitle", "info-form-fieldset1", "first-name-label", "last-name-label", "yes-no-label", "high-label", "medium-label", "low-label", "how-many-label", "option-1", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthLastChildPseudoSelectorWithEvenType() {
        assertThat(elementsSelectedWith(":nth-last-child(even)"), is(equalTo(elements("en-link", "title", "info-form", "first-name", "last-name", "yes-no", "high", "medium", "low", "how-many", "option-0", "option-2").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithFirstChildPseudoSelector() {
        assertThat(elementsSelectedWith(":first-child"), is(equalTo(elements("body", "en-link", "subtitle", "info-form-fieldset1", "first-name-label", "option-0").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithLastChildPseudoSelector() {
        assertThat(elementsSelectedWith(":last-child"), is(equalTo(elements("body", "subtitle", "info-form-fieldset1", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithOnlyChildPseudoSelector() {
        assertThat(elementsSelectedWith(":only-child"), is(equalTo(elements("body", "subtitle", "info-form-fieldset1").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthOfTypeSelectorWithATermAndBTerm() {
        assertThat(elementsSelectedWith(":nth-of-type(3n+1)"), is(equalTo(elements("body", "en-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "first-name", "high-label", "high", "how-many-label", "how-many", "option-0", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthOfTypeSelectorWithOddType() {
        assertThat(elementsSelectedWith(":nth-of-type(odd)"), is(equalTo(elements("body", "en-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "first-name", "yes-no-label", "yes-no", "medium-label", "medium", "how-many-label", "how-many", "option-0", "option-2", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthOfTypeSelectorWithEvenType() {
        assertThat(elementsSelectedWith(":nth-of-type(even)"), is(equalTo(elements("fr-link", "last-name-label", "last-name", "high-label", "high", "low-label", "low", "option-1", "option-3").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthLastOfTypeSelectorWithATermAndBTerm() {
        assertThat(elementsSelectedWith(":nth-last-of-type(3n+1)"), is(equalTo(elements("body", "fr-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "first-name", "high-label", "high", "how-many-label", "how-many", "option-0", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthLastOfTypeSelectorWithOddType() {
        assertThat(elementsSelectedWith(":nth-last-of-type(odd)"), is(equalTo(elements("body", "fr-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "first-name", "yes-no-label", "yes-no", "medium-label", "medium", "how-many-label", "how-many", "option-1", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithNthLastOfTypeSelectorWithEvenType() {
        assertThat(elementsSelectedWith(":nth-last-of-type(even)"), is(equalTo(elements("en-link", "last-name-label", "last-name", "high-label", "high", "low-label", "low", "option-0", "option-2").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithFirstOfType() {
        assertThat(elementsSelectedWith(":first-of-type"), is(equalTo(elements("body", "en-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "first-name-label", "first-name", "how-many", "option-0", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithLastOfType() {
        assertThat(elementsSelectedWith(":last-of-type"), is(equalTo(elements("body", "fr-link", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "how-many-label", "how-many", "option-3", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithOnlyOfType() {
        assertThat(elementsSelectedWith(":only-of-type"), is(equalTo(elements("body", "title", "section1", "subtitle", "info-form", "info-form-fieldset1", "how-many", "empty-p").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithEmpty() {
        assertThat(elementsSelectedWith(":empty"), is(equalTo(elements("first-name", "last-name", "yes-no", "high", "medium", "low", "info-form-submit", "empty-p").from(doc))));
    }
    
    @Test
    public void assertThatElementsCanBeSelectedWithEnabled() {
        assertThat(elementsSelectedWith(":enabled"), is(equalTo(elements("first-name", "last-name", "yes-no", "high", "medium", "low", "how-many").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithDisabled() {
        assertThat(elementsSelectedWith(":disabled"), is(equalTo(elements("info-form-submit").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithChecked() {
        assertThat(elementsSelectedWith(":checked"), is(equalTo(elements("yes-no", "medium").from(doc))));
    }

    @Test
    public void assertThatElementsCanBeSelectedWithSelected() {
        assertThat(elementsSelectedWith(":selected"), is(equalTo(elements("option-1").from(doc))));
    }

    static Element[] elementsSelectedWith(String selector) {
        return toArray(from(doc).select(selector), Element.class);
    }
}
