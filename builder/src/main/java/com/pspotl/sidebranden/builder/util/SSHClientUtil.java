package com.pspotl.sidebranden.builder.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;


@Slf4j
@Component
public class SSHClientUtil {

    // server config setting
    @Value("${whive.gitserver.target}")
    private String serverIp;

    @Value("${whive.gitserver.port}")
    private int serverPort;

    @Value("${whive.gitserver.user}")
    private String user;

    @Value("${whive.gitserver.password}")
    private String password;

    public static void order() throws Exception{
        JSch jsch = new JSch();
        Session session = jsch.getSession("gti-admin", "localhost", 22);
        session.setPassword("inswave!1");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();  //연결


        Channel channel = session.openChannel("exec");  //채널접속
        ChannelExec channelExec = (ChannelExec) channel; //명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand("cd "); //내가 실행시킬 명령어를 입력


        //콜백을 받을 준비.
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err);

        channel.connect();  //실행

        byte[] tmp = new byte[1024];

        while (in.available() > 0) {
            int i = in.read(tmp, 0, 1024);
            outputBuffer.append(new String(tmp, 0, i));
            if (i < 0) break;
        }



        //if (channel.isClosed()) {
            System.out.println("결과");
            System.out.println(outputBuffer.toString());

        //}

        channel.disconnect();

    }

    public void taskSSHMkdir(String mkdirProjectPath ,String repositoryName) {

        Session session = null;
        Channel channel = null;
        BufferedReader is = null;

        try {

                JSch jsch = new JSch();
                session = jsch.getSession(user, serverIp, serverPort);
                session.setPassword(password);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();  //연결

                channel = session.openChannel("exec");
                ((ChannelExec)channel).setCommand("cd "+mkdirProjectPath+" && mkdir "+ repositoryName);

                channel.setInputStream(null);
                ((ChannelExec)channel).setErrStream(System.err);
                InputStream in = channel.getInputStream();
                channel.connect();

                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) {
                            break;
                        }
                        System.out.print(new String(tmp, 0, i));
                    }
                    if (channel.isClosed()) {
                        if (channel.getExitStatus() == 0) {
                            System.out.println("Command executed successully.");
                        }
                        break;
                    }
                }

                System.out.println("결과");

        } catch (JSchException | IOException e){
            
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }


    }

    public void taskGitBare(String mkdirProjectPath ,String repositoryName) {

        Session session = null;
        Channel channel = null;
        BufferedReader is = null;

        try {

            JSch jsch = new JSch();
            session = jsch.getSession(user, serverIp, serverPort);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();  //연결

            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand("cd "+mkdirProjectPath + repositoryName+" && git init --bare");

            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (channel.getExitStatus() == 0) {
                        System.out.println("Command executed successully.");
                    }
                    break;
                }
            }

            System.out.println("결과");

        } catch (JSchException | IOException e){
                
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }


    }

}
