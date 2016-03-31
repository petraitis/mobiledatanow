Set classpath. All dependencies is in Mobiledatanow/lib directory<br>
For configuration to build developer need to set below properties:<br>

<pre><code>JdbcDataSource.url<br>
DatabaseDriver.uploadUrl<br>
ExportProject.downloadUrl<br>
ImportProject.uploadUrl<br>
WebService.compileFilePath<br>
logFile.filePath <br>
</code></pre>

on MobileDataNow\resources\config\development\wsl\config\mdn\MdnRmiServer.conf file<br>
And tomcat.path property in build.xml to run ant target order to build.xml MDN<br>
<pre><code>-          “database.development” target to make a empty DB<br>
-          “all” target<br>
-          “deploy” target<br>
-          “setup.tomcat”<br>
</code></pre>