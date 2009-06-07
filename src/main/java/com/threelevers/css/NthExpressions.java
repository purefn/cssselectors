/**
 * 
 */
package com.threelevers.css;

final class NthExpressions {
    static final NthExpression even = new NthExpression(2, 0) {
        public String toString() {
            return "even";
        }
    };

    static final NthExpression odd = new NthExpression(2, 1) {
        public String toString() {
            return "odd";
        }
    };
}