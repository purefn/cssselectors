package com.threelevers.css;

import static com.threelevers.css.CssSelectorsParserHelper.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class CssSelectorsParserHelperTest {

    @Test
    public void assertThatParseNthExpressionHandlesFullExpression() {
        assertThat(parseNthExpression("4n+3"), is(equalTo(new NthExpression(4, 3))));
    }
    
    @Test
    public void assertThatParseNthExpressionHandlesFullExpressionWithWhitespace() {
        assertThat(parseNthExpression("   4n  +   3    "), is(equalTo(new NthExpression(4, 3))));
    }

    @Test
    public void assertThatParseNthExpressionHandlesJustATerm() {
        assertThat(parseNthExpression("4n"), is(equalTo(new NthExpression(4, 0))));
    }

    @Test
    public void assertThatParseNthExpressionHandlesJustBTerm() {
        assertThat(parseNthExpression("4"), is(equalTo(new NthExpression(0, 4))));
    }
    
    
}
