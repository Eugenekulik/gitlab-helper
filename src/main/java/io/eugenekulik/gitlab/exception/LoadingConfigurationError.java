package io.eugenekulik.gitlab.exception;

public class LoadingConfigurationError extends RuntimeException {


  public LoadingConfigurationError(String message) {
    super(message);
  }

  public LoadingConfigurationError(String message, Throwable cause) {
    super(message, cause);
  }

}
