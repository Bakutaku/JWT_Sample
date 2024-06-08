package com.example.jwt_sample.auth.bean.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic認証に必要なパラメーター
 */
@Data // getterやsetterの追加
@Builder // コンストラクタを使用しないインスタンス生成するメソッドの追加
@NoArgsConstructor // 引数を必要としないコンストラクタ追加
@AllArgsConstructor // 属性すべてを必要とするコンストラクタ
public class BasicLoginRequest {
  private String user; // ユーザ名またはメールアドレス
  private String password; // パスワード
}
