<div align="center">
<br><br>
    <a href="http://ci.pluginwiki.us/job/BSLib/"><img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=FFFFFF" alt="Jenkins"></a> 
    <a href="https://repo.pluginwiki.us/service/rest/repository/browse/maven-releases/lib/brainsynder/API/"><img src="https://img.shields.io/badge/Maven_Repo-C71A36?style=for-the-badge&logo=Apache+Maven&logoColor=FFFFFF" alt="Maven Repo"></a><br>
    <a href="https://www.codefactor.io/repository/github/brainsynder-dev/bslib"><img src="https://www.codefactor.io/repository/github/brainsynder-dev/bslib/badge" alt="CodeFactor" /></a> 
    <img src="https://img.shields.io/maven-metadata/v?color=red&label=Current%20Version&metadataUrl=https%3A%2F%2Frepo.pluginwiki.us%2Frepository%2Fmaven-releases%2Flib%2Fbrainsynder%2FAPI%2Fmaven-metadata.xml">
</div>

<h1>BSLib</h1>

BSLib is a collection of useful classes, builders, helpers, and features such as:
- Colorize `(A class to help with handling ChatColors from regular colors to HEX colors)`
- Tellraw `(A simple class to help with creating Tellraw messages)`
- AnvilGUI `(A class that will open the Anvil GUI for a player and fetch the result when completed)`
- ServerVersion `(An enum for fetching what version the server is currently using)`

<h1>How to add it to my project</h1>

```
<repository>
    <id>bs-public</id>
    <url>https://repo.pluginwiki.us/repository/maven-releases/</url>
</repository>

<dependency>
    <groupId>lib.brainsynder.v2</groupId>
    <artifactId>API</artifactId>
    <version> {CURRENT_VERSION} </version>
</dependency>


<!--  Command API  ------  !!! OPTIONAL !!!  -->
<dependency>
    <groupId>lib.brainsynder.v2</groupId>
    <artifactId>command_api</artifactId>
    <version> {CURRENT_VERSION} </version>
</dependency>



<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.1</version>
    <configuration>
        <minimizeJar>true</minimizeJar>
        <createDependencyReducedPom>false</createDependencyReducedPom>
        <relocations>
            <relocation>
                <pattern>lib.brainsynder</pattern>
                <shadedPattern>YOUR.PACKAGE.shaded.bslib</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
