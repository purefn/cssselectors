package com.threelevers.css;

import static com.threelevers.css.DocumentBuilder.doc;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static com.threelevers.css.Elements.*;
import static com.threelevers.css.CssSelectors.*;

import org.junit.Test;
import org.w3c.dom.Document;

public class ElementsTest {
    static final Document doc = doc(
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
            "        <label id=\"yes-no-label\" for=\"yes-no\">Yes or No?</label> <input type=\"checkbox\" id=\"yes-no\" name=\"yes-no\" class=\"checkbox\">" +
            "        <input type=\"submit\" value=\"Submit\" id=\"info-form-submit\" class=\"info form submit button\"> " +
            "      </fieldset>" +
            "    </form>" +
            "  </body>" +
            "</html>"
    );

    @Test
    public void assertThatFirstElementWithNoSelectorReturnsFirstElement() {
        assertThat(first(doc.getElementById("body")), is(sameInstance(doc.getElementById("en-link"))));
    }
    
    @Test
    public void assertThatFirstElementWithSelectorReturnsFirstMatchingElement() {
        assertThat(first(doc.getElementById("body"), tag("h1")), is(sameInstance(doc.getElementById("title"))));
    }
    
    @Test
    public void assertThatLastElementWithNoSelectorReturnsLastElement() {
        assertThat(last(doc.getElementById("body")), is(sameInstance(doc.getElementById("info-form"))));
    }
    
    @Test
    public void assertThatLastElementWithSelectorReturnsLastMatchingElement() {
        assertThat(last(doc.getElementById("body"), tag("a")), is(sameInstance(doc.getElementById("fr-link"))));
    }

    @Test
    public void assertThatNextElementWithNoSelectorReturnsNextElement() {
        assertThat(next(doc.getElementById("en-link")), is(sameInstance(doc.getElementById("fr-link"))));        
    }
    
    @Test
    public void assertThatNextElementWithSelectorReturnsNextMatchingElement() {
        assertThat(next(doc.getElementById("en-link"), tag("h1")), is(sameInstance(doc.getElementById("title"))));        
    }
}
