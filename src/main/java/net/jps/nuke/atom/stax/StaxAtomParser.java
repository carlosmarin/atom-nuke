package net.jps.nuke.atom.stax;

import java.io.InputStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLInputFactory2;

/**
 *
 * @author zinic
 */
public class StaxAtomParser {

   private final XMLInputFactory xmlif;

   public StaxAtomParser() throws FactoryConfigurationError {
      this(XMLInputFactory2.newInstance());
   }

   public StaxAtomParser(XMLInputFactory inputFactory) {
      xmlif = inputFactory;

      xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
      xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
      xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
   }

   public void parse(InputStream source) throws XMLStreamException {
      final XMLStreamReader reader = xmlif.createXMLStreamReader(source);

      while (reader.hasNext()) {
         switch (reader.next()) {
            case XMLEvent.START_DOCUMENT:
               documentStart(reader);
               break;

            case XMLEvent.END_DOCUMENT:
               documentEnd(reader);
               break;
               
            case XMLEvent.START_ELEMENT:
               elementOpen(reader);
               break;
               
            case XMLEvent.END_ELEMENT:
               elementClosed(reader);
               break;

            case XMLEvent.ATTRIBUTE:
               attribute(reader);
               break;

            case XMLEvent.CDATA:
               cdata(reader);
               break;

            case XMLEvent.CHARACTERS:
               characters(reader);
               break;

            case XMLEvent.NAMESPACE:
               namespace(reader);
               break;

            case XMLEvent.COMMENT:
            case XMLEvent.ENTITY_DECLARATION:
            case XMLEvent.ENTITY_REFERENCE:
            case XMLEvent.DTD:
            case XMLEvent.NOTATION_DECLARATION:
            case XMLEvent.SPACE:
            default:
               // Ignore this event
               break;
         }
      }
   }

   private void documentStart(XMLStreamReader reader) {
   }

   private void documentEnd(XMLStreamReader reader) {
   }

   private void namespace(XMLStreamReader reader) {
   }

   private void cdata(XMLStreamReader reader) {
   }

   private void characters(XMLStreamReader reader) {
   }

   private void attribute(XMLStreamReader reader) {
   }
   
   private void elementOpen(XMLStreamReader reader) {
      final String elementName = reader.getLocalName();
   }
   
   private void elementClosed(XMLStreamReader reader) {
      final String elementName = reader.getLocalName();
   }

}
