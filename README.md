# ![](src/main/resources/META-INF/pluginIcon.svg) LogSensei - IntelliJ IDEA Plugin (Work in Progress)


**LogSensei** is an IntelliJ IDEA plugin that detects missing or insufficient logging in Java code and provides context-aware Quick Fixes.  
I am developing this project to strengthen my skills in **IntelliJ Platform SDK**, **PSI/AST analysis**, and **JVM developer tooling**.


## 🔍 What the plugin does

### ✔️ PSI-based inspections
The plugin analyzes Java code and detects missing logs in:

- empty or poorly handled `catch` blocks  
- excessive `info` logs inside loops  
- early-returns missing log statements  
- missing log entry/exit points in public methods inside `@Service` classes  


### ✔️ Quick Fixes (Alt+Enter)
Each inspection provides ready-to-insert logging snippets, such as:

- `log.error(...)`  
- `log.info(...)`  
- `log.debug(...)`  

Snippets are inserted using safe PSI transformations (`PsiElementFactory`, `JavaCodeStyleManager`).

### ✔️ Automatic logger injection
Depending on the selected logging library, the plugin can:

- add Lombok annotations (`@Log4j2`, `@Slf4j`)  
- or generate a `java.util.logging.Logger log` field  

Imports will be automatically shortened and cleaned up.

### ✔️ Automatic dependency management
If required dependencies are missing, LogSensei can update:

- `pom.xml`  
- `build.gradle`  
- `build.gradle.kts`  

Supported libraries:

- **Log4j2**  
- **SLF4J + Logback**  
- **Lombok**  
- **java.util.logging**



## 🛠️ Technologies & Skills Demonstrated

- **IntelliJ Platform SDK**
  - PSI traversal and analysis  
  - `LocalInspectionTool` implementations  
  - `LocalQuickFix` actions   
- **Java**  
- **Maven & Gradle integration**  
- **Lombok annotation processing**  
- Clean architecture with modular Quick Fixes and centralized dependency logic  



## 🎯 Purpose of the project

This project is a practical way to develop skills in:

- JVM tooling  
- IDE plugin development  
- static code analysis  
- building tools that improve developer productivity  

LogSensei is intentionally evolving, so I can explore deeper parts of IntelliJ SDK and advanced PSI operations.



## 📌 Status

The plugin is **actively developed**.  
Core features work, and new inspections, fix strategies, and refactorings are added continuously.

