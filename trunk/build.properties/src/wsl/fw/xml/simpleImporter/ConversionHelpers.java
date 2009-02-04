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
 * Collection of some simple conversion and fallback methods for convenience.
 *
 * @author <a href="mailto:oliver@zeigermann.de">Olli Z.</a>
 */
public class ConversionHelpers {

    /** Returns <code>value</code> if not null, otherwise <code>fallBack</code>.
     */
    public static String getString(String value, String fallBack) {
        if (value == null)
            return fallBack;
        return value;
    }

    /** Gets int value from a string value. 
     * @param value string value to get int from
     * @return int representation of value or <code>-1</code> 
     * if it can not be converted to an int
     */
    public static int getInt(String value) {
        return getInt(value, -1);
    }

    /** Gets int value from a string value. 
     * @param value string value to get int from
     * @param fallBack fall back value
     * @return int representation of value or <code>fallBack</code> 
     * if it can not be converted to an int
     */
    public static int getInt(String value, int fallBack) {
        if (value == null)
            return fallBack;
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException nfe) {
            return fallBack;
        }
    }

    /** Gets long value from a string value. 
     * @param value string value to get long from
     * @return long representation of value or <code>-1L</code> 
     * if it can not be converted to a long
     */
    public static long getLong(String value) {
        return getLong(value, -1L);
    }

    /** Gets long value from a string value. 
     * @param value string value to get long from
     * @param fallBack fall back value
     * @return long representation of value or <code>fallBack</code> 
     * if it can not be converted to a long
     */
    public static long getLong(String value, long fallBack) {
        if (value == null)
            return fallBack;
        try {
            return Long.valueOf(value).longValue();
        } catch (NumberFormatException nfe) {
            return fallBack;
        }
    }

    /** Gets boolean value a string value.
     * @param value string value to get boolean from
     * @param fallBack fall back value
     * @return boolean representation of value <code>fallBack</code> 
     * if it can not <em>properly</em> be converted to a boolean
     */
    public static boolean getBoolean(String value, boolean fallBack) {
        if (value == null)
            return fallBack;
        // do not use "Boolean.valueOf(" as this returns false on everything
        // but "true"
        if ("true".equals(value))
            return true;
        if ("false".equals(value))
            return false;
        return fallBack;
    }
}
