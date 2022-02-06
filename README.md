# Space Siege Breakers MIDlet
Space Siege Breakers is a Java ME based tower defense type mobile phone game created as a homework for a [course](https://web.archive.org/web/20131109234507/http://amorg.aut.bme.hu/education/subjects/mobintro) in 2010.

![Screenshot 001](https://github.com/laszlolukacs/spacesiegebreakers/raw/master/docs/screenshots/screenshot001.png)

## Dependencies
* Linux or Windows
* [Java Development Kit 7 (32-bit)](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html), JDK 8 and newer cannot seem to produce functioning MIDlet jars; WTK requires a 32-bit JDK to launch device emulators
* [Sun Wireless Toolkit 2.5.2_01 for CLDC](http://www.oracle.com/technetwork/java/download-135801.html), [Java ME SDK 3.4](https://www.oracle.com/java/technologies/javame-sdk-downloads.html), or [any](https://www.oracle.com/java/technologies/java-me-sdk-3-0-5-downloads.html) [release](https://www.oracle.com/java/technologies/javame-sdk/java-me-sdk-v30.html) in between
* [Eclipse Oxygen 3a (4.7.3a)](https://www.eclipse.org/downloads/packages/release/oxygen/3a) with [Mobile Tools for Java plugin](http://www.eclipse.org/mtj/)
* [Ant 1.9.x](https://archive.apache.org/dist/ant/binaries/apache-ant-1.9.16-bin.zip) with [Antenna](http://antenna.sourceforge.net/) for CLI builds

## Summary of set up
#### Building the project from the CLI with Ant and Antenna
* Install the dependencies
* Set up the required environment variables (`JAVA_HOME`, `WTK_HOME` and `ANT_HOME`) have been set up the following way:
    * Make sure that `JAVA_HOME` is pointing to a 32-bit JDK <= 7 and that the `JAVA_HOME/bin` has been added to the `PATH`
    * Make sure that `WTK_HOME`  has been set and points to either to the WTK or a Java ME SDK root
    * Make sure that `ANT_HOME` has been set and `ANT_HOME/bin` has been added to the `PATH`, also make sure that Antenna's jar has been added to `ANT_HOME/lib`
* `git clone git@github.com:laszlolukacs/spacesiegebreakers.git`
* `ant build`

After a successful build the artifacts (the JAD and JAR files) will be located in the `/bld` directory.

#### Opening the project in Eclipse
* Install the dependencies (Ant and Antenna could be omitted)
* Set up Eclipse and MTJ
    * Add a 32-bit JDK <= 7 to Eclipse using 'Window/Preferences' then 'Java/JREs'
    * Select 'Window/Preferences' then 'Java ME' and point the 'WTK root' attribute to the WTK/Java ME SDK root directory
    * Add the emulators at 'Window/Preferences' then 'Java ME/Device Management', then hit 'Manual Install...' and select either the WTK or a Java ME SDK root directory and add the emulators
* `git clone git@github.com:laszlolukacs/spacesiegebreakers.git`
* In Eclipse use 'File/Import...', then select 'General/Existing Projects into Workspace' to open the project in Eclipse.

When the MIDlet packages have been created the artifacts (the JAD and JAR files) will be located in the `/deployed/<TargetDeviceName>` directory.

## Deployment
Copy the resulting `SpaceSiegeBreakers.jad` and `SpaceSiegeBreakers.jar` artifacts either from the `/bld` (when built with Ant) or from the `/deployed/<TargetDeviceName>` (when MIDlet packages are created from Eclipse) directory to the target device.