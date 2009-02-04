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

import java.util.Collection;
import java.util.Iterator;

/**
 * <b>Rudimentary</b> representation of a path to an XML element. 
 * <br>
 * Two paths match in two cases:
 * <ol><li>If they are really equal in terms of the {@link #equals} method.
 * <li>If the path to match to is relative, i.e. it has no leading '/' and it is the suffix of the matching path.
 * </ol>
 * <br>
 * For example<br>
 * <code>/root/tag</code> matches <code>/root/tag</code> and<br>
 * <code>/root/tag</code> matches <code>tag</code>.
 *
 * @author <a href="mailto:oliver@zeigermann.de">Olli Z.</a>
 */
public class SimplePath {

    protected final String path;
    protected final Item[] pathList;

    /** Strips off ending slash from a string if there is one. */
    public final static String stripEndingSlash(String path) {
        if (path != null && path.length() > 0 && path.charAt(path.length() - 1) == '/') {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    /** Creates a path object from a string describing it. The describing
     * string uses '/' characters to seperate the paths parts.
     */
    public SimplePath(String path) {
        this(path, null);
    }

    /** Creates a path object from a string describing it. The describing
     * string uses '/' characters to seperate the paths parts.
     */
    public SimplePath(String path, Item[] pathList) {
        this.path = stripEndingSlash(path);
        this.pathList = pathList;
    }

    /** Copy ctor. */
    public SimplePath(SimplePath path) {
        this.path = stripEndingSlash(path.toString());
        this.pathList = new Item[path.pathList.length];
        System.arraycopy(path.pathList, 0, this.pathList, 0, path.pathList.length);
    }

    /**
     * Checks if an item matches the last segment of this path.
     */
    public boolean matches(Item name) {
        return (pathList != null && pathList.length > 0 && pathList[pathList.length - 1].equals(name));
    }

    /**
     * Checks if the given array of items matches this path.
     */
    public boolean matches(Item[] path, boolean isRelative) {
        if (pathList == null
            || path == null
            || path.length > pathList.length
            || (!isRelative && path.length != pathList.length)) {
            return false;
        } else {
            for (int i = path.length - 1; i >= 0; i--) {
                if (!pathList[i].equals(path[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Checks if the given array of items matches this path from the root. The given path is to be considered relative.
     * Useful to distinguish between something like /rootPath/valid/*\/valid and /rootPath/invalid/*\/valid. You will need two
     * matches for this:
     * <pre>
     * matchesFromRoot(new Item[] { new Item("rootPath"), new Item("valid")}) 
     * &&
     * matches(new Item("valid"))
     * </pre>
     */
    public boolean matchesFromRoot(Item[] path) {
        if (pathList == null || path == null || path.length > pathList.length) {
            return false;
        } else {
            for (int i = 0; i < path.length; i++) {
                if (!pathList[i].equals(path[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Checks if the given array of items matches this path. The given path is to be considered relative.
     */
    public boolean matches(Item[] path) {
        return matches(path, true);
    }

    /** Finds out if the the given path matches this one. 
     */
    public boolean matches(SimplePath matchPath) {
        return matches(matchPath.toString());
    }

    /** Finds out if the path represented by the given string matches this one. 
     * @see #matches(SimplePath)
    */
    public boolean matches(String matchPath) {
        String matchString = stripEndingSlash(matchPath);

        if (matchString != null && matchString.length() > 0 && matchString.charAt(0) != '/') {
            // relative
            return path.endsWith(matchString);
        } else {
            // absolute
            return path.equals(matchString);
        }
    }

    /** Checks if this path matches any of the paths stored in
     * <code>paths</code> collection. This means we iterate through 
     * <code>paths</code> and match every entry to this path.
     */
    public boolean matchsAny(Collection paths) {
        for (Iterator it = paths.iterator(); it.hasNext();) {
            SimplePath matchPath = (SimplePath) it.next();
            if (matches(matchPath))
                return true;
        }
        return false;
    }

    /** Checks if this path matches any of the paths stored in
     * <code>paths</code> collection. This means we iterate through 
     * <code>paths</code> and match every entry to this path.
     */
    public boolean matchsAny(String[] paths) {
        for (int i = 0; i < paths.length; i++) {
            if (matches(paths[i]))
                return true;
        }
        return false;
    }

    public String toString() {
        return path;
    }

    public boolean equals(Object o) {
        if (o instanceof String) {
            return path.equals(o);
        } else if (o instanceof SimplePath) {
            return path.equals(((SimplePath) o).toString());
        } else {
            return false;
        }
    }

}
