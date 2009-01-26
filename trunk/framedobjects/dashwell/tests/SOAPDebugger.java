package com.framedobjects.dashwell.tests;

import com.framedobjects.dashwell.tests.soapdebugger.MainWindow;
import com.framedobjects.dashwell.tests.soapdebugger.SOAPClient;

/**
 * @author sverrehu
 */
public final class SOAPDebugger {
    private static void test(SOAPClient sc)
    throws Exception {
    }
    
    public static void main(String[] args) {
        SOAPClient sc;
        
        if (args.length != 1) {
            System.out.println("usage: SOAPDebugger wsdl-file-or-uri");
            System.exit(0);
        }
        try {
            sc = new SOAPClient(args[0]);
            test(sc);
            MainWindow mw = new MainWindow(sc);
            mw.pack();
            mw.setVisible(true);
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }
}
