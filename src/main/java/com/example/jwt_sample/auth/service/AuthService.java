package com.example.jwt_sample.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt_sample.auth.bean.Token;
import com.example.jwt_sample.auth.bean.form.BasicLoginRequest;
import com.example.jwt_sample.auth.bean.form.CreateUserRequest;
import com.example.jwt_sample.auth.bean.form.TokenResponse;
import com.example.jwt_sample.auth.model.User;
import com.example.jwt_sample.auth.repository.UserRepository;
import com.example.jwt_sample.auth.role.AuthRole;

import lombok.RequiredArgsConstructor;

/**
 * AuthControllerの処理
 */
@Service // おまじないみたいなもの
@RequiredArgsConstructor // インスタンスの自動生成
public class AuthService {

  private final UserRepository userRep; // ユーザテーブル

  private final AuthenticationManager authManager; // basic認証に使用するもの
  private final PasswordEncoder passwordEncoder; // パスワードのハッシュ化用
  private final JwtService jwt; // JWT操作用

  /**
   * basic認証
   * 
   * @param req {@link BasicLoginRequest}
   * @return {@link Token}
   */
  public TokenResponse basicLogin(BasicLoginRequest req) {

    // basic認証を行う
    authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUser(), req.getPassword()));

    // ユーザ情報取得
    User user = userRep.findByName(req.getUser()) // ユーザ名から探す
        .orElseGet(() -> userRep // 無ければ..
            .findByEmail(req.getUser()) // メールアドレスから探す
            .orElseThrow()); // 見つからなかったら例外を発生

    // トークン発行
    Token token = jwt.createToken(user);

    // 作成 & 結果を返す
    // 何故Tokenと同じ内容なのに別のクラスに分けているか
    // リクエストに対するレスポンスとして使用ため用途が違うので念のために分けています。
    return TokenResponse.builder()
        .token(token.getToken()) // トークン
        .refreshToken(token.getRefreshToken()) // リフレッシュトークン
        .build(); // 作成
  }

  /**
   * ユーザ登録
   * 
   * @param req {@link CreateUserRequest}
   * @return {@link Token}
   */
  public TokenResponse createUser(CreateUserRequest req) {

    // ユーザ情報作成
    User user = User.builder()
        .name(req.getName()) // 名前
        .email(req.getEmail()) // メールアドレス
        .password(passwordEncoder.encode(req.getPassword())) // パスワード
        .role(AuthRole.USER) // ロール //TODO 将来的にはメールアドレス認証などを行うためTEMPに変更する
        .build(); // 作成

    // データベースに登録
    userRep.save(user);

    // トークン発行
    Token token = jwt.createToken(user);

    // 作成 & 結果を返す
    return TokenResponse.builder()
        .token(token.getToken()) // トークン
        .refreshToken(token.getRefreshToken()) // リフレッシュトークン
        .build(); // 作成
  }

}
