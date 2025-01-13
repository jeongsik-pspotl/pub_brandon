package com.pspotl.sidebranden.common.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@NoArgsConstructor
@EqualsAndHashCode(of={"email"})
@Entity
@Data
@Table(name="member")
public class Member implements UserDetails {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="name")
    private String name;

    @Column(name="username")
    private String username;

    @Column(name="team")
    private String team;

    @Column(name="company")
    private String company;


    @CreationTimestamp
    @Column(name="created_date" , updatable = false)
    private LocalDateTime created_date;

    @UpdateTimestamp
    @Column(name="updated_date")
    private LocalDateTime updated_date;

    @Column(name="last_login_date")
    private String last_login_date;

    public void setCreatedDateNow() {
        this.created_date = LocalDateTime.now();
    }

    public void setUpdatedDateNow() {
        this.updated_date = LocalDateTime.now();
    }

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "account_locked")
    private boolean accountNonLocked;

    @Column(name = "account_expired")
    private boolean accountNonExpired;

    @Column(name = "credentials_expired")
    private boolean credentialsNonExpired;



    // 계정이름을 리턴한다.
    @JsonIgnore
    public String getUsername() {return username;}

    @JsonProperty
    public void setUsername(String username) {
        this.username = username;
    }

    // 계정에 비밀번호를 리턴한다.
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    // 사용가능한 계정 인지?
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // 계정이 만료되지 않았는지?
    @Override
    public boolean isAccountNonExpired() {
        return !accountNonExpired;
    }

    // 계정에 패스워드가 만료되지않았는지?
    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsNonExpired;
    }

    // 계정이 잠겨있지않은지?
    @Override
    public boolean isAccountNonLocked() {
        return !accountNonLocked;
    }

    /*
     * 계정이 가지고 있는 권한목록을 리턴
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

//        roles.forEach(r -> {
//            authorities.add(new SimpleGrantedAuthority(r.getName()));
//            r.getPermissions().forEach(p -> {
//                authorities.add(new SimpleGrantedAuthority(p.getName()));
//            });
//        });
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));

        return authorities;
    }
}