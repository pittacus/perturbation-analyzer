javac -cp "%CLASSPATH%";..\lib\jfreechart-1.0.12.jar;..\lib\jcommon-1.0.15.jar dynamic\*.java
jar -cfm ..\PerturbationAnalyzer.jar META-INF\MANIFEST.MF COPYING NOTICE dynamic\plugin.props dynamic\*.class ..\lib\org ..\lib\com
copy /Y ..\PerturbationAnalyzer.jar "..\cytoscape-v2.6.2\plugins\"
