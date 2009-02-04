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
package wsl.fw.xml.simpleImporter;

import wsl.fw.xml.*;

import java.io.*;
import java.util.*;

import junit.framework.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.InputSource;

/**
 * Try out importer a little bit. 
 *
 * @author <a href="mailto:oliver@zeigermann.de">Olli Z.</a>
 */
public class SimpleImportTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(SimpleImportTest.class);
        return suite;
    }

    public SimpleImportTest(String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void testMain() {
        System.out.println("MAIN TEST");
        String testString =
            "<?xml version='1.0' encoding='ISO-8859-1'?><response state='ok'><mixed>Olli <b>ist</b> toll&lt;</mixed><text>Text&lt;<![CDATA[<huhu>Dies ist aller CDATA</huhu>]]></text></response>\n";
        System.out.println("TEST STRING:");
        System.out.println(testString);
        System.out.println("");
        SimpleImporter dumpImporter = new SimpleImporter();
        DumpTester dumpTester = new DumpTester();
        try {
            InputStream in = new ByteArrayInputStream(testString.getBytes("ISO-8859-1"));
            dumpImporter.addSimpleImportHandler(dumpTester);
            dumpImporter.setIncludeLeadingCDataIntoStartElementCallback(true);
            dumpImporter.setFullDebugMode(true);
            dumpImporter.setUseQName(false);
            dumpImporter.parse(new InputSource(in));

            String fullDebug = dumpImporter.getParsedStreamForDebug();
            System.out.println("FULL DEBUG START");
            System.out.println(fullDebug);
            System.out.println("FULL DEBUG END");

            assertEquals(testString, fullDebug);

            System.out.println(dumpImporter.getFoundMixedPCData() ? "MIXED" : "NOT MIXED");

            // checks for maximum chunk go here as well (no two text() callbacks may follow each other)
            String expectedDump =
                "DOCUMENT START\n/response:\n<response state=\"ok\">\n/response/mixed:\n<mixed>\nOlli\n/response/mixed/b:\n<b>\nist\n/response/mixed/b:\n</b>\n/response/mixed/text():\ntoll&lt;\n/response/mixed:\n</mixed>\n/response/text:\n<text>\n<![CDATA[Text<<huhu>Dies ist aller CDATA</huhu>]]>\n/response/text:\n</text>\n/response:\n</response>\nDOCUMENT END\n";
            String actualDump = dumpTester.logBuffer.toString();

            System.out.println("COLLECTED DUMP START");
            System.out.println(actualDump);
            System.out.println("COLLECTED DUMP END");

            if (!actualDump.equals(expectedDump)) {
                fail("\nWrong output:\n'" + actualDump + "'\nShould be:\n'" + expectedDump + "'\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error: " + e);
        }
    }

    public void testDetail() {
        System.out.println("DETAIL TEST");

        String testString =
            "<?xml version='1.0' encoding='ISO-8859-1'?><response state='ok' boolean='true' int='-100' long='4676767676' ns:olli='cool'><mixed>Olli <b>ist</b> toll&lt;</mixed><text>Text&lt;<![CDATA[<huhu>Dies ist aller CDATA</huhu>]]></text></response>\n";
        System.out.println("TEST STRING:");
        System.out.println(testString);
        System.out.println("");
        try {
            InputStream in = new ByteArrayInputStream(testString.getBytes("ISO-8859-1"));
            SimpleImporter dumpImporter = new SimpleImporter();
            DetailTester detailTester = new DetailTester();
            dumpImporter.addSimpleImportHandler(detailTester);
            dumpImporter.setIncludeLeadingCDataIntoStartElementCallback(true);
            dumpImporter.parse(new InputSource(in));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error: " + e);
        }
    }

    public void testPathList() {
        System.out.println("PATH LIST TEST");
        String testString =
            "<?xml version='1.0' encoding='ISO-8859-1'?>"
                + "<root xmlns:olli=\"http://www.zeigermann.de\" xmlns:daniel=\"http://www.floreysoft.de\"><sub>"
                + "<olli:element>\n"
                + "<daniel:element>Huhu</daniel:element>\n"
                + "</olli:element>"
                + "</sub></root>";
        System.out.println("TEST STRING:");
        System.out.println(testString);
        System.out.println("");

        try {
            InputStream in = new ByteArrayInputStream(testString.getBytes("ISO-8859-1"));
            SimpleImporter testImporter = new SimpleImporter();
            PathListTester tester = new PathListTester();
            testImporter.addSimpleImportHandler(tester);
            testImporter.setBuildComplexPath(true);
            testImporter.setUseQName(false);
            testImporter.setIncludeLeadingCDataIntoStartElementCallback(false);
            testImporter.parse(new InputSource(in));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error: " + e);
        }
    }

    private final static class PathListTester extends DefaultSimpleImportHandler {

        public void startElement(SimplePath path, String name, AttributesImpl attributes, String leadingCDdata) {

            boolean matchesRoot = path.matches(new Item("root")) && path.matches(Item.ITEM_ANY);
            boolean matchesOlli =
                path.matches(new Item("element")) && path.matches(new Item("element", "http://www.zeigermann.de"));
            boolean matchesDaniel =
                path.matches(
                    new Item[] {
                        new Item("root"),
                        Item.ITEM_ANY,
                        Item.ITEM_ANY,
                        new Item("element", "http://www.floreysoft.de")},
                    false);
            boolean matchesSub = path.matches(new Item("sub"));

            boolean matchesFromStart = path.matchesFromRoot(new Item[] { new Item("root")});

            if (matchesFromStart && !matchesRoot && !matchesOlli && !matchesDaniel && !matchesSub) {
                fail("Item matching does not work");
            }
        }

        public void cData(SimplePath path, String cdata) {
            if ("Huhu".equals(cdata)) {
                if (!path
                    .matches(
                        new Item[] {
                            new Item("root"),
                            new Item("sub"),
                            new Item("element"),
                            new Item("element", "http://www.floreysoft.de")})) {
                    fail("CDATA is in wrong path");
                }
            }
        }

    }

    private final static class DumpTester implements SimpleImportHandler {

        public StringBuffer logBuffer = new StringBuffer();
        public boolean previousCallbackWasCDATA = true;

        public void startDocument() {
            previousCallbackWasCDATA = false;
            log("DOCUMENT START");
        }

        public void endDocument() {
            previousCallbackWasCDATA = false;
            log("DOCUMENT END");
        }

        public void cData(SimplePath path, String cdata) {
            if (previousCallbackWasCDATA)
                fail("No two cData callbacks may follow each other, as this violates the maximum chunk guarantee given in API spec");
            previousCallbackWasCDATA = true;

            log(path.toString() + "/text():");
            String encodedText = XMLEncode.xmlEncodeText(cdata);
            log(encodedText);
        }

        public void startElement(SimplePath path, String name, AttributesImpl attributes, String leadingCDdata) {
            previousCallbackWasCDATA = false;
            log(path.toString() + ":");
            String startTag = XMLWriter.createStartTag(name, attributes);
            log(startTag);
            if (leadingCDdata != null) {
                String encodedText = XMLEncode.xmlEncodeText(leadingCDdata);
                log(encodedText);
            }
        }

        public void endElement(SimplePath path, String name) {
            previousCallbackWasCDATA = false;
            log(path.toString() + ":");
            log("</" + name + ">");
        }

        private void log(String text) {
            logBuffer.append(text).append('\n');
        }
    }

    private final class DetailTester extends DefaultSimpleImportHandler {

        public void startElement(SimplePath path, String name, AttributesImpl attributes, String leadingCDdata) {

            // checking path

            if (!path.matchsAny(new String[] { "response", "mixed", "text", "b" })) {
                fail("matchesAny(String[]) does not work");
            }

            List list = new ArrayList();
            list.add(new SimplePath("response"));
            list.add(new SimplePath("mixed"));
            list.add(new SimplePath("text"));
            list.add(new SimplePath("b"));
            if (!path.matchsAny(list)) {
                fail("matchesAny(Collection) does not work");
            }

            if (!path
                .matchsAny(new String[] { "/response", "/response/mixed", "/response/text", "/response/mixed/b" })) {
                fail("absolute paths do not seem to work");
            }

            if (!path.matchsAny(new String[] { "response", "response/mixed", "response/text", "mixed/b" })) {
                fail("relative paths do not seem to work");
            }

            // checking attribute
            if (path.matches("/response")) {
                String state = ConversionHelpers.getString(attributes.getValue("state"), "error");
                boolean b = ConversionHelpers.getBoolean(attributes.getValue("boolean"), false);
                int i = ConversionHelpers.getInt(attributes.getValue("int"), 4711);
                long l = ConversionHelpers.getLong(attributes.getValue("long"), 1L);
                assertEquals(state, "ok");
                assertEquals(b, true);
                assertEquals(i, -100);
                assertEquals(l, 4676767676L);

                // this is actually not there
                String notThere = ConversionHelpers.getString(attributes.getValue("notThere"), "is not there");
                assertEquals(notThere, "is not there");

                assertEquals(attributes.getLength(), 5);
                assertEquals(attributes.getValue(0), "ok");
                assertEquals(attributes.getType("ns:olli"), "CDATA");
                // these should be enough...
            }
        }
    }

}
