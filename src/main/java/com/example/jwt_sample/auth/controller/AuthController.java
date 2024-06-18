package com.example.jwt_sample.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt_sample.auth.bean.form.BasicLoginRequest;
import com.example.jwt_sample.auth.bean.form.CreateUserRequest;
import com.example.jwt_sample.auth.bean.form.RefreshRequest;
import com.example.jwt_sample.auth.bean.form.RefreshResponse;
import com.example.jwt_sample.auth.bean.form.TokenResponse;
import com.example.jwt_sample.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ログイン関係のコントローラ
 */
@RestController // おまじないみたいなもの
@RequestMapping("/api/auth") // URLの指定
@RequiredArgsConstructor // インスタンス自動生成
public class AuthController {

  private final AuthService service; // このコントローラー用のサービス(ここで行う処理を実装したもの)

  /**
   * basic認証を行う(パスワード認証)
   * 
   * @param req {@link BasicLoginRequest}
   */
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody BasicLoginRequest req) {
    // ログイン処理を実行 & 結果を返す
    return ResponseEntity.ok(service.basicLogin(req));
  }

  /**
   * ユーザを作成する
   * 
   * @param req {@link CreateUserRequest}
   */
  @PostMapping("/create/user")
  public ResponseEntity<TokenResponse> createUser(@RequestBody CreateUserRequest req) {
    // ユーザ作成処理を実行 & 結果を返す
    return ResponseEntity.ok(service.createUser(req));
  }

  /**
   * トークンの再発行
   * 
   * @param req {@link RefreshResponse}
   */
  @PostMapping("/refresh/token")
  public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest req) {
    // トークンの再発行 & 結果を返す
    return ResponseEntity.ok(service.refreshToken(req));
  }

}
