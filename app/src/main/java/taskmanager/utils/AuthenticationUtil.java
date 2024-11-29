package taskmanager.utils;

import taskmanager.entities.User;
import taskmanager.services.SessionManager;

public class AuthenticationUtil {
  public User currentUser() {
    String token = SessionManager.loadSessionToken();

    Long userId = JwtUtil.parseToken(token);

    return User.findById(userId);
  }

  public void signIn(Long userId) {
    String token = JwtUtil.generateToken(userId);
    SessionManager.saveSessionToken(token);
  }
}
