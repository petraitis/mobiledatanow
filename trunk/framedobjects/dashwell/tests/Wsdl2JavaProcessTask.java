/**
 * Copyright (C) 2006  Bull S. A. S.
 * Bull, Rue Jean Jaures, B.P.68, 78340, Les Clayes-sous-Bois
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA  02110-1301, USA.
 **/
package com.framedobjects.dashwell.tests;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.net.SetProxy;
import org.objectweb.orchestra.util.BPELConstants;
import org.objectweb.orchestra.util.Util;



/**
 * Wsdl2JavaProcessTask.java
 *
 * @author Goulven Le Jeune & Charles Souillard
 * @version $Id$
 */
public class Wsdl2JavaProcessTask extends StandardDeployTask {

    private boolean serverSide = false;
    private String wsdlFile = null;
    private String output = null;
    private boolean jbiWS = false;

    /**
     * @return the output
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * @return the wsdlFile
     */
    public String getWsdlFile() {
        return this.wsdlFile;
    }

    /**
     * @param wsdlFile the wsdlFile to set
     */
    public void setWsdlFile(String wsdlFile) {
        this.wsdlFile = wsdlFile;
    }

    /**
     * @return the serverSide
     */
    public boolean isServerSide() {
        return this.serverSide;
    }

    /**
     * @param serverSide the serverSide to set
     */
    public void setServerSide(boolean serverSide) {
        this.serverSide = serverSide;
    }

    public Wsdl2JavaProcessTask(boolean serverSide) {
        this(serverSide,null,null);
    }

    public Wsdl2JavaProcessTask(boolean serverSide, String wsdlFile, String output) {
        setTaskName("wsdl2javaProcess");
        setServerSide(serverSide);
        setOutput(output);
        setWsdlFile(wsdlFile);
    }

    public void execute() {
        log("Starting Wsdl2JavaProcessTask...");
        setProxy();
        createDirs();
        wsdl2java();
        log("Finishing Wsdl2JavaProcessTask...");
    }

    public void createDirs() {
        String st = getProp("generate_wsdl_dir") + this.sep + getProcessName();
        createDirName(st);
    }

    public void setProxy() {
        SetProxy sp = new SetProxy();
        sp.setProject(getProject());
        if (getProp("proxyHost")!=null && getProp("proxyPort")!=null) {
            sp.setProxyHost(getProp("proxyHost"));
            sp.setProxyPort(Integer.parseInt(getProp("proxyPort")));
        }
        if (getProp("nonProxyHosts")!=null) {
            sp.setNonProxyHosts(getProp("nonProxyHosts"));
        }
        sp.setTaskName("setproxy");
        sp.execute();
    }

    public void wsdl2java() {
        try {
            Method m = null;
            Class cl = null;
            URL[] urlTab = new URL[1];
            String url = Util.getFilePrefix();
            urlTab[0] = new URL(url+getProp("axis-ant"));
            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            URLClassLoader classL = new URLClassLoader(urlTab,parent);
            cl = Class.forName("org.apache.axis.tools.ant.wsdl.Wsdl2javaAntTask",true, classL);
            Object wsdl2javatask = cl.newInstance();
            m = cl.getMethod("setOutput", new Class[]{java.io.File.class});
            m.invoke(wsdl2javatask,new Object[]{new File(getOutput())});
            m = cl.getMethod("setProject", new Class[]{Project.class});
            m.invoke(wsdl2javatask,new Object[]{getProject()});
            m = cl.getMethod("setURL", new Class[]{java.lang.String.class});
            m.invoke(wsdl2javatask,new Object[]{getWsdlFile()});
            m = cl.getMethod("setAll", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(true)});
            m = cl.getMethod("setHelperGen", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(true)});
            m = cl.getMethod("setServerSide", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(isServerSide())});
            m = cl.getMethod("setVerbose", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(true)});
            m = cl.getMethod("setNoWrapped", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(true)});
            m = cl.getMethod("setWrapArrays", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(true)});
            m = cl.getMethod("setDebug", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(false)});
            m = cl.getMethod("setTaskName", new Class[]{java.lang.String.class});
            m.invoke(wsdl2javatask,new Object[]{"wsdl2java"});
            m = cl.getMethod("setAllowInvalidUrl", new Class[]{boolean.class});
            m.invoke(wsdl2javatask,new Object[]{new Boolean(true)});

            Class typeMappingVersionEnum = Class.forName("org.apache.axis.tools.ant.wsdl.TypeMappingVersionEnum",true, classL);
            Object tmve = typeMappingVersionEnum.newInstance();
            m = typeMappingVersionEnum.getMethod("setValue", new Class[]{String.class});
            m.invoke(tmve,new Object[]{BPELConstants.AXIS_TYPE_MAPPING_VERSION});

            m = cl.getMethod("setTypeMappingVersion", new Class[]{typeMappingVersionEnum});
            m.invoke(wsdl2javatask,new Object[]{tmve});

            m = cl.getMethod("execute", (Class[])null);
            m.invoke(wsdl2javatask,(Object[])null);

        } catch (Exception e) {
            throw new BuildException("Exception in Wsdl2JavaProcessTask wsdl2java : \n"+e,e);
        }
    }

    /**
     * @return the jbiWS
     */
    public boolean isJbiWS() {
        return this.jbiWS;
    }

    /**
     * @param jbiWS the jbiWS to set
     */
    public void setJbiWS(boolean jbiWS) {
        this.jbiWS = jbiWS;
    }

}
