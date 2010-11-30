/**
 * vlove - web based virtual machine management
 * Copyright (C) 2010 Limone Fresco Limited
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package vlove.util;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import vlove.VirtException;

/**
 * Allow us to load documents and do XPath stuff against it.
 * 
 * @author Michael Laccetti
 */
public class XPathUtils {
	private static DocumentBuilderFactory factory;
	private static XPathFactory xFact;
	
	static {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		xFact = XPathFactory.newInstance();
	}
	
	/**
	 * Convert a byte[] into a proper DOM Document.
	 * @param content
	 * @return
	 * @throws VirtException
	 */
	public static Document loadDocument(byte[] content) throws VirtException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(content));
			return doc;
		} catch (Exception ex) {
			throw new VirtException("Could not convert byte array to Document.", ex);
		}
	}
	
	/**
	 * Match the XPath expression against the DOM Document.
	 * @param doc
	 * @param xExpr
	 * @param nodeType
	 * @return
	 * @throws VirtException
	 */
	public static Object parseXPathExpression(Document doc, String xExpr, QName nodeType) throws VirtException {
		try {
			XPath xpath = xFact.newXPath();
			XPathExpression expr = xpath.compile(xExpr);
			Object n = expr.evaluate(doc, nodeType);
			return n;
		} catch (Exception ex) {
			throw new VirtException("Could not process XPath expression.", ex);
		}
	}
}