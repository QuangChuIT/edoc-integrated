//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.10 at 08:40:26 AM ICT 
//


package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.bkav.edoc.service.entity.soapenv.Body;
import com.bkav.edoc.service.entity.soapenv.Envelope;
import com.bkav.edoc.service.entity.soapenv.Header;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the Bkav.eDoc.Service.Entity package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Envelope_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: Bkav.eDoc.Service.Entity
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link From }
     */
    public From createFrom() {
        return new From();
    }

    /**
     * Create an instance of {@link To }
     */
    public To createTo() {
        return new To();
    }

    /**
     * Create an instance of {@link OtherInfo }
     */
    public OtherInfo createOtherInfo() {
        return new OtherInfo();
    }

    /**
     * Create an instance of {@link Author }
     */
    public Author createAuthor() {
        return new Author();
    }

    /**
     * Create an instance of {@link MessageHeader }
     */
    public MessageHeader createMessageHeader() {
        return new MessageHeader();
    }

    /**
     * Create an instance of {@link Header }
     */
    public Header createHeader() {
        return new Header();
    }

    /**
     * Create an instance of {@link Code }
     */
    public Code createCode() {
        return new Code();
    }

    /**
     * Create an instance of {@link ToPlaces }
     */
    public ToPlaces createToPlaces() {
        return new ToPlaces();
    }

    /**
     * Create an instance of {@link Manifest }
     */
    public Manifest createManifest() {
        return new Manifest();
    }

    /**
     * Create an instance of {@link Attachment }
     */
    public Attachment createAttachment() {
        return new Attachment();
    }

    /**
     * Create an instance of {@link Body }
     */
    public Body createBody() {
        return new Body();
    }

    /**
     * Create an instance of {@link Appendixes }
     */
    public Appendixes createAppendixes() {
        return new Appendixes();
    }

    /**
     * Create an instance of {@link Envelope }
     */
    public Envelope createEnvelope() {
        return new Envelope();
    }

    /**
     * Create an instance of {@link Reference }
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link PromulgationInfo }
     */
    public PromulgationInfo createPromulgationInfo() {
        return new PromulgationInfo();
    }

    /**
     * Create an instance of {@link DocumentType }
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Envelope }{@code >}}
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Envelope")
    public JAXBElement<Envelope> createEnvelope(Envelope value) {
        return new JAXBElement<Envelope>(_Envelope_QNAME, Envelope.class, null, value);
    }

}
