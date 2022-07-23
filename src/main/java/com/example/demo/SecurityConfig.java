package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	//DI
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	//「全てのリクエストの承認は、ログインしていることが条件」
    	//authorizeHttpRequests
        http.authorizeRequests()  // 認可の設定
        	// "/" がなければ"/login"
	        .antMatchers("/login", "/signup", "/error", "/login-error").permitAll()
	        .anyRequest().authenticated()  // それ以外は全て認証無しの場合アクセス不許可
	        .and()
        //loginPage メソッドでログイン画面のURLを /login と指定し
        //defaultSuccessUrl メソッドで認証後にリダイレクトされるページを指定し、
        //permitAll メソッドで全てのユーザーにアクセスの許可を与えています。
        .formLogin()  // ログイン設定
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .permitAll()
            .and()
        .logout()   // ログアウト設定
            .permitAll();

        //権限に応じたアクセス制限を行う場合
        //http.authorizeRequests()
        //.antMatchers("/admin/**").hasRole("ADMIN")
        //.anyRequest().hasRole("USER");
        
        //CSRF対策を無効にする場合
        //http.csrf().disable()
        //.authorizeRequests().anyRequest().authenticated();
    }
}