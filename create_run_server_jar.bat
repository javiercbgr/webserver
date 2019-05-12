:: ----------------------------------------------
:: Javier Cabero 2019 (c)
::
:: Compiles, creates JAR and executes the binary.
:: Tested on Java 12.0.1 and Windows 10.
:: ----------------------------------------------

javac -cp src/main/java src/main/java/me/homework/*.java
jar -cvfm webserver-1.0-SNAPSHOT.jar src/main/java/META-INF/MANIFEST.MF -C src/main/java .
java -jar webserver-1.0-SNAPSHOT.jar 12144 10 . 
