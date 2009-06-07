package com.threelevers.css;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CssSelectorsParserHelper {
    private static final Pattern NTH_EXPR_A_AND_TERMS = Pattern.compile("\\s*(\\-?)(\\d*)\\s*n\\s*([+\\-])\\s*(\\d*)\\s*");
    private static final Pattern NTH_EXPR_A_TERM_ONLY = Pattern.compile("\\s*(\\-?)(\\d*)\\s*n\\s*");
    private static final Pattern NTH_EXPR_B_TERM_ONLY = Pattern.compile("\\s*(\\-?)(\\d+)\\s*");

    
    private CssSelectorsParserHelper() {}
    
    static NthExpression parseNthExpression(String text) {
        if (isBlank(text)) {
            return new NthExpression(0, 0);
        }
        
        Matcher matcher = NTH_EXPR_A_AND_TERMS.matcher(text);
        if (matcher.matches()) {
            int a = valueOf(matcher.group(1), matcher.group(2));
            int b = valueOf(matcher.group(3), matcher.group(4));
            return new NthExpression(a, b);
        }
        matcher = NTH_EXPR_A_TERM_ONLY.matcher(text);
        if (matcher.matches()) {
            int a = valueOf(matcher.group(1), matcher.group(2));
            return new NthExpression(a, 0);
        }
        matcher = NTH_EXPR_B_TERM_ONLY.matcher(text);
        if (matcher.matches()) {
            int b = valueOf(matcher.group(1), matcher.group(2));
            return new NthExpression(0, b);
        }
        throw new IllegalArgumentException("'" + text + "' is not in the form an+b");
    }
    
    private static int valueOf(String sign, String magnitude) {
        int value = 1;
        if (sign != null && "-".equals(sign)) {
            value = -value;
        }
        if (!isBlank(magnitude)) {
            value *= Integer.valueOf(magnitude);
        }
        return value;
    }
    
    private static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
