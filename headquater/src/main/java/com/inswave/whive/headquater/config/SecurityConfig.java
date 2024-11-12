package com.inswave.whive.headquater.config;

import com.inswave.whive.headquater.interceptor.CertificationInterceptor;
import com.inswave.whive.headquater.security.CustomAccessDeniedHandler;
import com.inswave.whive.headquater.security.CustomAuthenticationEntryPoint;
import com.inswave.whive.headquater.security.JwtAuthenticationFilter;
import com.inswave.whive.headquater.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private final TokenProvider tokenProvider;

    private CertificationInterceptor interceptor;
    private String loginUrl = "/websquare/websquare.html?w2xPath=/login.xml";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost");
        configuration.addAllowedOrigin("https://dev.w-hive.io");
        configuration.addAllowedOrigin("http://dev.w-hive.io");
        configuration.addAllowedOrigin("http://w-hive.io");
        configuration.addAllowedOrigin("https://w-hive.io");
        configuration.addAllowedOrigin("http://w-hive.com");
        configuration.addAllowedOrigin("https://w-hive.com");
        configuration.addAllowedOrigin("http://www.w-hive.com");
        configuration.addAllowedOrigin("https://www.w-hive.com");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true); // configuration.addAllowedOrigin("*"); 와 동시에 못씀
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**","/favicon.ico");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
                //.cors()
                .cors().configurationSource(corsConfigurationSource()) // TODO : cors 설정 비활성화...
                .and()
                .csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증할것이므로 세션필요없으므로 생성안함.
                .and()
                // .authorizeRequests()
//                .addFilterBefore(new CustomBeforeFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests().antMatchers("/").permitAll() // .authorizeRequests().antMatchers("/").permitAll()
                .antMatchers("/*.html").permitAll()
                .antMatchers("/*.xml").permitAll()
                .antMatchers("/whive.mp4").permitAll()
                .antMatchers("/_wpack_/**").permitAll()
                .antMatchers("/langpack/**").permitAll()
                .antMatchers("/websquare/**").permitAll()
                .antMatchers("/pricing/**").permitAll()
                .antMatchers("/ui/comn/AuthTokenReCapTcha.html").permitAll()
                .antMatchers("/pricing/cm/images/base/**").permitAll()
                .antMatchers("/product/**").permitAll()
                .antMatchers("/cm/css/**").permitAll()
                .antMatchers("/cm/font/**").permitAll()
                .antMatchers("/cm/images/**").permitAll()
                .antMatchers("/cm/images/base/**").permitAll()
                .antMatchers("/data/support/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/xml/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/.well-known/acme-challenge/**").permitAll()
                .antMatchers(loginUrl).permitAll()
//                .antMatchers("/upload/**").permitAll()
                .antMatchers("/whivebranch").permitAll()
                .antMatchers("/websquare/websquare.html?w2xPath=/index.xml").permitAll()
                .antMatchers(HttpMethod.POST,"/api/account/signUp/checkEmail").permitAll() //TODO : email api 허용 추후 운영에는 주석 처리해야함
//                .antMatchers("/exception/**").permitAll()
//                .antMatchers("/favicon.ico").permitAll()
                .antMatchers(HttpMethod.POST,"/manager/account/signUp/**").permitAll()
                .antMatchers(HttpMethod.POST, "/manager/member/search/**").permitAll()
                .antMatchers(HttpMethod.POST, "/manager/account/resign/**").permitAll()
                .antMatchers(HttpMethod.POST,"/manager/member/**").permitAll()
                .antMatchers(HttpMethod.POST,"/manager/member/update/userAuthToken").permitAll()
                .antMatchers(HttpMethod.POST,"/manager/member/login").permitAll()
                .antMatchers(HttpMethod.POST,"/manager/**/**").permitAll()
                .antMatchers(HttpMethod.GET,"/manager/**/**").permitAll()
                .antMatchers(HttpMethod.POST,"/builder/**/**").permitAll()
                .antMatchers(HttpMethod.POST,"/builder/build/history/uploadSetupFileToAwsS3Server").permitAll()
                .antMatchers(HttpMethod.GET,"/builder/build/history/CheckAuthPopup/**").permitAll()
                .antMatchers(HttpMethod.GET,"/builder/build/history/getInstallUrl/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS,"/manager/member/*").permitAll()
                .antMatchers(HttpMethod.GET, "/error").permitAll()
                .anyRequest().fullyAuthenticated().and().httpBasic().and()
//                .hasRole("ADMIN")
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())            // 인증 오류 발생 시 처리를 위한 핸들러 추가
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())  // 인증 오류 발생 시 처리를 위한 핸들러 추가
                .and().csrf().disable()
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                // 기본 form login을 사용하지 않으므로 주석처리
//                .formLogin()
//                .loginPage("/websquare/websquare.html?w2xPath=/login.xml")
//                .loginProcessingUrl("/manager/member/login")
//                .and()
                .logout()
                .logoutSuccessUrl("/") // 로그아웃 성공 시, 이동할 URL - post 요청에서만 동작한다
                .logoutRequestMatcher(new AntPathRequestMatcher("/manager/member/logout")) // 로그아웃 URL, GET 방식으로 요청해도, POST 방식으로 처리
                .permitAll()
                .deleteCookies("JSESSIONID", "accessToken", "refreshToken") // 로그아웃 시, JSESSIONID, accessToken, refreshToken 삭제
                .invalidateHttpSession(true) // 로그아웃 시, 세션 종료
                .clearAuthentication(true) // 로그아웃 시, 권한 제거
                .and()
                .headers().frameOptions().sameOrigin();
    }
}
