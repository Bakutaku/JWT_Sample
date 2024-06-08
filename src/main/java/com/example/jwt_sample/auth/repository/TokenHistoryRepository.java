package com.example.jwt_sample.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt_sample.auth.model.TokenHistory;

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
}
