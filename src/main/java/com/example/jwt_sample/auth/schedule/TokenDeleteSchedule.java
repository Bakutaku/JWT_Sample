package com.example.jwt_sample.auth.schedule;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.jwt_sample.auth.repository.TokenHistoryRepository;

import lombok.RequiredArgsConstructor;

/**
 * いらないトークンの履歴を削除する
 */
@Component // おまじないみたいなもの
@RequiredArgsConstructor // インスタンスの自動生成
public class TokenDeleteSchedule {

  private final TokenHistoryRepository tokenRep; // トークンテーブル

  /**
   * トークンの定期削除
   */
  @Scheduled(cron = "${jwt.security.token.delete.time.cron}")
  public void doTokenDelete() {
    // 現在の時刻取得
    Date now = new Date();

    // データベースの有効期限切れの物を削除
    tokenRep.deleteByRefreshExpiresDateBefore(now);
  }
}