The procedure for setting up the mdn server development enviroment:
<br><br>
Install Eclipse and SVN, check out MDN old projects:<br>
1.	Install Eclipse. <br>
2.	Install MyEclipse plug-in, <br>
3.	Install Subclipse 1.0.3 plug-in (see <a href='http://subclipse.tigris.org/'>http://subclipse.tigris.org/</a>), which is Eclipse plug-in for cvs management. <br>
4.	Open Eclipse with new workspace folder. <br>
5.	File -> New -> Other -> SVN -> Checkout Projects from SVN -> Create a new repository location -> put url <a href='http://svn.mobiledatanow.com/mdn'>http://svn.mobiledatanow.com/mdn</a> to url field. Then you can see all the projects available for check out. <br>
6.	Check out all the MDN projects. <br>
<br><br>
Fix the problems:<br>
<br><br>
7.	Fix the classpath problems: right click the project which has classpath problems.<br>
8.	Click properties ->Java Build Path -> Libraries tab -> edit file class path locations to correct locations. <br>
9.	If you are using Java 1.5, change the compiler compliance level to be 1.4, then you could pass Enumeration errors. <br>
(Window -> Preference -> Java -> Compiler)<br>
<br><br>

Create MDN server<br><br>
11.	Run the batch file buildmdn-rmi-windows.cmd in the mdn-mdn folder, which will generate the stub files needed. Be ware that rmic command is inside java bin folder. Maybe you need to change the classpath of rmic command inside the batch file. <br>
12.	Run the batch file buildframework-rmi-windows.cmd in the mdn-framework folder. Same problem with above batch file. <br>
13.	Create the System DSN data source using ODBC windows tool. Set data source name as “mdnserver”. In this case, we use mdn.mdb Microsoft Access file instead of mysql database.<br>
14.	Create java application server “MdnServer” (Project: mdn-mdn; Main class: wsl.mdn.server.MdnServer) <br>
15.	Need to add extra Classpath in the Classpath tab: Click User Entries, then click right hand side button: Advanced->Add Folders. <br>
16.	Firstly, add \mdn-mdn\wsl\config\mdn folder, which has sec.dat file needed (This file include the assigned license key). <br>
17.	Secondly, add \mdn-mdn\lib folder, which has library file needed. <br>
18.	If after running the server, said some files are not available, please add more class paths. <br>
19.	When you first time run server, it will ask you the license key. So now we create/run License Key server. <br><br>

Create License server<br><br>
20.	Create java application server “License”. Project mdn-licence, Main class: wsl.licence.Licence. <br>
21.	Run this server, load customer key first by copying the key from MDN server, then generating the second part key, and copy back that part of key to MDN server, then here you go, MDN server should be running. <br>
<br><br>
Run the MDN Administration Application<br>
22.	Create java application. Project mdn-mdn, Main class: wsl.mdn.admin.MdnAdminApp.<br>
23.	Run this application, it should be no problem for running. <br><br>

Run the OpenLaszlo presentation.<br><br>

24.	Create the project for the front end – dashwell.<br>
25.	Create the tomcat server for it. Configure Server -> Choose Tomcat 5-> Set up the settings (Home Directory etc...)<br>
26.	For the JDK setting of Tomcat Server, please add “-Xms512m -Xmx1024m” to the Optional Java VM Arguments.<br>
27.	In the folder src/com/framedobjects/dashwell, the file config.properties need to be changed:<br>
The line<br>
language_file_path=C://DevelopEnv//Tomcat5//webapps//dashwell//lang<br>
Need to be changed to according location of your local language file.<br><br>

28.	Deploy the dashwell project into Tomcat. Right click the project -> MyEclipse -> Add and Remove Project Deployments …<br>
29.	Run the project, in the browser, type in <a href='http://localhost:8080/dashwell/'>http://localhost:8080/dashwell/</a>. MDN should start.<br>