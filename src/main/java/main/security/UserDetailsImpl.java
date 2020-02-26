package main.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Calendar;
import java.util.Collection;


public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private byte isModerator;
    private String code;
    private Calendar regTime;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id,
                           String name,
                           String email,
                           String password,
                           byte isModerator,
                           String code,
                           Calendar regTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isModerator = isModerator;
        this.code = code;
        this.regTime = regTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    public Integer getId(){return id;}

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {return name;}

    public String getEmail(){return email;}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsModerator(byte isModerator) {
        this.isModerator = isModerator;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRegTime(Calendar regTime) {
        this.regTime = regTime;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public byte getIsModerator() {
        return isModerator;
    }

    public String getCode() {
        return code;
    }

    public Calendar getRegTime() {
        return regTime;
    }
}
