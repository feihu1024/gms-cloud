package com.ruoyi.auth.form;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginBody
{
    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "用户密码")
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
