package taskmanager.exceptions;

public class AuthenticationFailed extends Exception {
  private int errorCode;

  public AuthenticationFailed(int errorCode, String message) {
      super(message);
      this.errorCode = errorCode;
  }

  public int getErrorCode() {
      return errorCode;
  }
}
