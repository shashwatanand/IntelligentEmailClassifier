package org.samsha.iec.servicemanager;

//java packages
import java.io.File;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EmailDoc {
	private Document doc;
	private String fileName;
	
	public EmailDoc(Document doc) {
		this.doc = doc;
	}
	
	public EmailDoc(Document doc, String fileName) {
		this.doc = doc;
		this.fileName = fileName;
		this.modifyXmlDoc();
	}
	
	private void modifyXmlDoc() {
		try {
			NodeList fileNodes = this.doc.getElementsByTagName("file");
			int noOfNodes = fileNodes.getLength();
			Node headerNode = this.doc.getElementsByTagName("header").item(0);
			if (fileNodes != null) {
				for (int index = 0; index < noOfNodes; index++) {
					headerNode.removeChild(fileNodes.item(index));
				}
			}
			Element fileElement = this.doc.createElement("file");
			fileElement.appendChild(doc.createTextNode(this.fileName));
			
			headerNode.appendChild(fileElement);
			
			TransformerFactory transformFactory = TransformerFactory.newInstance();
			Transformer transformer = transformFactory.newTransformer();
			DOMSource source = new DOMSource(this.doc);
			this.doc.normalize();
			File file = new File(this.fileName);
			file.delete();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getSubject() {
		try {
			Node node = this.doc.getElementsByTagName("subject").item(0);
			return node.getTextContent();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	private String getFile() {
		try {
			Node node = this.doc.getElementsByTagName("file").item(0);
			return node.getTextContent();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getFileName() {
		try {
			String absolutePath = getFile();
			int lastSperatorIndex = absolutePath.lastIndexOf("\\");
			int dotIndex = absolutePath.lastIndexOf(".");
			return absolutePath.substring(lastSperatorIndex + 1, dotIndex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getMessage() {
		try {
			Node node = this.doc.getElementsByTagName("message").item(0);
			return node.getTextContent();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getTo() {
		try {
			Node node = this.doc.getElementsByTagName("to").item(0);
			return node.getTextContent();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String getFrom() {
		try {
			Node node = this.doc.getElementsByTagName("from").item(0);
			return node.getTextContent();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
