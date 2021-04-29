package org.spldev.formula.expression.io;

import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * This is an extension of a default xml reader, which saves the line numbers
 * via user data. Original code by:
 * http://eyalsch.wordpress.com/2010/11/30/xml-dom-2/
 *
 * @author Jens Meinicke
 * @author Sebastian Krieter
 */
public class PositionalXMLHandler extends DefaultHandler {

	public final static String LINE_NUMBER_KEY_NAME = "lineNumber";

	private final LinkedList<Element> elementStack = new LinkedList<>();
	private final StringBuilder textBuffer = new StringBuilder();

	private final Document doc;

	private Locator locator;

	public PositionalXMLHandler(Document doc) {
		super();
		this.doc = doc;
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
		throws SAXException {
		addTextIfNeeded();
		final Element el = doc.createElement(qName);
		for (int i = 0; i < attributes.getLength(); i++) {
			el.setAttribute(attributes.getQName(i), attributes.getValue(i));
		}
		el.setUserData(LINE_NUMBER_KEY_NAME, locator.getLineNumber(), null);
		elementStack.push(el);
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		addTextIfNeeded();
		final Element closedEl = elementStack.pop();
		if (elementStack.isEmpty()) {
			doc.appendChild(closedEl);
		} else {
			final Element parentEl = elementStack.peek();
			parentEl.appendChild(closedEl);
		}
	}

	@Override
	public void characters(final char ch[], final int start, final int length) throws SAXException {
		textBuffer.append(ch, start, length);
	}

	private void addTextIfNeeded() {
		if (textBuffer.length() > 0) {
			final Element el = elementStack.peek();
			final Node textNode = doc.createTextNode(textBuffer.toString());
			el.appendChild(textNode);
			textBuffer.delete(0, textBuffer.length());
		}
	}

}
