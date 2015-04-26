# Spock specification extractor

If you write specification using [Spock Framework](http://spockframework.github.io/spock/docs/1.0/index.html) you can put many information in it. For example, you could describe each step, add some description to whole specification or add links to additional pages and issues.

Maven plugin from this repository allows you to extract specification info from your tests and generate html report.

**Actual plugin is under development so you have to build plugin manually**

You have to add plugin to pom.xml:
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>spock-spec-extractor-maven-plugin</artifactId>
            <groupId>com.github.alien11689</groupId>
            <version>VERSION</version>
            <executions>
                <execution>
                    <id>extractor</id>
                    <phase>process-test-classes</phase>
                    <goals>
                        <goal>extract</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Now run mvn with goal 'spock-spec-extractor:extract' or goal from phase equal or after 'process-test-classes'.