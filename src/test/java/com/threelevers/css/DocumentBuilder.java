package com.threelevers.css;

import java.io.IOException;
import java.io.StringReader;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentBuilder {

    public static Document doc(String d) {
        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(d)));
            return parser.getDocument();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
