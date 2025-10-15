# Changelog – LogSensei

All notable changes to this project will be documented in this file.  
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),  
and this project adheres to [Semantic Versioning](https://semver.org/).


## [Unreleased]
### Added
- Basic project structure
- Migration build.gradle.kts → build.gradle

## [0.2.2] - 2025-10-15
### Added

Main feature: Detecting INFO logging level in loops and suggest change to DEBUG 

- Inspection 3: high-frequency logging in loops
- QuickFix 3: change log.info(...) to log.debug(...) and add @Log4j2 annotation 

## [0.2.1] - 2025-10-06
### Fixed
- NullCheckLogQuickFix now properly handles single-statement if blocks without braces
- Example: `if (x == null) return;` now correctly transforms to `if (x == null) { log.warn(...); return; }`

## [0.2.0] - 2025-10-01
### Added

Main feature: WARN level logging for null checks

- Inspection 2: null check without WARN log
- QuickFix 2: add log.warn(...) and @Log4j2 annotation 
- Context-aware warning messages with method and variable names

## [0.1.0] - 2025-09-24 - Initial Release
### Added

Main feature: Add ERROR level logging in catch blocks

- Basic project structure and build configuration
- Inspection 1: catch block without ERROR log 
- QuickFix 1: add log.error(...) and @Log4j2 annotation 
- Automatic Log4j2 import 



