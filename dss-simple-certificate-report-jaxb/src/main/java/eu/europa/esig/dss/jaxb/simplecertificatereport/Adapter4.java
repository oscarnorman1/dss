//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.22 at 02:33:08 PM CET 
//


package eu.europa.esig.dss.jaxb.simplecertificatereport;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;

public class Adapter4
    extends XmlAdapter<String, SubIndication>
{


    public SubIndication unmarshal(String value) {
        return (eu.europa.esig.dss.jaxb.parsers.SubIndicationParser.parse(value));
    }

    public String marshal(SubIndication value) {
        return (eu.europa.esig.dss.jaxb.parsers.SubIndicationParser.print(value));
    }

}
