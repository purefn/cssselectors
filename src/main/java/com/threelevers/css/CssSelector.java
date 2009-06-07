package com.threelevers.css;

import org.w3c.dom.Element;

interface CssSelector {
    boolean matches(Element element);
}