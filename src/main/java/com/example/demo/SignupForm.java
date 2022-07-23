package com.example.demo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SignupForm {

    @NotNull
    @Min(1)
    @Max(999999999)
	private Integer number;

    //	Null、空文字、空白をエラーとする
	@NotBlank
    @Size(min = 1, max = 50, message = "ユーザー名は1文字以上50文字以下で入力してください")
    private String username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

	public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}