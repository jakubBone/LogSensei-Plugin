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

        // Lombok
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

        // Log4j2 / SLF4J+Logback
        if (loggingLibrary != null && loggingLibrary.requiresDependency()) {
            // Main dependency
            dependencies.append("""
                <dependency>
                    <groupId>%s</groupId>
                    <artifactId>%s</artifactId>
                    <version>%s</version>
                </dependency>
            """.formatted(
                    loggingLibrary.getGroupId(),
                    loggingLibrary.getArtifactId(),
                    loggingLibrary.getVersion()
            ));

            // Optional secondary (e.g. log4j-api)
            if (loggingLibrary.hasSecondaryDependency()) {
                dependencies.append("""
                    <dependency>
                        <groupId>%s</groupId>
                        <artifactId>%s</artifactId>
                        <version>%s</version>
                    </dependency>
                """.formatted(
                        loggingLibrary.getSecondaryGroupId(),
                        loggingLibrary.getSecondaryArtifactId(),
                        loggingLibrary.getSecondaryVersion()
                ));
            }
        }

        int dependenciesEndIndex = content.lastIndexOf("</dependencies>");
        if (dependenciesEndIndex < 0) {
            return content + "\n\n<dependencies>\n" +
                    dependencies +
                    "\n</dependencies>\n";
        }

        return content.substring(0, dependenciesEndIndex) +
                dependencies +
                content.substring(dependenciesEndIndex);
    }

    private static String addGradleDependencies(String content, boolean addLombok,
                                                LoggingLibrary loggingLibrary, boolean isKotlin) {
        StringBuilder dependencies = new StringBuilder("\n");

        // Lombok
        if (addLombok) {
            if (isKotlin) {
                dependencies.append("    compileOnly(\"org.projectlombok:lombok:1.18.30\")\n");
                dependencies.append("    annotationProcessor(\"org.projectlombok:lombok:1.18.30\")\n");
            } else {
                dependencies.append("    compileOnly 'org.projectlombok:lombok:1.18.30'\n");
                dependencies.append("    annotationProcessor 'org.projectlombok:lombok:1.18.30'\n");
            }
        }

        // Log4j2 / SLF4J+Logback
        if (loggingLibrary != null && loggingLibrary.requiresDependency()) {
            // Main dependency
            String primary = loggingLibrary.getGroupId() + ":" +
                    loggingLibrary.getArtifactId() + ":" +
                    loggingLibrary.getVersion();

            if (isKotlin) {
                dependencies.append("    implementation(\"").append(primary).append("\")\n");
            } else {
                dependencies.append("    implementation '").append(primary).append("'\n");
            }

            // Optional secondary
            if (loggingLibrary.hasSecondaryDependency()) {
                String secondary = loggingLibrary.getSecondaryGroupId() + ":" +
                        loggingLibrary.getSecondaryArtifactId() + ":" +
                        loggingLibrary.getSecondaryVersion();

                if (isKotlin) {
                    dependencies.append("    implementation(\"").append(secondary).append("\")\n");
                } else {
                    dependencies.append("    implementation '").append(secondary).append("'\n");
                }
            }
        }

        int dependenciesIndex = content.indexOf("dependencies {");
        if (dependenciesIndex < 0) {
            return content + "\n\ndependencies {" +
                    dependencies +
                    "}\n";
        }

        int blockStart = content.indexOf("{", dependenciesIndex) + 1;
        return content.substring(0, blockStart) +
                dependencies +
                content.substring(blockStart);
    }
}