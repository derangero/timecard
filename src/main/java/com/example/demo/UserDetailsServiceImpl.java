package com.example.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;
    //＠Autowired アノテーションを使用して、SecurityConfig クラスで Bean 定義した PasswordEncode を取得します。
    //@Autowired
    //PasswordEncoder passwordEncoder;

    //ユーザー名を元に、データベースのユーザー情報を取得するものです。
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String sql = "SELECT * FROM employee WHERE name = ?";
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, username);
            String password = (String)map.get("password");
            Integer number = (Integer)map.get("number");

            //権限情報は、GrantedAuthority にセット
            //SimpleGrantedAuthority は GrantedAuthority インターフェイスの実装クラスであり、引数に権限情報の文字列を指定することで、GrantedAuthority のインスタンスを生成
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));

            return new UserDetailsImpl(number, username, password, authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("user not found.", e);
        }
    }
    
    @Transactional
    public void register(Integer number, String username, String password, String authority) {
        String sql = "INSERT INTO employee(number, name, password, authority) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, number, username, new BCryptPasswordEncoder().encode(password), authority);
    }
 
    public boolean isExistUser(String username) {
        String sql = "SELECT COUNT(*) FROM employee WHERE name = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[] { username });
        if (count == 0) {
            return false;
        }
        return true;
    }
}
