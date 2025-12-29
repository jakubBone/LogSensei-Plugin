package org.slf4j;

public interface Logger {
  void error(String message, Throwable t);
  void warn(String message);
  void info(String message);
  void debug(String message);
}