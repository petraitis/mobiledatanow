/**	$Id: SymbolSubst.java,v 1.1.1.1 2002/06/12 23:43:20 jonc Exp $
 *
 * Reads a String or text file and processes it to substitute symbols.
 *
 */
package wsl.tools;

import java.util.Properties;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class SymbolSubst
{
    public final static char   DELIM          = '^';
    public final static String CMD_SYMBOLDEF  = "-sd:";
    public final static String CMD_SYMBOLFILE = "-sf:";
    public final static String CMD_NODELIM    = "-nodelim";
    public final static String CMD_UNMATCHED  = "-unmatched";

    private Properties _symbolSet;
    private boolean    _useDelimiters;
    private boolean    _allowUnmatchedSymbols;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param symbolSet, a Properties object holding the symbols and their
     *   substitution values.
     * @param useDelimiters, if true symbols must be wrapped in delimiters
     *   (^symbolname^, use the ^^ escape to insert a literal ^).
     * @param allowUnmatchedSymbols, if true it is not an error for a file
     *   to contain symbols which have no matching definition. Meaningless
     *   if not using delimiters.
     */
    public SymbolSubst(Properties symbolSet, boolean useDelimiters,
        boolean allowUnmatchedSymbols)
    {
        _symbolSet             = symbolSet;
        _useDelimiters         = useDelimiters;
        _allowUnmatchedSymbols = allowUnmatchedSymbols;
    }

    //--------------------------------------------------------------------------
    /**
     * Perform symbol substitution.
     * @param line, the input string on which substitution is to be performed.
     * @return the modified string.
     */
    public String substitute(String line)
    {
        if (_useDelimiters)
            return delimSubstitute(line);
        else
            return nodelimSubstitute(line);
    }

    //--------------------------------------------------------------------------
    /**
     * Perform substitution reading in, writing to out.
     * @param in, the input stream.
     * @param out, the output stream.
     */
    public void substitute(InputStream in, OutputStream out)
        throws IOException
    {
        // open reader and writer
        BufferedReader in2  = new BufferedReader(new InputStreamReader(in));
        PrintWriter    out2 = new PrintWriter(new OutputStreamWriter(out));

        // iterate reading and processing lines
        String inLine;
        while ((inLine = in2.readLine()) != null)
            out2.println(substitute(inLine));

        // close reader and writer
        in2.close();
        out2.close();
    }

    //--------------------------------------------------------------------------
    /**
     * Perform symbol substitution using symbol delimiters.
     * @param line, the input string on which substitution is performed.
     * @return the modified string.
     */
    protected String delimSubstitute(String line)
    {
        StringBuffer outBuf = new StringBuffer();

        int  index = 0;
        int  len   = line.length();
        char c;
        char nextc;

        // ietrate getting chars
        while (index < len)
        {
            // get char
            c     = line.charAt(index);
            nextc = ((index + 1) < len) ? line.charAt(index + 1) : '\0';

            if (c == DELIM && nextc != DELIM)
            {
                // found symbol delimiter
                // find end delimiter
                int endIndex = line.indexOf(DELIM, index + 1);

                if (endIndex == -1)
                    throw new IllegalArgumentException("No terminating delimiter in : "
                        + line);

                // get the symbol and its subst value
                String symbol     = line.substring(index + 1, endIndex);
                String substValue = _symbolSet.getProperty(symbol);

                if (substValue == null)
                {
                    // no matching symbol definition
                    if (_allowUnmatchedSymbols)
                    {
                        // put in whole symbol and delim
                        outBuf.append(line.substring(index, endIndex + 1));
                        index = endIndex + 1;
                    }
                    else
                        throw new IllegalArgumentException("Unmatched symbol : "
                            + symbol);
                }
                else
                {
                    // got symbol, do subst
                    outBuf.append(substValue);
                    index = endIndex + 1;
                }
            }
            else if (c == DELIM && nextc == DELIM)
            {
                // found escaped delimiter, copy a single DELIM char
                outBuf.append(DELIM);
                index++;
                index++;
            }
            else
            {
                // normal character, copy and inc
                outBuf.append(c);
                index++;
            }
        }

        return outBuf.toString();
    }

    //--------------------------------------------------------------------------
    /**
     * Perform symbol substitution without using symbol delimiters.
     * @param line, the input string on which substitution is performed.
     * @return the modified string.
     */
    protected String nodelimSubstitute(String line)
    {
        // build symbol key set to test against
        String symbols[] = new String[_symbolSet.size()];
        Enumeration keyEnum = _symbolSet.keys();
        int index = 0;
        while (keyEnum.hasMoreElements())
            symbols[index++] = (String) keyEnum.nextElement();

        boolean      bFound;
        int          len = line.length();
        StringBuffer outBuf = new StringBuffer();

        // iterate testing for symbols
        index = 0;
        while (index < len)
        {
            bFound = false;
            // do test for symbol
            for (int i = 0; i < symbols.length; i++)
                if (line.startsWith(symbols[i], index))
                {
                    // found a symbol match
                    bFound = true;

                    // append to output
                    outBuf.append((String) _symbolSet.get(symbols[i]));

                    // advance index past symbol
                    index += symbols[i].length();
                }

            // if not found copy char and advance
            if (!bFound)
                outBuf.append(line.charAt(index++));
        }

        return outBuf.toString();
    }

    //--------------------------------------------------------------------------
    /**
     * Load symbols from a file.
     * @param inFilename, name of file containing symbols.
     * @param prop, the Properties object into which the symbols are loaded.
     */
    public static void loadSymbolFile(String inFilename, Properties prop)
        throws IOException
    {
        // open the stream
        InputStream in = new FileInputStream(inFilename);

        // load the symbols
        prop.load(in);

        // close stream
        in.close();
    }

    //--------------------------------------------------------------------------
    /**
     * Load symbols from command line args.
     * Individual args are parsed as a symbol definition if they have the
     * prefix -sd:
     * @param args, command line args.
     * @param prop, the Properties object into which the symbols are loaded.
     */
    public static void loadCmdLineSymbols(String args[], Properties prop)
    {
        // iterate args looking for the -sd: prefix
        for (int i = 0; i < args.length; i++)
            if (args[i].startsWith(CMD_SYMBOLDEF))
            {
                // found a symbol def, get the part after -sd:
                String str = args[i].substring(CMD_SYMBOLDEF.length());

                // parse out the name-value pair at =
                int index = str.indexOf('=');
                if (index == -1)
                    throw new IllegalArgumentException("Symbol definition must have = : "
                        + args[i]);
                String key   = str.substring(0, index);
                String value = str.substring(index + 1);
                prop.put(key, value);
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Display command line help.
     */
    public static void showHelp()
    {
        System.out.println();
        System.out.println("Symbol substitutor, syntax is:");
        System.out.println();
        System.out.println("java " + SymbolSubst.class.getName()
            + " <input file> <output file> [ " + CMD_NODELIM + " ]\n  [ "
            + CMD_UNMATCHED + " ] [ " + CMD_SYMBOLFILE + "<symbol file> ...] [ "
            + CMD_SYMBOLDEF + "<symbol_name>=<symbol_value>...]");
        System.out.println();
        System.out.println("<input file> is the file to be read and substituted."
            + "Symbols in the input file\n  delimited by " + DELIM + " are "
            + "replaced by the matching symbol value.");
        System.out.println("<output file> is where the substituted results are written.");
        System.out.println(CMD_NODELIM + " means symbols in the input file do "
            + "not need to be delimited by\n  a " + DELIM + " pair.");
        System.out.println(CMD_UNMATCHED + " means it is not an error for the "
            + "input file to contain\n  symbols for which there is no definition.");
        System.out.println("<symbol file> is a Properties file defining symbol "
            + "names and their\n  substitution values.");
        System.out.println("<symbol_name> and <symbol_value> define a symbol.");
        System.out.println();
        System.out.println("Delimiters and the " + CMD_UNMATCHED
            + " option have no meaning when using " + CMD_NODELIM + ".");
        System.out.println();
    }

    //--------------------------------------------------------------------------
    /**
     * Main entrypoint.
     */
    public static void main(String args[])
        throws IOException
    {
        if (args.length < 2)
        {
            showHelp();
            return;
        }

        Properties symbolSet = new Properties();

        // parse command line args
        String  inFileName     = args[0];
        String  outFileName    = args[1];
        boolean useDelim       = true;
        boolean allowUnmatched = false;

        for (int i = 2; i < args.length; i++)
        {
            if (args[i].equals(CMD_NODELIM))
                useDelim = false;
            else if (args[i].equals(CMD_UNMATCHED))
                allowUnmatched = true;
            else if (args[i].startsWith(CMD_SYMBOLDEF))
                ; // do nothing as load fn catches these
            else if (args[i].startsWith(CMD_SYMBOLFILE))
                loadSymbolFile(args[i].substring(CMD_SYMBOLFILE.length()), symbolSet);
            else
                throw new IllegalArgumentException(args[i]);
        }

        // load command line symbol definitions (those with CMD_SYMBOLDEF
        // skipped above)
        loadCmdLineSymbols(args, symbolSet);

        // open files
        InputStream  in  = new FileInputStream(inFileName);
        OutputStream out = new FileOutputStream(outFileName);

        // create sunstitutor
        SymbolSubst substitutor = new SymbolSubst(symbolSet, useDelim,
            allowUnmatched);

        // perform substitution
        substitutor.substitute(in, out);

        // close files
        in.close();
        out.close();

        System.out.println("Finished processing " + inFileName + " to " + outFileName);
    }
}
