# ![](src/main/resources/META-INF/pluginIcon.svg) LogSensei - IntelliJ IDEA Plugin


**LogSensei** is an IntelliJ IDEA plugin that detects missing or insufficient logging in Java code and provides context-aware Quick Fixes.


## 📋 Requirements

- **IntelliJ IDEA** 2024.3 or newer (Community or Ultimate)
- **Java** 17+
- **Project type**: Maven or Gradle (for automatic dependency management)
- **Lombok plugin** (recommended) — for `@Slf4j` / `@Log4j2` support. Without it, code compiles but IDE shows false errors


## 📥 Installation

### From JetBrains Marketplace (recommended)
1. Open IntelliJ IDEA
2. Go to **Settings** → **Plugins** → **Marketplace**
3. Search for **"LogSensei"**
4. Click **Install** → **Restart IDE**

### Manual installation
1. Download latest `.zip` from [Releases](https://github.com/jakubBone/LogSensei-Plugin/releases)
2. Go to **Settings** → **Plugins** → ⚙️ → **Install Plugin from Disk...**
3. Select downloaded `.zip` file → **Restart IDE**


## 🎯 Usage

### Automatic inspections
LogSensei automatically analyzes your code and highlights issues:

| Issue | Highlight | Suggested fix |
|-------|-----------|---------------|
| Empty catch block | ⚠️ Warning | Add `log.error(...)` |
| INFO log in loop | ⚠️ Warning | Change to `log.debug(...)` |
| Early return without log | ⚠️ Warning | Add `log.debug(...)` |
| Spring component method without entry/exit logs | ⚠️ Warning | Add `log.info(...)` |

Supported Spring annotations: `@Service`, `@Controller`, `@RestController`

### Quick Fix (Alt+Enter)
1. Place cursor on highlighted code
2. Press **Alt+Enter** (or click 💡)
3. Select suggested fix
4. Choose logging library (first time only)

### Supported logging libraries
When applying a fix, you can choose:
- **SLF4J + Logback** (recommended)
- **Log4j2**
- **java.util.logging**

LogSensei will automatically:
- Add Lombok annotation (`@Slf4j` or `@Log4j2`) if available
- Add missing dependencies to `pom.xml` or `build.gradle`


## 🔍 Inspections

### 1. Catch block without logging
```java
// ⚠️ Warning: Catch block missing ERROR log
try {
    riskyOperation();
} catch (Exception e) {
    // empty or no log
}

// ✅ After fix:
try {
    riskyOperation();
} catch (Exception e) {
    log.error("[methodName] An exception occurred.", e);
}
```

### 2. High-frequency log in loop
```java
// ⚠️ Warning: INFO log in loop (performance issue)
for (Item item : items) {
    log.info("Processing: {}", item);
}

// ✅ After fix:
for (Item item : items) {
    log.debug("Processing: {}", item);
}
```

### 3. Early return without logging
```java
// ⚠️ Warning: Early return without DEBUG log
public void process(String input) {
    if (input == null) return;
    // ...
}

// ✅ After fix:
public void process(String input) {
    if (input == null) {
        log.debug("[ClassName] Early return");
        return;
    }
    // ...
}
```

### 4. Spring component method without entry/exit logs
Works with `@Service`, `@Controller`, and `@RestController`:
```java
@Service  // or @Controller, @RestController
public class UserService {
    // ⚠️ Warning: Missing entry/exit logs
    public User findUser(Long id) {
        return repository.findById(id);
    }

    // ✅ After fix:
    public User findUser(Long id) {
        log.info("[findUser] Operation started");
        User result = repository.findById(id);
        log.info("[findUser] Operation finished");
        return result;
    }
}
```


## ⚙️ Configuration

Inspections can be enabled/disabled individually:

**Settings** → **Editor** → **Inspections** → **LogSensei**

- ☑️ Catch block without error logging
- ☑️ High-frequency log in loop
- ☑️ Early return without logging
- ☑️ Spring component method missing entry/exit logs


## 🛠️ Technologies

- **IntelliJ Platform SDK** — PSI traversal and analysis
- **Java 17**
- **Gradle** with IntelliJ Platform Plugin 2.9.0


## 📝 License

[MIT License](LICENSE)


## 🤝 Contributing

Issues and Pull Requests are welcome!

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

