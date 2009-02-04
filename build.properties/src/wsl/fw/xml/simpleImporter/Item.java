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

/**
 * Represntation of a path element. 
 *
 * @author <a href="mailto:oliver@zeigermann.de">Olli Z.</a>
 */
public final class Item {

    public static final Item ITEM_ANY = null;
    
    private final String namespaceURI;
    private final String name;

    public Item() {
        this(null, null);
    }
    public Item(String name) {
        this(name, null);
    }

    public Item(String name, String namespaceURI) {
        this.namespaceURI = namespaceURI;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        Item token = (Item) o;
        return (
            (token.name == null || this.name.equals(token.name))
                && (this.namespaceURI == null
                    || token.namespaceURI == null
                    || this.namespaceURI.equals(token.namespaceURI)));

    }

    public String toString() {
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return name;
        } else {
            return namespaceURI + ":" +name;
        }
    }
}