package com.squirrel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    private String phone;

    private String username;

    private String password;

    private String qq;

    private String createAt;

    private Integer goodsNum;

    private Byte power;

    private String lastLogin;

    private Byte status;

   private String portrait;
}