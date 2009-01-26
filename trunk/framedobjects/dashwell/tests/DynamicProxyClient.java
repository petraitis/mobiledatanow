package com.framedobjects.dashwell.tests;

import java.net.MalformedURLException;

import java.net.URL;



import javax.xml.namespace.QName;

import javax.xml.rpc.Service;

import javax.xml.rpc.ServiceException;

import javax.xml.rpc.ServiceFactory;




/**

 * Simple webservice client which uses dynamic proxy

 *

 * @author Jaikiran Pai

 */

public class DynamicProxyClient {



    public static void main(String args[]) {



        try {

            

            // ServiceFactory instance

            ServiceFactory serviceFactory = ServiceFactory.newInstance();

            System.out.println("Got the service factory");

            String wsdlURLString = "http://localhost:8080/axis/services/HelloWorld?wsdl";

            URL wsdlURL = new URL(wsdlURLString);

            String serviceName = "HelloWorldServiceService";

            String nameSpaceURI = "http://jaikiran.com";

            String portName = "HelloWorld";

            // get hold of the service by passing the URL of the wsdl and the

            // service name

            Service service = serviceFactory.createService(wsdlURL, new QName(

                    nameSpaceURI, serviceName));

            System.out.println("Got the service: " + service);

            // create the service proxy

            HelloWorldService serviceProxy = (HelloWorldService) service

                    .getPort(new QName(nameSpaceURI, portName),

                            HelloWorldService.class);

            System.out.println("Got the service proxy: " + serviceProxy);

            System.out.println("Invoking method on service proxy........");

            System.out.println(serviceProxy.sayHelloTo("Dynamic user"));



        } catch (ServiceException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        } catch (MalformedURLException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

    }



}
