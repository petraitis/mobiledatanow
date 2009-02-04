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

import java.io.IOException;
import java.io.StringWriter;

/**
 * This is a convenience class for writing XML to a string. As
 * no IOExceptions will occur this class catches them for you
 * doing nothing. Call {@link #toString} to finally get your string.
 * As constructor for {@link XMLWriter} already needs writer call
 * {@link #create} to get your objects instead of consructor.
 *
 * @author <a href="mailto:oliver@zeigermann.de">Olli Z.</a>
 */
public class XMLStringWriter extends XMLWriter {

    /** Creates a new <code>XMLStringWriter</code> objects. */
    public static XMLStringWriter create() {
        return new XMLStringWriter(new StringWriter());
    }

    private StringWriter sw;

    private XMLStringWriter(StringWriter sw) {
        super(sw);
        this.sw = sw;
    }

    /** Gets the string representation of your written XML. */
    public String toString() {
        try {
            flush();
        } catch (IOException ioe) {
            // won't happen...
        }
        sw.flush();
        return sw.toString();
    }

    public void writeXMLDeclaration() {
        try {
            super.writeXMLDeclaration();
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeProlog(String prolog) {
        try {
            super.writeProlog(prolog);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeNl() {
        try {
            super.writeNl();
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeComment(String comment) {
        try {
            super.writeComment(comment);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writePI(String target, String data) {
        try {
            super.writePI(target, data);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeStartTag(String startTag, boolean nl) {
        try {
            super.writeStartTag(startTag, nl);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeStartTag(String startTag) {
        try {
            super.writeStartTag(startTag);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeEndTag(String endTag, boolean nl) {
        try {
            super.writeEndTag(endTag, nl);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeEndTag(String endTag) {
        try {
            super.writeEndTag(endTag);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeEmptyElement(String emptyTag, boolean nl) {
        try {
            super.writeEmptyElement(emptyTag, nl);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeEmptyElement(String emptyTag) {
        try {
            super.writeEmptyElement(emptyTag);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeCData(String cData) {
        try {
            super.writeCData(cData);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writePCData(String pcData) {
        try {
            super.writePCData(pcData);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeElementWithCData(String startTag, String cData, String endTag) {
        try {
            super.writeElementWithCData(startTag, cData, endTag);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

    public void writeElementWithPCData(String startTag, String pcData, String endTag) {
        try {
            super.writeElementWithPCData(startTag, pcData, endTag);
        } catch (IOException ioe) {
            // won't happen...
        }
    }

}
