# BSLib
This is Library I will shade into my plugins from now on.
```
<repository>
    <id>everything</id>
    <url>http://ci.pluginwiki.us/plugin/repository/everything/</url>
</repository>
<dependency>
    <groupId>simple.brainsynder</groupId>
    <artifactId>API</artifactId>
    <version>4.0.3-SNAPSHOT</version>
</dependency>



<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.1</version>
    <configuration>
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
