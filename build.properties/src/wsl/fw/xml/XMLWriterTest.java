/* XML Im-/Exporter: Copyright 2002-2004, Oliver Zeigermann (oliver@zeigermann.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of
 *   conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list
 *   of conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * - Neither the name of the Oliver Zeigermann nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, ORTORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */ 
package wsl.fw.xml;

import java.io.*;

import junit.framework.*;

import org.xml.sax.helpers.AttributesImpl;

/**
 * Test cases for {@link XMLWriter}, {@link XMLOutputStreamWriter},
 * and {@link XMLEncode}.
 *
 * @author olli
 */
public class XMLWriterTest extends TestCase {

    private XMLWriter xmlWriter;
    private StringWriter stringWriter;

    public XMLWriterTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(XMLWriterTest.class);

        return suite;
    }

    /** Test of createEndTag method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testCreateEndTag() {
        System.out.println("testCreateEndTag");

        String xml = XMLWriter.createEndTag("root");
        String expected = "</root>";
        assertEquals(expected, xml);
    }

    /** Test of createStartTag method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testCreateStartTag() {
        System.out.println("testCreateStartTag");

        String xml =
            XMLWriter.createStartTag("root", new String[] { "at1", "at2" }, new String[] { "v1", "v2" }, false);
        String expected = "<root at1=\"v1\" at2=\"v2\">";
        assertEquals(expected, xml);

    }

    /** Test of createStartTag method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testCreateStartTag2() {
        System.out.println("testCreateStartTag2");

        String xml = XMLWriter.createStartTag("root", new String[][] { { "at1", "v1" }, {
                "at2", "v2\"'" }, {
                "at3", null }
        }, false, true, '\'');
        String expected = "<root at1='v1' at2='v2&quot;&apos;'>";
        assertEquals(expected, xml);

    }

    /** Test of createStartTag method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testCreateEmptyStartTag() {
        System.out.println("testCreateEmptyStartTag");

        String xml = XMLWriter.createStartTag("root", "at1", "v1\"'<no-tag>", true);
        String expected = "<root at1=\"v1&quot;&apos;&lt;no-tag&gt;\"/>";
        assertEquals(expected, xml);
    }

    /** Test of writeProlog method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testWriteProlog() {
        System.out.println("testWriteProlog");

        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            xmlWriter.writeXMLDeclaration();
            String xml = stringWriter.toString();
            String expected = "<?xml version=\"1.0\"?>\n";
            assertEquals(expected, xml);
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    /** Test of writeComment method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testWriteComment() {
        System.out.println("testWriteComment");

        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            xmlWriter.writeComment("comment");
            String xml = stringWriter.toString();
            String expected = "<!-- comment -->";
            assertEquals(expected, xml);
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    /** Test of writePI method, of class de.zeigermann.xml.XMLOutputStreamWriter. */
    public void testWritePI() {
        System.out.println("testWritePI");

        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            xmlWriter.writePI("target", "data");
            String xml = stringWriter.toString();
            String expected = "<?target data?>";
            assertEquals(expected, xml);
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    // as start/end tag writing can hardly be checked isolated do this here 
    // generating a whole file
    public void testMain() {
        System.out.println("MAIN TEST");
        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            xmlWriter.writeProlog("<?xml version='1.0' encoding='UTF-8' ?>");
            xmlWriter.writeProlog("<!DOCTYPE log SYSTEM '../share/log.dtd'>");
            xmlWriter.writeStartTag("<next>");
            String startTag =
                XMLOutputStreamWriter.createStartTag(
                    "root",
                    new String[] { "at1", "at2" },
                    new String[] { "v1", "v2" },
                    false);
            String endTag = XMLOutputStreamWriter.createEndTag("root");
            String cData = "<kein-tag>";
            xmlWriter.writeElementWithCData(startTag, cData, endTag);
            String emptyStartTag =
                XMLOutputStreamWriter.createStartTag(
                    "root",
                    new String[] { "at1", "at2" },
                    new String[] { "v1", "v2" },
                    true);
            xmlWriter.writeEmptyElement(emptyStartTag);
            xmlWriter.writeStartTag("<next1>");
            xmlWriter.writeStartTag("<next2>");
            xmlWriter.writeStartTag("<next3>", false);
            xmlWriter.writeCData("This is long <![CDATA[CDATA that can be encoded as CDATA block");
            xmlWriter.writeEndTag("</next3>");
            xmlWriter.writeStartTag("<next3>", false);
            xmlWriter.writeCData("This is long <![CDATA[CDATA]]> that can not be encoded as CDATA block");
            xmlWriter.writeEmptyElement(emptyStartTag);
            xmlWriter.writeEndTag("</next3>");
            xmlWriter.writeEndTag("</next2>");
            xmlWriter.writeEndTag("</next1>");
            xmlWriter.writeEndTag("</next>");

            String xml = stringWriter.toString();
            String expected =
                "<?xml version='1.0' encoding='UTF-8' ?>\n<!DOCTYPE log SYSTEM '../share/log.dtd'>\n<next>\n  <root at1=\"v1\" at2=\"v2\">&lt;kein-tag&gt;</root>\n  <root at1=\"v1\" at2=\"v2\"/>\n  <next1>\n    <next2>\n      <next3><![CDATA[This is long <![CDATA[CDATA that can be encoded as CDATA block]]></next3>\n      <next3>This is long &lt;![CDATA[CDATA]]&gt; that can not be encoded as CDATA block<root at1=\"v1\" at2=\"v2\"/>\n      </next3>\n    </next2>\n  </next1>\n</next>\n";
            System.out.println(xml);
            if (!xml.equals(expected)) {
                fail("\nWrong output:\n" + xml + "\nShould be:\n" + expected + "\n");
            }
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    // test XMLEncode
    public void testDecode() {
        System.out.println("XMLEncode.xmlDecodeTextToCDATA");

        String xml = XMLEncode.xmlDecodeTextToCDATA("<root at1=\"v1&quot;&apos;&lt;&amp;&gt;no-tag>\"/>");
        String expected = "<root at1=\"v1\"'<&>no-tag>\"/>";
        assertEquals(expected, xml);
    }

    public void testDetail() {
        System.out.println("DETAIL TEST");
        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            xmlWriter.setPrettyPrintMode(false);
            xmlWriter.writeStartTag("<next>");
            String emptyStartTag =
                XMLOutputStreamWriter.createStartTag(
                    "root",
                    new String[] { "at1", "at2" },
                    new String[] { "v1", "v2" },
                    true);
            xmlWriter.writeEmptyElement(emptyStartTag);
            xmlWriter.writeStartTag("<next1>");
            xmlWriter.writeEndTag("</next1>");
            xmlWriter.writeEndTag("</next>");

            String xml = stringWriter.toString();
            String expected = "<next\n><root at1=\"v1\" at2=\"v2\"\n/><next1\n></next1\n></next\n>";
            System.out.println(xml);
            if (!xml.equals(expected)) {
                fail("\nWrong output:\n" + xml + "\nShould be:\n" + expected + "\n");
            }
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    public void testExtendedConvenience() {
        System.out.println("EXTENDED CONVENIENCE TEST");
        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            XMLOutputStreamWriter
                .generateAndWriteElementWithCData(xmlWriter, "root", new String[][] { { "at1", "v1" }, {
                    "at2", "v2" }
            }, "<cdata>");
            String xml = stringWriter.toString();
            String expected = "<root at1=\"v1\" at2=\"v2\">&lt;cdata&gt;</root>\n";
            System.out.println(xml);
            if (!xml.equals(expected)) {
                fail("\nWrong output:\n" + xml + "\nShould be:\n" + expected + "\n");
            }
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    public void testUmlaute() {
        System.out.println("CHECKING ISO-8859-1 german umlaute");
        try {
            stringWriter = new StringWriter();
            xmlWriter = new XMLWriter(stringWriter);
            xmlWriter.writeXMLDeclaration();
            xmlWriter.writeStartTag("<root>", XMLOutputStreamWriter.NO_NEWLINE);
            xmlWriter.writePCData("text öäü ÄÖÜ ß");
            xmlWriter.writeEndTag("</root>");
            String xml = stringWriter.toString();
            String expected = "<?xml version=\"1.0\"?>\n<root>text öäü ÄÖÜ ß</root>\n";
            System.out.println(xml);
            if (!xml.equals(expected)) {
                fail("\nWrong output:\n'" + xml + "'\nShould be:\n'" + expected + "'\n");
            }
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }

    }

    public void testEncoding() {
        System.out.println("CHECKING TWO BYTE ENCODING");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLOutputStreamWriter xmlWriter = new XMLOutputStreamWriter(out, XMLOutputStreamWriter.ENCODING_UTF_16);
            // this must not be encoded to UTF-16 - CHECK!
            xmlWriter.writeXMLDeclaration();
            xmlWriter.writeStartTag("<r>", XMLOutputStreamWriter.NO_NEWLINE);
            xmlWriter.writePCData("t öäü ß");
            xmlWriter.writeEndTag("</r>");
            xmlWriter.flush();
            out.flush();
            // this actually can not be encoded into string as there is a mixture
            // of two encodings...
            //            String xml = out.toString(XMLOutputStreamWriter.ENCODING_UTF_16);
            //            System.out.println("XML: "+xml);
            byte[] outBytes = out.toByteArray();
            // the following is simple to understand: 
            // - first there is the literal, not encoded plain ascii (as required by XML spec) XML declaration followed by a newline (10 or 0xa)
            // - then follows a sequence specifying big endian order for UTF-16 (-1 or 0xFF), -2 or 0xFE)
            // - the rest are just normal ascii bytes preceeded by 0x0 as this is the UTF-16 encoding for ascii
            // - finally there is a newline encoded as UTF-16 which is just 0 followed by 10 or 0xa
            byte[] expectedBytes = {
                // xml delcaration in plain ascii
                '<',
                    '?',
                    'x',
                    'm',
                    'l',
                    ' ',
                    'v',
                    'e',
                    'r',
                    's',
                    'i',
                    'o',
                    'n',
                    '=',
                    '"',
                    '1',
                    '.',
                    '0',
                    '"',
                    ' ',
                    'e',
                    'n',
                    'c',
                    'o',
                    'd',
                    'i',
                    'n',
                    'g',
                    '=',
                    '"',
                    'U',
                    'T',
                    'F',
                    '-',
                    '1',
                    '6',
                    '"',
                    '?',
                    '>',
                // plain ascii newline
                10,
                // UTF-16 start big endian order
                -2, -1,
                // <r>t in UTF-16 
                0, '<', 0, 'r', 0, '>', 0, 't', 0, ' ',
                // german umlaute encoded as UTF-16
                //                0, 'ö' (F6=246,246-256=-10), 0, 'ä' (E4=228, 228-256=-28), 0, 'ü' (FC=252, 252-256=-4), 0, ' ', 0, 'ß' (DF=223, 223-256=33),
                0, -10, 0, -28, 0, -4, 0, ' ', 0, -33,
                // <r> in UTF-16
                0, '<', 0, '/', 0, 'r', 0, '>',
                // UTF-16 newline
                0, 10 };
            assertEquals(outBytes.length, expectedBytes.length);

            // check all bytes
            for (int i = 0; i < outBytes.length; i++) {
                System.out.print(outBytes[i] + ", ");
                if (outBytes[i] != expectedBytes[i]) {
                    fail("\nWrong encoding:\n" + outBytes[i] + "\nShould be:\n" + expectedBytes[i] + "\n");
                }
            }
            System.out.println("DONE");
        } catch (IOException ioe) {
            System.out.println("EXCEPTION");
            fail("Exception: " + ioe);
        }
    }

    public void testSAXAttributes() {
        System.out.println("CHECKING SAX Attributes");
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "at1", "CDATA", "v1\"'<no-tag>");
        attributes.addAttribute("", "", "at2", "CDATA", "v2");

        String xml = XMLWriter.createStartTag("root", attributes, true);
        String expected = "<root at1=\"v1&quot;&apos;&lt;no-tag&gt;\" at2=\"v2\"/>";
        assertEquals(expected, xml);
    }
}
