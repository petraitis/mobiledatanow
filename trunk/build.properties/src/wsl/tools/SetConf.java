/**	$Id: SetConf.java,v 1.4 2003/01/16 00:05:08 tecris Exp $
 *
 *	Simple substitution program to build configuration files
 *
 */
package wsl.tools;

import java.util.Properties;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class SetConf
{
	private static final String confs [] =
    {
        "MdnRmiClient.conf",
        "MdnRmiServer.conf",
        "MdnSecureRegistry.conf",
        "MdnWmlServlet.conf",
        "MdnHtmlServlet.conf",
        "MsExchange.conf",
        "mdn.conf"
    };

	private static final String
		UNIX		= "unix",
		WINDOWS		= "windows",
		UNIX_SYM	= "unix.sym",
		WINDOWS_SYM	= "windows.sym",
		BAK_EXT		= ".bak",
		SYM_EXT		= ".sym";


	public static void
	main (
	 String args [])
		throws IOException
	{
        if (args.length < 2 ||
			(
        		!args [1].equals (WINDOWS) &&
				!args [1].equals (UNIX)))
        {
            System.out.println("Format is: setconf <MDN_HOME> <"
					+ WINDOWS
					+ "|"
					+ UNIX
					+ ">");
            System.exit(1);
        }

        boolean useDelim = true;
        boolean allowUnmatched = true;

        String sep = File.separator;
        String path = args[0]+sep+"wsl"+sep+"config"+sep+"mdn"+sep;
        String symName = path + args[1] + SYM_EXT;

        Properties symbolSet = new Properties();
        SymbolSubst.loadSymbolFile(symName, symbolSet);
        SymbolSubst substitutor = new SymbolSubst(symbolSet, useDelim, allowUnmatched);

		for (int i =0; i < confs.length; i++)
        {
            String inStr = path+confs[i]+BAK_EXT;
            String outStr = path+confs[i];
            System.out.println("Converting symbols in " + inStr + " into " + outStr);

            File fIn = new File(inStr);
            InputStream  in  = new FileInputStream(fIn);
            File fOut = new File(outStr);
            fOut.createNewFile();
            OutputStream out = new FileOutputStream(fOut);

            // perform substitution
            substitutor.substitute(in, out);

            // close files
            in.close();
            out.close();
        }
	}
}
