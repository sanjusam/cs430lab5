package cs.cs430.lab4;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A class that parses the xml, store all the records read into a queue of the data structure "Transaction Records".
 * The queue is returned to the caller.
 */

class ParseXMLData {
    Queue<TransactionRecord> readXML(final String fileName) {
        final Queue<TransactionRecord> parsedDataQueue = new ArrayDeque();
        try {
            File file = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("Borrowed_by");

            for (int s = 0; s < nodeLst.getLength(); s++) {

                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element sectionNode = (Element) fstNode;
                    NodeList memberIdElementList = sectionNode.getElementsByTagName("MemberID");
                    Element memberIdElmnt = (Element) memberIdElementList.item(0);
                    NodeList memberIdNodeList = memberIdElmnt.getChildNodes();
                    final int memNum;
                    try {
                        memNum = Integer.parseInt(((Node) memberIdNodeList.item(0)).getNodeValue().trim());
                    } catch (final NumberFormatException nfe) {
                        continue;
                    }

                    NodeList secnoElementList = sectionNode.getElementsByTagName("ISBN");
                    Element secnoElmnt = (Element) secnoElementList.item(0);
                    NodeList secno = secnoElmnt.getChildNodes();

                    NodeList codateElementList = sectionNode.getElementsByTagName("Checkout_date");
                    Element codElmnt = (Element) codateElementList.item(0);
                    NodeList cod = codElmnt.getChildNodes();

                    NodeList cidateElementList = sectionNode.getElementsByTagName("Checkin_date");
                    Element cidElmnt = (Element) cidateElementList.item(0);
                    NodeList cid = cidElmnt.getChildNodes();

                    parsedDataQueue.add(new TransactionRecord(memNum,
                            ((Node) secno.item(0)).getNodeValue().trim(),
                            ((Node) cod.item(0)).getNodeValue().trim(),
                            ((Node) cid.item(0)).getNodeValue().trim()));
                }

            }
        } catch (final Exception e) {
            System.out.println("Unable to parse the XML " + fileName  +"... exiting ");
            e.printStackTrace();
            System.exit(-1);
        }
        return parsedDataQueue;
    }
}
