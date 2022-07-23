package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class TestController {
	//DI
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
	
    @GetMapping
    public String index () {
        return "index";
    }

    //login にアクセスすれば、login.html を表示する
    @GetMapping("/login")
    public String login () {
        return "login";
    }
    //「ユーザー登録」ページを表示するだけのものです。
    @GetMapping("/signup")
    public String newSignup(SignupForm signupForm) {
        return "signup";
    }

    @PostMapping("/signup")
    //＠Validated を加えて、バリデーションチェックを有効に
    //BindingResult result を引数として追加しています。ここに、エラー情報が格納されます。
    public String signup(@Validated SignupForm signupForm, BindingResult result, Model model,
    		 HttpServletRequest request) {
        if (result.hasErrors()) {
        	///signup のページを表示
            return "signup";
        }

        try {
            userDetailsServiceImpl.register(signupForm.getNumber(), signupForm.getUsername(), signupForm.getPassword(), "ROLE_USER");
        } catch (DataAccessException e) {
        	model.addAttribute("signupError", "ユーザー名 " + signupForm.getUsername() + "は既に登録されています");
            return "signup";
        }
        
        //セキュリティ情報（コンテキスト）を定義するインターフェース
        SecurityContext context = SecurityContextHolder.getContext();
        //現在アプリケーションとやり取りをしているユーザーに関する情報
        //Authentication からユーザー名や権限情報（ロール）なども取得することができます
        Authentication authentication = context.getAuthentication();

        //匿名ユーザー（anonymousUser）かどうか
        if (authentication instanceof AnonymousAuthenticationToken == false) {
        	//現在のスレッドからコンテキスト値をクリアする
            SecurityContextHolder.clearContext();
        }
        
        try {
        	//HttpServletRequest オブジェクトの login メソッドを使用して、新たに登録されたユーザー名とパスワードでログイン処理
            request.login(signupForm.getUsername(), signupForm.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        
        return "redirect:/";
    }
}