package com.example.jwt_sample.auth.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt_sample.auth.model.TokenHistory;
import com.example.jwt_sample.auth.model.User;

import jakarta.transaction.Transactional;

/**
 * tokensテーブルのレポジトリ
 */
public interface TokenHistoryRepository extends JpaRepository<TokenHistory, Integer> {

  /**
   * トークンIDから調べる
   * 
   * @param token_id トークンID
   * @return {@link TokenHistory}
   */
  Optional<TokenHistory> findByTokenId(UUID tokenId);

  /**
   * リフレッシュトークンから調べる
   * 
   * @param refresh_token リフレッシュトークン
   * @return {@link TokenHistory}
   */
  Optional<TokenHistory> findByRefreshToken(UUID refreshToken);

  /**
   * リフレッシュトークンの有効期限切れのものを削除する
   * 
   * @param time 時刻
   * @return 削除したデータ
   */
  @Transactional // ロールバック処理をしてくれるやつ(これがないとdeleteは動かない)
  List<TokenHistory> deleteByRefreshExpiresDateBefore(Date time);

  /**
   * ユーザが一致するレコードを削除する
   * 
   * @param user
   * @return
   */
  @Transactional // ロールバック処理をしてくれるやつ(これがないとdeleteは動かない)
  List<TokenHistory> deleteByUser(User user);
}
