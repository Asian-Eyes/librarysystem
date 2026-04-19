package org.example.Service;

import java.time.LocalDateTime;

import org.example.Model.UserModel;
import org.example.Repository.AuthRepo;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
   private final AuthRepo authRepo = new AuthRepo();

   public boolean signUp(String username, String email, String password, String role) {
      if (isBlank(username) || isBlank(email) || isBlank(password) || isBlank(role)) {
         return false;
      }

      if (authRepo.existsByUsername(username) || authRepo.existsByEmail(email)) {
         return false;
      }

      UserModel user = new UserModel();
      user.setUsername(username.trim());
      user.setEmail(email.trim());
      user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
      user.setRole(role.trim());
      user.setCreatedAt(LocalDateTime.now());

      return authRepo.createUser(user);
   }

   public UserModel signIn(String usernameOrEmail, String password) {
      if (isBlank(usernameOrEmail) || isBlank(password)) {
         return null;
      }

      UserModel user = authRepo.findByUsernameOrEmail(usernameOrEmail.trim());
      if (user == null) {
         return null;
      }

      return BCrypt.checkpw(password, user.getPasswordHash()) ? user : null;
   }

   private boolean isBlank(String value) {
      return value == null || value.trim().isEmpty();
   }
}
