package com.example.jwt_sample.auth.service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwt_sample.auth.bean.GenerateTokenResponse;
import com.example.jwt_sample.auth.bean.Token;
import com.example.jwt_sample.auth.model.TokenHistory;
import com.example.jwt_sample.auth.model.User;
import com.example.jwt_sample.auth.repository.TokenHistoryRepository;
import com.example.jwt_sample.auth.repository.UserRepository;
import com.example.jwt_sample.auth.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * JWTの認証処理などを行うクラス
 */
@Service // おまじないみたいなもの
@RequiredArgsConstructor // Beanのインスタンス自動生成用
public class JwtService {

  @Value("${jwt.security.storage.key}")
  private String STORAGE_KEY; // 保存場所

  @Value("${jwt.security.start.text}")
  private String TOKEN_START; // トークンの始まりの文字

  // データベース操作用
  private final UserRepository userRep; // usersテーブル
  private final TokenHistoryRepository tokenRep; // tokensテーブル

  private final JwtUtil jwt; // jwt操作用

  /**
   * トークン生成
   * 
   * @param userId 発行者
   * @return トークン
   */
  public Token createToken(User user) {

    // トークンID作成
    UUID token_id = UUID.randomUUID();

    // トークン生成
    GenerateTokenResponse token = jwt.generateToken(user.getId().toString(), token_id);

    // リフレッシュトークン作成
    UUID refreshToken = UUID.randomUUID();

    // レコード作成
    TokenHistory recode = TokenHistory.builder()
        .tokenId(token_id) // トークンID
        .refreshToken(refreshToken) // リフレッシュトークン
        .issuedDate(token.getNow()) // 発行日
        .expiresDate(token.getExpires()) // 有効期限
        .build();

    // データベースに保存
    tokenRep.save(recode);

    // 返り値作成
    Token res = Token.builder()
        .token(token.getToken())
        .refreshToken(refreshToken)
        .build();

    return res;
  }

  /**
   * トークンからユーザIDを取得する
   * 
   * @param token
   * @return
   */
  public String extractUserId(String token) {
    // 復号
    DecodedJWT deToken = jwt.decodeJwt(token);

    // ユーザデータを返す
    return deToken.getSubject();
  }

  /**
   * リクエストからトークンを取り出す
   * 
   * @param req {@link HttpServletRequest}
   * @return JWTトークン
   */
  public String getToken(HttpServletRequest req) throws Exception {

    // ヘッダーからトークン取得
    String token = req.getHeader(STORAGE_KEY);

    if (token != null && token.startsWith(TOKEN_START)) {
      // 先頭の文字を取り外す
      token = token.substring(TOKEN_START.length());

      // 結果を返す
      return token;
    }

    // 結果を返す
    return null;
  }

  /**
   * JWTトークンの認証
   * 
   * @param token トークン
   * @return 結果
   */
  public UserDetails authenticate(String token) throws JWTVerificationException, NoSuchElementException {

    // トークン復号
    DecodedJWT deToken = jwt.decodeJwt(token);

    // ユーザID取得
    UUID userID = UUID.fromString(deToken.getSubject());

    // ユーザがいるか検証(いない場合は例外発生)
    UserDetails user = userRep.findById(userID)
        .orElseThrow();

    // トークンID取得
    UUID tokenID = UUID.fromString(deToken.getId());

    // 発行されたトークンか検証(無ければ例外発生)
    TokenHistory tokenHistory = tokenRep.findByTokenId(tokenID)
        .orElseThrow();

    // 現在の時刻取得
    Date now = new Date();

    // 有効期限ないか
    if (now.after(tokenHistory.getExpiresDate())) {
      // 有効期限切れ

      // 例外を発生させる
      throw new JWTVerificationException("This token has expired");

    }

    // 結果を返す
    return user;
  }
}
