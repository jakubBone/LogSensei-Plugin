package com.jakubbone.logsensei.dependency;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jakubbone.logsensei.dependency.model.BuildSystem;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;

public class DependencyManager {
    public static boolean addDependencies(Project project, BuildSystem buildSystem,
                                          boolean addLombok, LoggingLibrary loggingLibrary) {

        VirtualFile buildFile = project.getBaseDir().findChild(buildSystem.getBuildFile());

        try{
            String currentContent = readFile(buildFile);
            String modifiedContent = generateModifiedContent(
                    buildSystem,
                    addLombok,
                    currentContent,
                    loggingLibrary
            );

            writeFile(project, buildFile, modifiedContent);

            return true;
        } catch (IOException e){
            return false;
        }
    }

    private static String readFile(VirtualFile buildFile) throws IOException {
        return new String(buildFile.contentsToByteArray(), StandardCharsets.UTF_8);
    }

    private static void writeFile(Project project, VirtualFile buildFile, String content){
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                buildFile.setBinaryContent(content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String generateModifiedContent(BuildSystem buildSystem, boolean addLombok, String currentContent, LoggingLibrary loggingLibrary){
        if (buildSystem.isMaven()) {
            return addMavenDependencies(currentContent, addLombok, loggingLibrary);
        } else if (buildSystem.isGradle()) {
            boolean isKotlin = buildSystem == BuildSystem.GRADLE_KOTLIN;
            return addGradleDependencies(currentContent, addLombok, loggingLibrary,
                    isKotlin);
        } else {
            return currentContent;
        }
    }

    private static String addMavenDependencies(String content, boolean addLombok, LoggingLibrary loggingLibrary) {
        StringBuilder dependencies = new StringBuilder();

        if (addLombok) {
            dependencies.append("""
                    <dependency>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.30</version>
                        <scope>provided</scope>
                    </dependency>
                """);
        }

        if (loggingLibrary.requiresDependency()) {
            if (loggingLibrary == LoggingLibrary.LOG4J2) {
                dependencies.append("""
                        <dependency>
                            <groupId>org.apache.logging.log4j</groupId>
                            <artifactId>log4j-core</artifactId>
                            <version>2.22.0</version>
                        </dependency>
                        <dependency>
                            <groupId>org.apache.logging.log4j</groupId>
                            <artifactId>log4j-api</artifactId>
                            <version>2.22.0</version>
                        </dependency>
                    """);
            } else if (loggingLibrary == LoggingLibrary.SLF4J_LOGBACK) {
                dependencies.append("""
                        <dependency>
                            <groupId>org.slf4j</groupId>
                            <artifactId>slf4j-api</artifactId>
                            <version>2.0.9</version>
                        </dependency>
                        <dependency>
                            <groupId>ch.qos.logback</groupId>
                            <artifactId>logback-classic</artifactId>
                            <version>1.4.14</version>
                        </dependency>
                    """);
            }
        }

        // Find the </dependencies> closing tag and insert before it
        int dependenciesEndIndex = content.lastIndexOf("</dependencies>");
        return content.substring(0, dependenciesEndIndex) +
                    dependencies +
                    content.substring(dependenciesEndIndex);
    }

    private static String addGradleDependencies(String content, boolean addLombok,
                                                LoggingLibrary loggingLibrary, boolean isKotlin) {
        StringBuilder dependencies = new StringBuilder("\n");

        if (addLombok) {
            if (isKotlin) {
                dependencies.append("    compileOnly(\"org.projectlombok:lombok:1.18.30\")\n");
                dependencies.append("    annotationProcessor(\"org.projectlombok:lombok:1.18.30\")\n");
            } else {
                dependencies.append("    compileOnly 'org.projectlombok:lombok:1.18.30'\n");
                dependencies.append("    annotationProcessor 'org.projectlombok:lombok:1.18.30'\n");
            }
        }

        if (loggingLibrary.requiresDependency()) {
            if (loggingLibrary == LoggingLibrary.LOG4J2) {
                if (isKotlin) {
                    dependencies.append("    implementation(\"org.apache.logging.log4j:log4j-core:2.22.0\")\n");
                    dependencies.append("    implementation(\"org.apache.logging.log4j:log4j-api:2.22.0\")\n");
                } else {
                    dependencies.append("    implementation 'org.apache.logging.log4j:log4j-core:2.22.0'\n");
                    dependencies.append("    implementation 'org.apache.logging.log4j:log4j-api:2.22.0'\n");
                }
            } else if (loggingLibrary == LoggingLibrary.SLF4J_LOGBACK) {
                if (isKotlin) {
                    dependencies.append("    implementation(\"org.slf4j:slf4j-api:2.0.9\")\n");
                    dependencies.append("    implementation(\"ch.qos.logback:logback-classic:1.4.14\")\n");
                } else {
                    dependencies.append("    implementation 'org.slf4j:slf4j-api:2.0.9'\n");
                    dependencies.append("    implementation 'ch.qos.logback:logback-classic:1.4.14'\n");
                }
            }
        }

        // Find dependencies block and add dependencies
        int dependenciesIndex = content.indexOf("dependencies {");
        int blockStart = content.indexOf("{", dependenciesIndex) + 1;
        return content.substring(0, blockStart) +
                    dependencies +
                    content.substring(blockStart);

    }
}