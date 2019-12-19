# BSLib
This is not a new plugin (Like SimpleAPI). Instead this aims to make the plugins that have SimpleAPI shaded into them smaller.

- [Jenkins](http://ci.pluginwiki.us/job/BSLib/)
- [Maven](http://ci.pluginwiki.us/plugin/repository/everything/lib/brainsynder/API/)
```
<repository>
    <id>bs-repo</id>
    <url>http://ci.pluginwiki.us/plugin/repository/everything/</url>
</repository>
<dependency>
    <groupId>lib.brainsynder</groupId>
    <artifactId>API</artifactId>
    <version>0.1-B(JENKINS BUILD)</version>
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
