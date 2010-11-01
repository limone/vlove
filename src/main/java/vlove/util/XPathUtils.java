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

public class XPathUtils {
	private static DocumentBuilderFactory factory;
	private static XPathFactory xFact;
	
	static {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		xFact = XPathFactory.newInstance();
	}
	
	public static Document loadDocument(byte[] content) throws VirtException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(content));
			return doc;
		} catch (Exception ex) {
			throw new VirtException("Could not convert byte array to Document.", ex);
		}
	}
	
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