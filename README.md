# LogSensei MVP (user studies)

### IntelliJ plugin (Java) that helps beginners add the right logs in the right places. 

## Goal

- Help beginner Java developers add the right logs in the right places.

- KISS: works immediately after install, no config, no AI.

- Faster debugging and clearer code flow.

## MVP scope


### * Tech

Language: Java (plugin written in Java)

IDE Platform: IntelliJ Platform SDK (inspections + quick-fixes)

Logging: Log4J2 + Lombok (@Log4j2)


### * Assumptions

- Inspections for: empty/unsafely handled catch, if (x == null), public method entry, @Service methods.

- Quick-fix (Alt+Enter) inserts a ready snippet.

- Auto-add @Log4j2 (or a log field) if missing.

- Short learning tooltips about log levels.


## 1. Error logging in `catch`

### User Story

As a developer, I want to insert a `log.error` snippet into an empty `catch` block with one click.

### System Behavior

- Detects empty `catch` blocks or those without a logger call.
- Highlights the `catch` keyword.
- Offers an intention ("Quick Fix") "Add error log".
    - When using the keyboard shortcut (e.g. Alt+Enter)
        - Automatically adds the `@Log4j2` annotation if missing.
        - Inserts snippet: `log.error("[{}] An exception occurred.", "methodName", e);`

### Acceptance Criteria

- **Scenario 1: Happy Path**
    - **Given:** No `@Log4j2` annotation and an empty `catch` block.
    - **When:** User hovers over the highlighted place - uses the "Add error log" intention.
    - **Then:** `@Log4j2` annotation is added and the `log.error` snippet inserted.
- **Scenario 2: Annotation already exists**
    - **Given:** Existing `@Log4j2` annotation and empty `catch` block.
    - **When:** User hovers over the highlighted place - uses the "Add error log" intention.
    - **Then:** `log.error` snippet is inserted; annotation is not duplicated.
- **Scenario 3: Negative case**
    - **Given:** The `catch` block already contains a logger call and `@Log4j2`.
    - **When:** User opens the file.
    - **Then:** The `catch` keyword is not highlighted; the intention is unavailable.

## 2. Warning on `null` check

### User Story

As a developer, I want to receive a suggestion to add `log.warn` when checking for `null`.

### System Behavior

- Detects conditions like `if (variable == null)`.
- Highlights the conditional expression.
- Offers an intention ("Quick Fix") "Add warning log for null check".
- When using the keyboard shortcut (e.g. Alt+Enter)
    - Automatically adds the `@Log4j2` annotation if missing.
    - Inserts snippet: `log.warn("[{}] Variable '{}' was null.", "methodName", "variableName");`

### Acceptance Criteria

- **Scenario 1: Happy Path**
    - **Given:** No `@Log4j2` annotation and block `if (user == null)`.
    - **When:** User hovers over the highlighted place - uses the "Add warning log for null check" intention.
    - **Then:** `@Log4j2` annotation is added and the `log.warn` snippet inserted with variable name "user".
- **Scenario 2: Annotation already exists**
    - **Given:** Existing `@Log4j2` annotation and block `if (user == null)`.
    - **When:** User hovers over the highlighted place - uses the "Add warning log for null check" intention.
    - **Then:** `log.warn` snippet is inserted; annotation is not duplicated.
- **Scenario 3: Negative case**
    - **Given:** Block `if (user == null)` already contains a `log.warn` call and `@Log4j2`.
    - **When:** User opens the file.
    - **Then:** The `if` condition is not highlighted; the intention is unavailable.

## 3. Tracing entry into public methods

### User Story

As a developer, I want to receive a suggestion to add `log.debug` at the beginning of public methods.

### System Behavior

- Detects public methods that do not have a `DEBUG` log in the first line of the body.
- Highlights the method name.
- Offers an intention ("Quick Fix") "Add debug log for method entry".
- When using the keyboard shortcut (e.g. Alt+Enter)
    - Inserts snippet: `log.debug("[{}] Entering method with parameters: {}", "methodName", params);` (or version without parameters).

### Acceptance Criteria

- **Scenario 1: Happy Path**
    - **Given:** Public method without a `DEBUG` log at entry.
    - **When:** User hovers over the highlighted place - uses the "Add debug log for method entry" intention.
    - **Then:** `@Log4j2` annotation is added (if missing) and the `log.debug` snippet inserted.
- **Scenario 2: Annotation already exists**
    - **Given:** Existing `@Log4j2` annotation and no `DEBUG` in the first line of the body.
    - **When:** User hovers over the highlighted place - uses the "Add error log" intention.
    - **Then:** `log.error` snippet is inserted; annotation is not duplicated.
- **Scenario 2: Negative case**
    - **Given:** Public method already contains `log.debug` in the first line and `@Log4j2`.
    - **When:** User opens the file.
    - **Then:** The method name is not highlighted; the intention is unavailable.

## 4. Logging in services

### User Story

As a developer, I want to receive a suggestion to add `log.info` at the beginning and end of methods in `@Service` classes.

### System Behavior

- Detects public methods in classes with the `@Service` annotation.
- Highlights the method name if it lacks `INFO` logs at entry and exit.
- Offers an intention ("Quick Fix") "Add info logs for service method".
- When using the keyboard shortcut (e.g. Alt+Enter) inserts two snippets:
    - At the beginning: `log.info("[{}] Starting operation with parameters: {}", "methodName", params);`
    - Before `return`: `log.info("[{}] Operation finished. Result: {}", "methodName", result);`

### Acceptance Criteria

- **Scenario 1: Happy Path**
    - **Given:** Method in `@Service` class without `INFO` logs.
    - **When:** User hovers over the highlighted place - uses the "Add info logs for service method" intention.
    - **Then:** `@Log4j2` annotation is added (if missing) and `log.info` snippets inserted at the beginning and end of the method.
- **Scenario 2: Negative case**
    - **Given:** Method in `@Service` class already contains `INFO` logs at entry and exit and `@Log4j2`.
    - **When:** User opens the file.
    - **Then:** The method name is not highlighted; the intention is unavailable.

## 5. Understanding logging levels (Learning mode)

### User Story

As a junior developer, I want to see a tooltip explaining the meaning of the logging level for each suggestion.

### System Behavior

- For each intention ("Quick Fix") to add a log (e.g. "Add error log") additional information is available.
- When hovering over the suggestion, a tooltip explaining the given logging level is displayed.

### Acceptance Criteria

- **Scenario 1: Tooltip display**
    - **Given:** Any suggestion to add a log from the plugin is visible in the intention menu.
    - **When:** User hovers over it with the mouse cursor.
    - **Then:** A tooltip appears with text explaining the corresponding logging level (`ERROR`, `WARN`, etc.).
- **Scenario 2: Negative case**
    - **Given:** The intention menu contains a suggestion from the plugin and another standard IntelliJ suggestion.
    - **When:** User hovers over the standard IntelliJ suggestion.
    - **Then:** The plugin's educational tooltip does not appear.