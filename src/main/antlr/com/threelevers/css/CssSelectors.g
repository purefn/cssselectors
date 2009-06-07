grammar CssSelectors;

@parser::header {
    package com.threelevers.css;
    
    import static com.threelevers.css.CssSelectors.*;
    import static com.threelevers.css.Combinator.*;
    import static com.threelevers.css.AttributeComparator.*;
    import static com.threelevers.css.NthExpressions.*;
    import java.util.List;
    import java.util.LinkedList;
}

@lexer::header {
    package com.threelevers.css;
}

selectors returns [GroupingSelector selector]
    : s=selector { $selector = CssSelectors.selectors($s.selector); }
      (',' s=selector { $selector = $selector.or($s.selector); } )*
    ;
    
selector returns [CombinableSelector selector]
    : S* s=simple_selector { $selector = CssSelectors.selector($s.selector); }
      ( c=combinator s=simple_selector { 
        switch ($c.combinator) {
            case DESCENDANT:
                $selector = $selector.ancestorOf($s.selector);
                break;
            case ADJACENT:
                $selector = $selector.adjacentTo($s.selector);
                break;
            case CHILDOF:
                $selector = $selector.parentOf($s.selector);
                break;
            case SIBLING:
                $selector = $selector.siblingOf($s.selector);
                break;
        }
      })* 
    ;

combinator returns [Combinator combinator]
    : ( S { $combinator = DESCENDANT; }
      | S* '+' { $combinator = ADJACENT; }
      | S* '>' { $combinator = CHILDOF; }
      | S* '~' { $combinator = SIBLING; } ) S*
    ;

simple_selector returns [SimpleSelector selector]
    : ( type_selector { $selector = $type_selector.selector; } 
      | universal { $selector = $universal.selector; } )
      ( element_selector { $selector = $selector.and($element_selector.selector); } )*
    | e=element_selector { $selector = $e.selector; }
      ( e=element_selector { $selector.and($e.selector); })*
    ;

element_selector returns [ElementSelector selector]
    : id_selector { $selector = $id_selector.selector; }
    | class_selector { $selector = $class_selector.selector; }
    | attrib_selector { $selector = $attrib_selector.selector; }
    | pseudo_selector { $selector = $pseudo_selector.selector; }
    ;

// not supporting namespaces at this time
type_selector returns [TypeSelector selector]
    : /* ( namespace_prefix )? */ element_name { $selector = tag($element_name.text); }
    ;

element_name
    : IDENT
    ;

// not supporting namespaces at this time
universal returns [TypeSelector selector]
    : /* ( namespace_prefix )? */ '*' { $selector = any(); }
    ;


id_selector returns [ElementSelector selector]
    : '#' id=IDENT { $selector = id($id.text); }
    ;

class_selector returns [ElementSelector selector]
    : '.' className=IDENT { $selector = cssClass($className.text); }
    ;

// not supporting namespaces at this time
attrib_selector returns [ElementSelector selector]
    : '[' S* /* ( namespace_prefix )? */ attribName=IDENT { $selector = attrib($attribName.text); } S*
      ( ( '=' S* v=STRING { $selector = ((AttributeSelector) selector).equalTo($v.text); }
        | '^=' S* v=STRING { $selector = ((AttributeSelector) selector).startsWith($v.text); }
        | '$=' S* v=STRING { $selector = ((AttributeSelector) selector).endsWith($v.text); }
        | '*=' S* v=STRING { $selector = ((AttributeSelector) selector).contains($v.text); }
        | '~=' S* v=STRING { $selector = ((AttributeSelector) selector).has($v.text); }
        | '|=' S* v=STRING { $selector = ((AttributeSelector) selector).equalsLangSubcode($v.text); }
        | '=' S* a=IDENT { $selector = ((AttributeSelector) selector).equalTo(attrib($a.text)); }
        | '^=' S* a=IDENT { $selector = ((AttributeSelector) selector).startsWith(attrib($a.text)); }
        | '$=' S* a=IDENT { $selector = ((AttributeSelector) selector).endsWith(attrib($a.text)); }
        | '*=' S* a=IDENT { $selector = ((AttributeSelector) selector).contains(attrib($a.text)); }
        | '~=' S* a=IDENT { $selector = ((AttributeSelector) selector).has(attrib($a.text)); }
        | '|=' S* a=IDENT { $selector = ((AttributeSelector) selector).equalsLangSubcode(attrib($a.text)); }
        ) S*
      )? ']'
    ;

pseudo_selector returns [ElementSelector selector]
    /* '::' starts a pseudo-element, ':' a pseudo-class */
    /* Exceptions: :first-line, :first-letter, :before and :after. */
    /* Note that pseudo-elements are restricted to one per selector and */
    /* occur only in the last simple_selector_sequence. */
    : ':' ':'?
        ( negation { $selector = $negation.selector; }
        | functional_pseudo_selector { $selector = $functional_pseudo_selector.selector; }
        )
    ;

functional_pseudo_selector returns [ElementSelector selector]
    : nth_child { $selector = $nth_child.selector; }
    | nth_last_child { $selector = $nth_last_child.selector; }
    | 'first-child' { $selector = firstChild(); }
    | 'last-child' { $selector = lastChild(); }
    | 'only-child' { $selector = onlyChild(); }
    | nth_of_type { $selector = $nth_of_type.selector; }
    | nth_last_of_type { $selector = $nth_last_of_type.selector; }
    | 'first-of-type' { $selector = firstOfType(); }
    | 'last-of-type' { $selector = lastOfType(); }
    | 'only-of-type' { $selector = onlyOfType(); }
    | 'empty' { $selector = empty(); }
    | 'enabled' { $selector = enabled(); }
    | 'disabled' { $selector = disabled(); }
    | 'checked' { $selector = checked(); }
    | 'selected' { $selector = selected(); }
    ;
    
nth_child returns [ElementSelector selector]
    : 'nth-child(' S*
      ( nth_expr {
          NthExpression nthExpr = CssSelectorsParserHelper.parseNthExpression($nth_expr.text);
          $selector = nthChild(nthExpr); 
        }
      | 'odd' S* { $selector = nthChild(odd); }
      | 'even' S* { $selector = nthChild(even); }
      ) ')'
    ;
    
nth_last_child returns [ElementSelector selector]
    : 'nth-last-child(' S*
      ( nth_expr {
          NthExpression nthExpr = CssSelectorsParserHelper.parseNthExpression($nth_expr.text);
          $selector = nthLastChild(nthExpr); 
        }
      | 'odd' S* { $selector = nthLastChild(odd); }
      | 'even' S* { $selector = nthLastChild(even); }
      ) ')'
    ;
      
nth_of_type returns [ElementSelector selector]
    : 'nth-of-type(' S*
      ( nth_expr {
          NthExpression nthExpr = CssSelectorsParserHelper.parseNthExpression($nth_expr.text);
          $selector = nthOfType(nthExpr); 
        }
      | 'odd' S* { $selector = nthOfType(odd); }
      | 'even' S* { $selector = nthOfType(even); }
      ) ')'
    ;

nth_last_of_type returns [ElementSelector selector]
    : 'nth-last-of-type(' S*
      ( nth_expr {
          NthExpression nthExpr = CssSelectorsParserHelper.parseNthExpression($nth_expr.text);
          $selector = nthLastOfType(nthExpr); 
        }
      | 'odd' S* { $selector = nthLastOfType(odd); }
      | 'even' S* { $selector = nthLastOfType(even); }
      ) ')'
    ;

// NOTE: this is the way i'd like to do the nth_expr, but i can't figure out how to make antlr not see -n and n-1 in
//       expressions like -n+1 and 4n-1 as IDENTs instead of as DASH 'n' and 4 'n' DASH 1 so i'm punting for now
//nth_expr returns [NthExpression expr]
//    : sign_a=DASH? magnitude_a=NUMBER? 'n' S* sign_b=( '+' | DASH) S* magnitude_b=NUMBER {
//          $expr = new NthExpression(CssSelectorsParserHelper.valueOf($sign_a, $magnitude_a), CssSelectorsParserHelper.valueOf($sign_b, $magnitude_b));
//      }
//    | magnitude_a=NUMBER? 'n' { $expr = new NthExpression(CssSelectorsParserHelper.valueOf($sign_a, $magnitude_a), 0); }
//    | magnitude_b=NUMBER { $expr = new NthExpression(0, CssSelectorsParserHelper.valueOf($sign_b, $magnitude_b)); }
//    ;

nth_expr
    : ( ( '+' | '-' | NUMBER | STRING | IDENT ) S* )+ 
    ;

negation returns [ElementSelector selector ]
    : 'not(' S* negation_arg S* ')' { $selector = not($negation_arg.selector); }
    ;

negation_arg returns [ElementSelector selector]
    : type_selector { $selector = $type_selector.selector; }
    | universal { $selector = $universal.selector; }
    | id_selector { $selector = $id_selector.selector; }
    | class_selector { $selector = $class_selector.selector; }
    | attrib_selector { $selector = $attrib_selector.selector; }
    | functional_pseudo_selector
    ;

IDENT
    :   DASH? ('_' | 'a'..'z'| 'A'..'Z' | '\u0100'..'\ufffe' ) ('_' | DASH | 'a'..'z'| 'A'..'Z' | '\u0100'..'\ufffe' | '0'..'9')*
    ;

DASH
    : '-'
    ;

NUMBER
    : ( ( '0'..'9' )* '.' )? ( '0'..'9' )+
    ;

STRING
    : '"'! ( ~( '"'|'\n'|'\r' ) )* '"'! { setText(getText().substring(1, getText().length() - 1)); }
    | '\''! ( ~('\''|'\n'|'\r' ) )* '\''! { setText(getText().substring(1, getText().length() - 1)); }
    ;

S
    : ( ' ' | '\t' | '\r' | '\n' | '\f' )
    ;
