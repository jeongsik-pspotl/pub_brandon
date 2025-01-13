package com.pspotl.sidebranden.manager.util;

import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberUserCreate;
import com.pspotl.sidebranden.manager.config.EmailConfig;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Component
public class EmailUtil {

    @Autowired
    EmailConfig emailConfig;

    public static final int noOfQuickServiceThreads = 20;

    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_TTL_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";

    private static final String UTF_8 = "utf-8";

    private static final String MAIL_PERSONAL_NAME = "W-Hive CS 담당";

    private ScheduledExecutorService quickService = Executors.newScheduledThreadPool(noOfQuickServiceThreads);

    @Async
    public void sendEmail() {
        Properties props = System.getProperties();

        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // session.setDebug(true); //for debug

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            mailMessage.addRecipients(Message.RecipientType.TO,"pspotl87@inswave.com");
            mailMessage.setSubject("W-Hive 회원 가입 테스트");

            String msg="";
            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 W-Hive 가입 창이 있는 브라우저 창에 입력하세요.</p>";
            msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
            msg += "code";
            msg += "</td></tr></tbody></table></div>";
            msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">W-Hive </a>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));


            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });


        } catch (MessagingException | UnsupportedEncodingException e) {
                
             
            Exception ex = e;

            if (ex instanceof SendFailedException) {
                SendFailedException sfex = (SendFailedException) ex;
                Address[] invalid = sfex.getInvalidAddresses();
                if (invalid != null) {
                    System.out.println(" ** Invalid Addresses");
                    for (int i = 0; i < invalid.length; i++)
                    System.out.println(" " + invalid[i]);

                }
            }
        }
    }

    @Async
    public String sendCodeDataToEmail(MemberLogin member) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);

        try {
            String createKeyCode = createKey();

            mailMessage.addRecipients(Message.RecipientType.TO,member.getEmail());
            mailMessage.setSubject("W-Hive E-Mail 본인 인증");

            String msg="";
            msg += "<!DOCTYPE html>";
            msg += "<html lang=\"en\">";
            msg += "<head>";
            msg += "<meta charset=\"UTF-8\">";
            msg += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
            msg += "<title>Document</title>";
            msg += "</head>";
            msg += "<body>";
            msg += "<h1 style=\"width:140px;height:31px;background:url("+emailConfig.getWhiveDomaim()+"/cm/images/email/w-hive_logo.png) no-repeat;\" title=\"W-hive Logo\"></h1>";
            msg += "<table style=\"margin:0 auto;padding-bottom:40px;border-bottom:1px solid #602B8A;width:400px;color:#602B8A;\">";
            msg += "<tr>";
            msg += "<th>";
            msg += "<img style=\"width:35px;height:31px;\"src=\""+emailConfig.getWhiveDomaim()+"/cm/images/email/ico_email_chk.png\" title=\"이메일 주소 확인 아이콘\"></img>";
            msg += "<p style=\"padding-bottom:40px;border-bottom:1px solid #602B8A; color:#602B8A;font-size:20px;font-weight:bold;\">이메일 주소 확인</p>";
            msg += "</th>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td>";
            msg += "<p style=\"color:#3E3E3E;text-align:center;font-size:15px;font-weight:bold;\">아래 확인 코드를 W-Hive 가입 창에 입력하세요.</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<p style=\"display:inline-block;margin:10px auto 30px;padding:24px 62px;background:#F5F5F7;color:#3E3E3E;text-align:center;font-size:19px;font-weight:bold;\">"+createKeyCode+"</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<a href=\"" + emailConfig.getWhiveDomaim() + "\">";
            msg += "<button style=\"padding:13px 24px;border-radius:21px;border:0;width:100%;background: #602B8A;color:#fff;cursor: pointer;\">W-Hive 홈페이지로 이동</button>";
            msg += "</a>";
            msg += "</td>";
            msg += "</tr>";
            msg += "</table>";
            msg += "<footer style=\"width: 400px; margin:24px auto 0;padding:16px;background:#F5F5F7\">";
            msg += "<p style=\"margin-top:0;font-size:14px;font-weight:bold;\">인스웨이브시스템즈</p>";
            msg += "<span style=\"display:block;margin-bottom:16px;color:#959595;font-size:13px;\">서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>C-12F, 247, Gonghang-Daero, Gangseo-Gu, Seoul, Korea</span>";
            msg += "<div>";
            msg += "<a href=\"tel:02-2082-1400\" style=\"border-bottom:1px solid #959595;color:#959595;font-size:13px\">8202-2082-1400</a>";
            msg += "<a href=\"mailto:whivehelp@inswave.com\" style=\"border-bottom:1px solid #959595;margin-left:18px;color:#959595;font-size:13px\">whivehelp@inswave.com</a>";
            msg += "</div>";
            msg += "<a href=\"https://www.inswave.com/\">";
            msg += "<img style=\"margin-top:24px;width:130px;height:24px;\"src=\""+emailConfig.getWhiveDomaim()+"/cm/images/email/inswave_logo.png\" title=\"인스웨이브 로고\"></img>";
            msg += "</a>";
            msg += "</footer>";
            msg += "</body>";
            msg += "</html>";
//            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 W-Hive 가입 창에 입력하세요.</p>";
//            msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
//            msg += createKeyCode;
//            msg += "</td></tr></tbody></table></div>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));


            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });
            return createKeyCode;
        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
            return "";
        }

    }

    // 가입 완료 인증 이후 로그인 화면 이동
    // 내부적으로는 code -> Y 처리
    @Async
    public void sendSignResultEmailToLoginPage(MemberUserCreate memberUserCreate) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,memberUserCreate.getEmail());
            mailMessage.setSubject("W-Hive 회원 가입 완료");

            String msg="";
            msg += "<!DOCTYPE html>";
            msg += "<html lang=\"ko\">";
            msg += "<head>";
            msg += "<meta charset=\"UTF-8\">";
            msg += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
            msg += "<title>W-Hive 회원 가입 완료</title>";
            msg += "</head>";
            msg += "<body>";
            msg += "<h1 style=\"width:140px;height:31px;background:url(" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_logo.png) no-repeat; title=\"W-hive Logo\"></h1>";
            msg += "<table style=\"margin:0 auto;padding-bottom:40px;border-bottom:1px solid #602B8A;width:400px;color:#602B8A;\">";
            msg += "<tr>";
            msg += "<th>";
            msg += "<img style=\"width:35px;height:31px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_email_join.png\" title=\"W-Hive 회원 가입 완료 아이콘\"></img>";
            msg += "<p style=\"padding-bottom:40px;border-bottom:1px solid #602B8A; color:#602B8A;font-size:20px;font-weight:bold;\">W-Hive 회원 가입 완료</p>";
            msg += "</th>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td>";
            msg += "<p style=\"color:#3E3E3E;text-align:center;font-size:15px;\">" + memberUserCreate.getEmail().toString() + "님</p>";
            msg += "<p style=\"margin-bottom:60px;color:#3E3E3E;text-align:center;font-size:15px;\">W-Hive 서비스 가입이 완료 되었습니다.<br/>하단에 로그인 링크로 이동하시기 바랍니다.</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<a href=\"" + emailConfig.getWhiveDomaim() + "\">";
            msg += "<button style=\"padding:13px 24px;border-radius:21px;border:0;width:100%;background: #602B8A;color:#fff;cursor: pointer;\">W-Hive 로그인 페이지로 이동</button>";
            msg += "</a>";
            msg += "</td>";
            msg += "</tr>";
            msg += "</table>";
            msg += "<footer style=\"width: 400px; margin:24px auto 0;padding:16px;background:#F5F5F7\">";
            msg += "<p style=\"margin-top:0;font-size:14px;font-weight:bold;\">인스웨이브시스템즈</p>";
            msg += "<span style=\"display:block;margin-bottom:16px;color:#959595;font-size:13px;\">서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>C-12F, 247, Gonghang-Daero, Gangseo-Gu, Seoul, Korea</span>";
            msg += "<div>";
            msg += "<a href=\"tel:02-2082-1400\" style=\"color:#959595;font-size:13px\">8202-2082-1400</a>";
            msg += "<a href=\"mailto:whivehelp@inswave.com\" style=\"margin-left:18px;color:#959595;font-size:13px\">whivehelp@inswave.com</a>";
            msg += "</div>";
            msg += "<a href=\"https://www.inswave.com/\">";
            msg += "<img style=\"margin-top:24px;width:130px;height:24px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/inswave_logo.png\" title=\"인스웨이브 로고\"></img>";
            msg += "</a>";
            msg += "</footer>";
            msg += "</body>";
            msg += "</html>";

//            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 회원 가입 완료</h1>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> "+memberUserCreate.getUser_name() +"님. W-Hive 서비스 가입이 완료 되었습니다. 하단에 로그인 링크로 이동하시기 바랍니다.</p>";
//            msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));


            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    // 아이디 찾기 기능
    @Async
    public void sendIDCheckToEmail(Map<String, Object>  payloadMap) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,payloadMap.get(PayloadKeyType.email.name()).toString());
            mailMessage.setSubject("W-Hive 아이디 찾기");

            String msg="";
            msg += "<!DOCTYPE html>";
            msg += "<html lang=\"ko\">";
            msg += "<head>";
            msg += "<meta charset=\"UTF-8\">";
            msg += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
            msg += "<title>Document</title>";
            msg += "</head>";
            msg += "<body>";
            msg += "<h1 style=\"width:140px;height:31px;background:url(" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_logo.png) no-repeat;\" title=\"W-hive Logo\"></h1>";
            msg += "<table style=\"margin:0 auto;padding-bottom:40px;border-bottom:1px solid #602B8A;width:400px;color:#602B8A;\">";
            msg += "<tr>";
            msg += "<th>";
            msg += "<img style=\"width:35px;height:31px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/ico_email_chk.png\" title=\"W-Hive 아이디 찾기 아이콘\"></img>";
            msg += "<p style=\"padding-bottom:40px;border-bottom:1px solid #602B8A; color:#602B8A;font-size:20px;font-weight:bold;\">W-Hive 아이디 찾기</p>";
            msg += "</th>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<p style=\"margin-bottom:60px;color:#3E3E3E;text-align:center;font-size:15px;\">고객님 아이디는 <span style=\"color:#602B8A;font-weight:bold;\">"+ payloadMap.get("user_login_id").toString() +"</span> 입니다.<br/>하단 링크를 통해 이동하시면 됩니다.</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<a href=\"" + emailConfig.getWhiveDomaim() + "\">";
            msg += "<button style=\"padding:13px 24px;border-radius:21px;border:0;width:100%;background: #602B8A;color:#fff;cursor: pointer;\">W-Hive 로그인 페이지로 이동</button>";
            msg += "</a>";
            msg += "</td>";
            msg += "</tr>";
            msg += "</table>";
            msg += "<footer style=\"width: 400px; margin:24px auto 0;padding:16px;background:#F5F5F7\">";
            msg += "<p style=\"margin-top:0;font-size:14px;font-weight:bold;\">인스웨이브시스템즈</p>";
            msg += "<span style=\"display:block;margin-bottom:16px;color:#959595;font-size:13px;\">서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>C-12F, 247, Gonghang-Daero, Gangseo-Gu, Seoul, Korea</span>";
            msg += "<div>";
            msg += "<a href=\"tel:02-2082-1400\" style=\"color:#959595;font-size:13px\">8202-2082-1400</a>";
            msg += "<a href=\"mailto:whivehelp@inswave.com\" style=\"margin-left:18px;color:#959595;font-size:13px\">whivehelp@inswave.com</a>";
            msg += "</div>";
            msg += "<a href=\"https://www.inswave.com/\">";
            msg += "<img style=\"margin-top:24px;width:130px;height:24px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/inswave_logo.png\" title=\"인스웨이브 로고\"></img>";
            msg += "</a>";
            msg += "</footer>";
            msg += "</body>";
            msg += "</html>";

//            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 아이디 찾기</h1>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> 고객님 아이디는 \""+payloadMap.get("user_login_id").toString()+"\" 입니다.</p>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> 하단에 로그인 링크로 이동하시면 됩니다.</p>";
////            msg += "<a href=\"http://localhost:9095\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";
//            msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";  // TODO 하드코딩 수정하기

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    // 비밀번호 재설정
    @Async
    public void sendPasswordResetToEmail(Map<String, Object>  payloadMap) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,payloadMap.get(PayloadKeyType.email.name()).toString());
            mailMessage.setSubject("W-Hive 비밀번호 재설정");

            String msg="";
            msg += "<!DOCTYPE html>";
            msg += "<html lang=\"ko\">";
            msg += "<head>";
            msg += "<meta charset=\"UTF-8\">";
            msg += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
            msg += "<title>Password Reset</title>";
            msg += "</head>";
            msg += "<body>";
            msg += "<h1 style=\"width:140px;height:31px;background:url(" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_logo.png) no-repeat;\" title=\"W-Hive logo\"></h1>";
            msg += "<table style=\"margin:0 auto; padding-bottom:40px; border-bottom: 1px solid #602B8A; width: 400px; color: #602B8A;\">";
            msg += "<tr>";
            msg += "<th>";
            msg += "<img style=\"width:35px; height:31px;\" src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/ico_email_pw.png\" title=\"변경된 비밀번호 안내 아이콘\"></img>";
            msg += "<p style=\"padding-bottom:40px;border-bottom:1px solid #602B8A; color: #602B8A; font-size=20px; font-weight: bold;\">변경된 비밀번호 안내</p>";
            msg += "</th>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td>";
            msg += "<p style=\"color:#3E3E3E;text-align:center;font-size:15px;font-weight:bold;\">" + payloadMap.get("username").toString() + " 님</p>";
            msg += "<p style=\"margin-bottom:60px;color:#3E3E3E;text-align:center;font-size:15px;\">변경된 비밀번호는 <span style=\"color:#602B8A;font-weight:bold;\">" + payloadMap.get("uuid").toString() + "</span> 입니다.<br/>하단 링크를 통해 로그인이 가능합니다.<br/>감사합니다.</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<a href=\"" + emailConfig.getWhiveDomaim() + "\">";
            msg += "<button style=\"padding:13px 24px;border-radius:21px;border:0;width:100%;background: #602B8A;color:#fff;cursor: pointer;\">W-Hive 로그인 페이지로 이동</button>";
            msg += "</a>";
            msg += "</td>";
            msg += "</tr>";
            msg += "</table>";
            msg += "<footer style=\"width: 400px; margin:24px auto 0;padding:16px;background:#F5F5F7\">";
            msg += "<p style=\"margin-top:0;font-size:14px;font-weight:bold;\">인스웨이브시스템즈</p>";
            msg += "<span style=\"display:block;margin-bottom:16px;color:#959595;font-size:13px;\">서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>C-12F, 247, Gonghang-Daero, Gangseo-Gu, Seoul, Korea</span>";
            msg += "<div>";
            msg += "<a href=\"tel:02-2082-1400\" style=\"color:#959595;font-size:13px\">8202-2082-1400</a>";
            msg += "<a href=\"mailto:whivehelp@inswave.com\" style=\"margin-left:18px;color:#959595;font-size:13px\">whivehelp@inswave.com</a>";
            msg += "</div>";
            msg += "<a href=\"https://www.inswave.com/\">";
            msg += "<img style=\"margin-top:24px;width:130px;height:24px;\" src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/inswave_logo.png\" title=\"인스웨이브 로고\"></img>";
            msg += "</a>";
            msg += "</footer>";
            msg += "</body>";
            msg += "</html>";

//            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 비밀번호 재설정</h1>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> "+ payloadMap.get("username").toString() +"님, 새로운 비밀번호 \""+payloadMap.get("uuid").toString()+"\"로 통해 아래 링크로 로그인이 가능합니다.</p>";
//            // input box
//            // button
//            msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    // 탈퇴 링크 이메일 보내기
    @Async
    public void sendUserSecessionToEmail(Map<String, Object>  payloadMap) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // session.setDebug(true); //for debug

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,payloadMap.get(PayloadKeyType.email.name()).toString());
            mailMessage.setSubject("W-Hive 탈퇴 링크 전달");

            String msg="";
            msg += "<!DOCTYPE html>";
            msg += "<html lang=\"ko\">";
            msg += "<head>";
            msg += "<meta charset=\"UTF-8\">";
            msg += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
            msg += "<title>W-Hive 탈퇴 링크 전달</title>";
            msg += "</head>";
            msg += "<body>";
            msg += "<h1 style=\"width:140px;height:31px;background:url(" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_logo.png) no-repeat;\" title=\"W-hive Logo\"></h1>";
            msg += "<table style=\"margin:0 auto;padding-bottom:40px;border-bottom:1px solid #602B8A;width:400px;color:#602B8A;\">";
            msg += "<tr>";
            msg += "<th>";
            msg += "<img style=\"width:35px;height:31px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_email_link.png\" title=\"W-Hive 탈퇴 링크 아이콘\"></img>";
            msg += "<p style=\"padding-bottom:40px;border-bottom:1px solid #602B8A; color:#602B8A;font-size:20px;font-weight:bold;\">W-Hive 탈퇴 링크</p>";
            msg += "</th>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td>";
            msg += "<p style=\"margin-bottom:60px;color:#3E3E3E;text-align:center;font-size:15px;\">W-Hive 서비스 탈퇴 절차를 진행하시려면 아래 링크를 클릭해주세요.</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td style=\"text-align: center;\">";
            msg += "<a href=\"" + emailConfig.getWhiveDomaim() + "/manager/account/resign/sendPageUrl?email=" + payloadMap.get(PayloadKeyType.email.name()).toString() + "\" rel=\"noreferrer noopener\" target=\"_blank\">";
            msg += "<button style=\"padding:13px 24px;border-radius:21px;border:0;width:100%;background: #602B8A;color:#fff;cursor: pointer;\">W-Hive 탈퇴 페이지로 이동</button>";
            msg += "</a>";
            msg += "</td>";
            msg += "</tr>";
            msg += "</table>";
            msg += "<footer style=\"width: 400px; margin:24px auto 0;padding:16px;background:#F5F5F7\">";
            msg += "<p style=\"margin-top:0;font-size:14px;font-weight:bold;\">인스웨이브시스템즈</p>";
            msg += "<span style=\"display:block;margin-bottom:16px;color:#959595;font-size:13px;\">서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>C-12F, 247, Gonghang-Daero, Gangseo-Gu, Seoul, Korea</span>";
            msg += "<div>";
            msg += "<a href=\"tel:02-2082-1400\" style=\"color:#959595;font-size:13px\">8202-2082-1400</a>";
            msg += "<a href=\"mailto:whivehelp@inswave.com\" style=\"margin-left:18px;color:#959595;font-size:13px\">whivehelp@inswave.com</a>";
            msg += "</div>";
            msg += "<a href=\"https://www.inswave.com/\">";
            msg += "<img style=\"margin-top:24px;width:130px;height:24px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/inswave_logo.png\" title=\"인스웨이브 로고\"></img>";
            msg += "</a>";
            msg += "</footer>";
            msg += "</body>";
            msg += "</html>";

//            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 탈퇴 링크</h1>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> W-Hive 서비스 탈퇴 절차를 진행하시려면 아래 링크를 클릭해주세요.</p>";
            // input box
            // button
//             msg += "<a href=\""+emailConfig.getWhiveDomaim()+"/manager/account/resign/sendPageUrl?email="+payloadMap.get(PayloadKeyType.email.name()).toString()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">W-Hive 탈퇴 페이지 이동</a>";
//            msg += "<a href=\"http://localhost:9095/manager/account/resign/sendPageUrl?email="+payloadMap.get(PayloadKeyType.email.name()).toString()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">W-Hive 탈퇴 페이지 이동</a>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    @Async
    public void sendUserSecessionResultToEmail(Map<String, Object>  payloadMap) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,payloadMap.get(PayloadKeyType.email.name()).toString());
            mailMessage.setSubject("W-Hive 탈퇴 완료");

            String msg="";
            msg += "<!DOCTYPE html>";
            msg += "<html lang=\"ko\">";
            msg += "<head>";
            msg += "<meta charset=\"UTF-8\">";
            msg += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
            msg += "<title>Document</title>";
            msg += "</head>";
            msg += "<body>";
            msg += "<h1 style=\"width:140px;height:31px;background:url(" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_logo.png) no-repeat;\" title=\"W-hive Logo\"></h1>";
            msg += "<table style=\"margin:0 auto;padding-bottom:40px;border-bottom:1px solid #602B8A;width:400px;color:#602B8A;\">";
            msg += "<tr>";
            msg += "<th>";
            msg += "<img style=\"width:35px;height:31px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/w-hive_email_join.png\" title=\"W-Hive 탈퇴 완료 아이콘\"></img>";
            msg += "<p style=\"padding-bottom:40px;border-bottom:1px solid #602B8A; color:#602B8A;font-size:20px;font-weight:bold;\">W-Hive 탈퇴 완료</p>";
            msg += "</th>";
            msg += "</tr>";
            msg += "<tr>";
            msg += "<td>";
            msg += "<p style=\"color:#3E3E3E;text-align:center;font-size:15px;\">W-Hive 서비스 탈퇴 완료 되었습니다.<br/>감사합니다.</p>";
            msg += "</td>";
            msg += "</tr>";
            msg += "</table>";
            msg += "<footer style=\"width: 400px; margin:24px auto 0;padding:16px;background:#F5F5F7\">";
            msg += "<p style=\"margin-top:0;font-size:14px;font-weight:bold;\">인스웨이브시스템즈</p>";
            msg += "<span style=\"display:block;margin-bottom:16px;color:#959595;font-size:13px;\">서울시 강서구 공항대로 247, 퀸즈파크나인 C동 12층 (07803)<br/>C-12F, 247, Gonghang-Daero, Gangseo-Gu, Seoul, Korea</span>";
            msg += "<div>";
            msg += "<a href=\"tel:02-2082-1400\" style=\"color:#959595;font-size:13px\">8202-2082-1400</a>";
            msg += "<a href=\"mailto:whivehelp@inswave.com\" style=\"margin-left:18px;color:#959595;font-size:13px\">whivehelp@inswave.com</a>";
            msg += "</div>";
            msg += "<a href=\"https://www.inswave.com/\">";
            msg += "<img style=\"margin-top:24px;width:130px;height:24px;\"src=\"" + emailConfig.getWhiveDomaim() + "/cm/images/email/inswave_logo.png\" title=\"인스웨이브 로고\"></img>";
            msg += "</a>";
            msg += "</footer>";
            msg += "</body>";
            msg += "</html>";

//            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 탈퇴 완료</h1>";
//            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> W-Hive 서비스 탈퇴 완료 되었습니다. 감사합니다.</p>";
            // input box
            // button
            // msg += "<a href=\"https://whive.inswave.kr/manager/account/resign/sendPageUrl?email="+payloadMap.get(PayloadKeyType.email.name()).toString()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">W-Hive 탈퇴 페이지 이동</a>";
            // msg += "<a href=\"http://localhost:9095/manager/account/resign/sendPageUrl?email="+payloadMap.get(PayloadKeyType.email.name()).toString()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">W-Hive 탈퇴 페이지 이동</a>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    // 빌드 완료 이메일 발송
    @Async
    public void sendBuildResultToEmail(Map<String, Object>  payloadMap) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // session.setDebug(true); //for debug

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,payloadMap.get(PayloadKeyType.email.name()).toString());
            String msg="";

            if(payloadMap.get("status").equals("SUCCESSFUL")){
                mailMessage.setSubject("W-Hive 빌드 완료 안내");

                msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 빌드 완료 안내</h1>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> \""+payloadMap.get("user_login_id").toString()+"\" 님 빌드 완료 되었습니다. </p>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> 로그인 화면으로 이동하고 Build History 화면에서 빌드 이력을 확인 하실 수 있습니다. .</p>";
//            msg += "<a href=\"http://localhost:9095\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";
                msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";

            }else if(payloadMap.get("status").equals("FAILED")){
                mailMessage.setSubject("W-Hive 빌드 실패 안내");

                msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 빌드 실패 안내</h1>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> \""+payloadMap.get("user_login_id").toString()+"\" 님 빌드 실패 되었습니다. </p>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> 로그인 화면으로 이동하고 Build History 화면에서 빌드 이력을 확인 하실 수 있습니다. .</p>";
//            msg += "<a href=\"http://localhost:9095\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";
                msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";

            }

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    // 배포 완료 이메일 발송
    @Async
    public void sendDeployResultToEmail(Map<String, Object>  payloadMap) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO,payloadMap.get(PayloadKeyType.email.name()).toString());


            String msg="";
            if(payloadMap.get("status").equals("SUCCESSFUL")){
                mailMessage.setSubject("W-Hive 스토어 배포 완료 안내");

                msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 스토어 배포 완료 안내</h1>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> \""+payloadMap.get("user_login_id").toString()+"\" 님 스토어 배포 완료 되었습니다. </p>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> 로그인 화면으로 이동하고 Deploy History 화면에서 배포 이력을 확인 하실 수 있습니다. .</p>";
                msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";

            }else if(payloadMap.get("status").equals("FAILED")){
                mailMessage.setSubject("W-Hive 스토어 배포 실패 안내");

                msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive 스토어 배포 실패 안내</h1>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> \""+payloadMap.get("user_login_id").toString()+"\" 님 스토어 배포 실패 되었습니다. </p>";
                msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\"> 로그인 화면으로 이동하고 Deploy History 화면에서 배포 이력을 확인 하실 수 있습니다. .</p>";
                msg += "<a href=\""+emailConfig.getWhiveDomaim()+"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">로그인 페이지 이동</a>";

            }

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(),MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
             log.warn(e.getMessage(), e);
        }

    }

    public void sendServiceContactUsEmail(Map<String, String> payload) {
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put(MAIL_SMTP_HOST, emailConfig.getHost());
        props.put(MAIL_SMTP_PORT, emailConfig.getPort());
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_TTL_ENABLE, "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });

        // create email instance
        MimeMessage mailMessage = new MimeMessage(session);
        try {
            // 주소 동적 처리
            mailMessage.addRecipients(Message.RecipientType.TO, "whivehelp@inswave.com");

            mailMessage.setSubject("W-Hive Service 문의");

            String msg = "";
            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">W-Hive Service 문의</h1>";
            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">이름 : " + payload.get("name").toString() + " </p>";
            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">연락처 : " + payload.get("phone").toString() + " </p>";
            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">이메일 : " + payload.get("email").toString() + " </p>";
            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">내용 : " + payload.get("content").toString() + " </p>";

            mailMessage.setText(msg, UTF_8, "html"); //내용
            mailMessage.setFrom(new InternetAddress(this.emailConfig.getUsername(), MAIL_PERSONAL_NAME));

            // send mail
            quickService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(mailMessage);
                    }catch(Exception e){
                        log.error("Exception occur while send a mail : ",e);
                    }
                }
            });

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.warn(e.getMessage(), e);
        }

    }

    private static String createKey() {
        StringBuffer key = new StringBuffer();
        SecureRandom rnd = new SecureRandom();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }

}
