# Spock specification extractor

If you write specification using [Spock Framework](http://spockframework.github.io/spock/docs/1.0/index.html) you can put many information in it. For example, you could describe each step, add some description to whole specification or add links to additional pages and issues.

Maven plugin from this repository allows you to extract specification info from your tests and generate html report.

Add plugin repository to your pom.xml:
```xml
<pluginRepositories>
    <pluginRepository>
        <id>github-alien11689</id>
        <name>alien11689's Git based repo</name>
        <url>https://github.com/alien11689/maven-repo/raw/master/</url>
    </pluginRepository>
</pluginRepositories>
```

You have to add plugin to pom.xml:
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>spock-spec-extractor-maven-plugin</artifactId>
            <groupId>com.github.alien11689</groupId>
            <version>0.0.1</version>
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

Report file will be generated in target/specification/output.xml directory.

## Parameters

* pattern - java regex for paths to spock files - default '.*\.groovy$'
* omitBlocks - list of blocks to omit in report file - available blocks EXPECT, CLEANUP, WHEN, THEN, SETUP, GIVEN, WHERE, AND - default '[WHERE]'
* mergeAndBlock - if true then AND block description will be merged with previous block description and AND block will be omitted in report file - default 'false'
* omitBlocksWithoutDescription - if true then all blocks without description will not be exposed in report file - default 'false'

Example configuration:
```xml
<plugin>
    <artifactId>spock-spec-extractor-maven-plugin</artifactId>
    <groupId>com.github.alien11689</groupId>
    <version>0.0.1</version>
    <configuration>
        <pattern>.*Test\.groovy$</pattern>
        <mergeAndBlock>true</mergeAndBlock>
        <omitBlocksWithoutDescription>true</omitBlocksWithoutDescription>
        <omitBlocks>
            <omitBlock>WHERE</omitBlock>
            <omitBlock>SETUP</omitBlock>
            <omitBlock>CLEANUP</omitBlock>
        </omitBlocks>
    </configuration>
</plugin>
```
