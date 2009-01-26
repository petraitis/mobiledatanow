package com.framedobjects.dashwell.tests;
/*
 * TaxBasicClient.java
 *
 * A client application for the TaxBasic Web Service
 * This will run the text and gui versions based on input from the command line. 
 * -text = text based command line version
 * -gui = graphical version
 *
 * Notes: This requires the Java SE 6 which include the JAX libraries.
 *
 */

//import com.strikeiron.taxbasic.ws.*;
//import com.strikeiron.taxbasic.gui.*;

import java.io.*;
import javax.swing.*;

import com.strikeiron.www.TaxDataBasicSoap;

/**
 *
 * @author StrikeIron
 */
public class TaxBasicClient {

    static final String INSTRUCTION_MESSAGE = "Step 1: Enter your StrikeIron User ID(registered user) or a valid Email Address(unregistered user)." +
            "\n\nStep 2: Enter password(registered users only) or skip by pressing <Enter> for unregistered users." + 
            "\n\nStep 3: Enter zip code.";

    static final String USER_ID_INPUT_MESSAGE = "StrikeIron User ID or Valid Email Address: ";         
    static final String PASSWORD_INPUT_MESSAGE = "Registered User Password or press <Enter>: ";
    
    static final String COMMAND_LINE_INPUT_TEXT = "-text";
    static final String COMMAND_LINE_INPUT_GUI = "-gui"; 

    public static final String HELP_MESSAGE = "usage: [ " + COMMAND_LINE_INPUT_TEXT + " | " + 
            COMMAND_LINE_INPUT_GUI + " ]";
        
    /**
     * Creates a new instance of TaxBasicClient
     */
    public TaxBasicClient( String [] args ) {
        if( args[0].equals("-text") ) {
            commandLineTaxBasic( );
        }
        else if( args[0].equals("-gui") ) {
            guiTaxBasic( );
        }
        else {
            System.out.println( HELP_MESSAGE );
        }
    }

    public void commandLineTaxBasic( ) {
        System.out.println( INSTRUCTION_MESSAGE + "\n" );
             
        String emailAddress = "";
        String userID = "";
        String password = "";
        String zipCode = "";

        // get the user input
        userID = getUserInput( USER_ID_INPUT_MESSAGE );
        password = getUserInput( PASSWORD_INPUT_MESSAGE );
        // if password is empty this is an unregistered user
        if( password.equals("") ) {
            emailAddress = userID; 
            userID = "";
        }

        zipCode = getUserInput( "Zip Code: ");

        try {   
            // setup service and soap implementations
            TaxDataBasic service = new TaxDataBasic();
            TaxDataBasicSoap port = service.getTaxDataBasicSoap();            
            // Holder objects that will store the return results
            javax.xml.ws.Holder<TaxRateUSAData> getTaxRateUSResultHolder = new javax.xml.ws.Holder( );
            javax.xml.ws.Holder<SISubscriptionInfo> siSubscriptionInfoHolder = new javax.xml.ws.Holder( );

            // call the Web Service //todo
            port.getTaxRateUS(emailAddress, userID, password, zipCode, getTaxRateUSResultHolder, siSubscriptionInfoHolder);
        
            // get the returned values and cast to appropriate class
            TaxRateUSAData taxData = (TaxRateUSAData)getTaxRateUSResultHolder.value;
            SISubscriptionInfo subscriptionInfo = (SISubscriptionInfo)siSubscriptionInfoHolder.value;            
        
            // output the results
            System.out.println( "\nTax Data" + 
                    "\n========\n" );
            
            System.out.println( "Zip Code: " + taxData.getZipCode() + 
                "\nCity: " + taxData.getCityName() + 
                "\nCounty: " + taxData.getCountyName() + 
                "\nState: " + taxData.getState() + 
                "\nState Sales Tax: " + taxData.getStateSalesTax() +
                "\nState Use Tax: " + taxData.getStateUseTax() +
                "\nCounty Sales Tax: " + taxData.getCountySalesTax() +
                "\nCounty Use Tax: " + taxData.getCountyUseTax() +
                "\nCity Sales Tax: " + taxData.getCitySalesTax() +
                "\nCity Use Tax: " + taxData.getCityUseTax() + 
                "\nTotal Sales Tax: " + taxData.getTotalSalesTax() +
                "\nTotal Use Tax: " + taxData.getTotalUseTax() +
                "\nTax Shipping Alone: " + taxData.getTaxShippingAlone() +
                "\nTax Shipping & Handling: " + taxData.getTaxShippingHandling() ); 
 
            System.out.println( "\nLicense Information" + 
                    "\n===================\n" );

            System.out.println( "License Status: " + subscriptionInfo.getLicenseStatus() + 
                    "\nLicense Action: " + subscriptionInfo.getLicenseAction() + 
                    "\nRemaining Hits: " + subscriptionInfo.getRemainingHits() );   
        }
        catch( Exception ex ) {
            System.out.println( "There was an error: " + ex.getMessage() ); 
        }
        
        
    }
    
    public void guiTaxBasic( ) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        } 
        catch( Exception e ) {
            e.printStackTrace( );
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TaxBasicJFrame().setVisible(true);
            }
        });       
        
    }
    
    
    /**
     * @param prompt the prompt to display
     *
     * Displays a prompt and gets user input
     */
    private String getUserInput( String prompt )
    {
        System.out.print( prompt );
        
        BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );
        
        String userInput = null; 
        
        try {
            userInput = br.readLine( );            
        }
        catch( IOException ioe ) {
            System.out.println( "IO Error." );
            System.exit( 1 );
        }
        
        return( userInput );
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        boolean help = false;
        
        // require user to specify us or canada
        if( args.length == 1 ) {
            TaxBasicClient taxBasicClient = new TaxBasicClient( args );
        }
        else {
            System.out.println( TaxBasicClient.HELP_MESSAGE );
        }
        
    }
    
}
