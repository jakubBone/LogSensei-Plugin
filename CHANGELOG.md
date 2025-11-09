# Changelog – LogSensei

All notable changes to this project will be documented in this file.  
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),  
and this project adheres to [Semantic Versioning](https://semver.org/).


## [Unreleased]
### Added
- Basic project structure
- Migration `build.gradle.kts` → `build.gradle`

## [0.3.0] - 2025-11-09
### Added

**New feature:** Detect public service method with missing logs and suggest `INFO` level logs

- **Inspection 5**: service method with missing enter or exit logging
- **QuickFix 5**: add enter/exit `log.info(...)` and `@Log4j2` annotation 


**New feature:** Detect early return statement with missing log and suggest DEBUG level

- **Inspection 4**: early return statements with missing log
- **QuickFix 4**: add `log.debug(...)` and `@Log4j2` annotation


**New feature:** In-IDE log level education   

- **LogEducationNotifier** shows educational popups about proper log level usage `ERROR`, `WARN` and `INFO`


### Changed

- Refactored core inspection logic for better readability 
- Simplified conditions, unified naming, and reduced code nesting)
- **Extracted shared logic** into utility classes:
  - `LogDetectionUtils` – common log detection helpers  
  - `LogSenseiUtils` – unified method for adding `@Log4j2` annotation  
  - `LogSenseiConstants` – centralized log message patterns
  
## [0.2.2] - 2025-10-15
### Added

**Main feature:** Detect `INFO` level logs in loops and suggest change to `DEBUG` level 

- **Inspection 3**: high-frequency logging in loops
- **QuickFix 3**: change `log.info(...)` to `log.debug(...)` and add `@Log4j2` annotation 

## [0.2.1] - 2025-10-06
### Fixed
- `NullCheckLogQuickFix` now properly handles single-statement `if` blocks without braces
- Example: `if (x == null) return;` now correctly transforms to `if (x == null) { log.warn(...); return; }`

## [0.2.0] - 2025-10-01
### Added

**Main feature:** Detect null checks with missing logs and suggest `WARN` level log

- **Inspection 2**: null check with missing log
- **QuickFix 2**: add `log.warn(...)` and `@Log4j2` annotation 
- Context-aware warning messages with method and variable names

## [0.1.0] - 2025-09-24 - Initial Release
### Added

**Main feature:** Detect catch blocks with missing logs and suggest `ERROR` level log

- Basic project structure and build configuration
- **Inspection 1**: catch block mith missing log 
- **QuickFix 1**: add `log.error(...)` and `@Log4j2` annotation 
