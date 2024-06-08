package com.example.jwt_sample.auth.bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * トークン生成時の戻り値用
 */
@Data // getterやsetterの追加
@Builder // コンストラクタを使用しないインスタンス生成するメソッドの追加
@NoArgsConstructor // 引数を必要としないコンストラクタ追加
@AllArgsConstructor // 属性すべてを必要とするコンストラクタ
public class GenerateTokenResponse {
  private String token; // トークン
  private Date now; // 発行日
  private Date expires; // 有効期限
}
