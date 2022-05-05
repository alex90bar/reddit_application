package com.example.reddit.service;

import com.example.reddit.dto.AuthenticationResponse;
import com.example.reddit.dto.LoginRequest;
import com.example.reddit.dto.RefreshTokenRequest;
import com.example.reddit.dto.RegisterRequest;
import com.example.reddit.exception.SpringRedditException;
import com.example.reddit.model.NotificationEmail;
import com.example.reddit.model.User;
import com.example.reddit.model.VerificationToken;
import com.example.reddit.repository.UserRepository;
import com.example.reddit.repository.VerificationTokenRepository;
import com.example.reddit.security.JwtProvider;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class AuthService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final MailService mailService;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final RefreshTokenService refreshTokenService;


  @Transactional
  public void signup(RegisterRequest registerRequest) {
    User user = new User();
    user.setUserName(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setCreated(Instant.now());
    user.setEnabled(false);
    userRepository.save(user);
    String token = generateVerificationToken(user);
    mailService.sendMail(new NotificationEmail("Please activate your account!",
        user.getEmail(), "Thank you for signin up on Reddit-Application, " +
        "please click on the below url to activate your account finally: " +
        "http://localhost:8080/api/auth/accountVerification/" + token));
  }

  private String generateVerificationToken(User user) {
    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationTokenRepository.save(verificationToken);
    return token;
  }

  public void verifyAccount(String token) {
    Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
    verificationToken.orElseThrow(() -> new SpringRedditException("Invalid token. Error!"));
    fetchUserAndEnable(verificationToken.get());
  }

  @Transactional
  private void fetchUserAndEnable(VerificationToken verificationToken) {
    String username = verificationToken.getUser().getUserName();
    User user = userRepository.findByUserName(username)
        .orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
    user.setEnabled(true);
    userRepository.save(user);
  }

  public AuthenticationResponse login(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
            loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtProvider.generateToken(authentication);
    return AuthenticationResponse.builder()
        .authenticationToken(token)
        .refreshToken(refreshTokenService.generateRefreshToken().getToken())
        .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
        .username(loginRequest.getUsername())
        .build();
  }
  public User getCurrentUser() {
    org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
        getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserName(principal.getUsername())
        .orElseThrow(() -> new SpringRedditException("User not found with name - " + principal.getUsername()));
    return user;
  }

  public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
    String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
    return AuthenticationResponse.builder()
        .authenticationToken(token)
        .refreshToken(refreshTokenRequest.getRefreshToken())
        .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
        .username(refreshTokenRequest.getUsername())
        .build();
  }
}
