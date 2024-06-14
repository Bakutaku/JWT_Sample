package com.example.jwt_sample.auth.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.jwt_sample.auth.bean.Token;
import com.example.jwt_sample.auth.bean.form.BasicLoginRequest;
import com.example.jwt_sample.auth.bean.form.CreateUserRequest;
import com.example.jwt_sample.auth.bean.form.RefreshRequest;
import com.example.jwt_sample.auth.bean.form.RefreshResponse;
import com.example.jwt_sample.auth.bean.form.TokenResponse;
import com.example.jwt_sample.auth.model.TokenHistory;
import com.example.jwt_sample.auth.model.User;
import com.example.jwt_sample.auth.repository.TokenHistoryRepository;
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
  private final TokenHistoryRepository tokenRep; // トークンテーブル

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
        .valid(true) // アカウントが有効か
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

  /**
   * トークンの再発行
   * 
   * @param req {@link RefreshRequest}
   * @return {@link RefreshResponse}
   */
  public RefreshResponse refreshToken(RefreshRequest req) {
    // トークン取得
    String reqToken = jwt.removalToken(req.getToken());

    // 復号したトークンID格納用
    String tokenID = "";

    try {
      // トークンからトークンIDを取得
      tokenID = jwt.extractTokenId(reqToken);
    } catch (TokenExpiredException e) {
      // 有効期限切れの場合、認証なしで複合 & ID取得
      tokenID = JWT.decode(reqToken).getId();
    } catch (Exception e) {
      // それ以外の例外
      // 例外を再度発生
      throw e;
    }

    // データベースから一致するレコードを取得する(無ければ例外)
    TokenHistory tokenHistory = tokenRep.findByTokenId(UUID.fromString(tokenID))
        .orElseThrow();

    // リフレッシュトークンが一致するか
    if (!tokenHistory.getRefreshToken().equals(UUID.fromString(req.getRefreshToken()))) {
      // 一致しない場合
      // 例外発生
      throw new NoSuchElementException();
    }

    // トークン生成
    Token token = jwt.createToken(tokenHistory.getUser());

    // 戻り値作成 & 値を返す
    return RefreshResponse.builder()
        .token(token.getToken()) // トークン
        .refreshToken(token.getRefreshToken()) // リフレッシュトークン
        .build(); // 作成
  }
}
