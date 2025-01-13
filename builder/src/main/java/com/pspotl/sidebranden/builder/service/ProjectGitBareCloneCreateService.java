package com.pspotl.sidebranden.builder.service;

import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class ProjectGitBareCloneCreateService extends BaseService {


    private ReentrantLock reentrantLock = new ReentrantLock();

    // 결과는 다음 프로세스 처리를 위해서 동기화 처리를 한다.
    public void executueProcessSigniningCreate(String createTaskId, String platform, String projectPath, String gitRepositoryPath, String cmdShellscriptPath) {
        log.info(" #### buildservice parameter check ####{} {}" ,platform );

        if(platform.toLowerCase().equals(PayloadMsgType.android.name())){

            List<ProcessBuilder> signingCreateList = Collections.synchronizedList(new ArrayList<ProcessBuilder>());
            // "ls",";","ls",";",
            signingCreateList.add(new ProcessBuilder(cmdShellscriptPath, "git", "clone", projectPath, "ssh://"+gitRepositoryPath));

//                signingCreateList.add(new ProcessBuilder("cd",path,";","keytool"
//                        ,"-genkey","-dname"
//                        ,"CN=MarkJones,OU=JavaSoft,O=organizationName,L=localityName,C=country"
//                        ,"-alias",keytoolResult.get("key_alias").toString()
//                        ,"-keypass",keytoolResult.get("key_password").toString()
//                        ,"-keystore",keytoolResult.get("keystore_file_name").toString()
//                        ,"-storepass",keytoolResult.get("all_keyfile_password").toString()
//                        ,"-keyalg","RSA"
//                        ,"-keysize","2048"
//                        ,"-validity","10000"));

            executeSigningCreateListSync(signingCreateList, createTaskId);

        } else {

            List<ProcessBuilder> signingCreateList = Collections.synchronizedList(new ArrayList<ProcessBuilder>());
            // "ls",";","ls",";",
            signingCreateList.add(new ProcessBuilder(cmdShellscriptPath, "git", "clone", projectPath, "ssh://"+gitRepositoryPath));

//                signingCreateList.add(new ProcessBuilder("cd",path,";","keytool"
//                        ,"-genkey","-dname"
//                        ,"CN=MarkJones,OU=JavaSoft,O=organizationName,L=localityName,C=country"
//                        ,"-alias",keytoolResult.get("key_alias").toString()
//                        ,"-keypass",keytoolResult.get("key_password").toString()
//                        ,"-keystore",keytoolResult.get("keystore_file_name").toString()
//                        ,"-storepass",keytoolResult.get("all_keyfile_password").toString()
//                        ,"-keyalg","RSA"
//                        ,"-keysize","2048"
//                        ,"-validity","10000"));

            executeSigningCreateListSync(signingCreateList, createTaskId);

        }

    }

    public void executeSigningCreateListSync(List<ProcessBuilder> signingCreateList, String createTaskId){
        ProcessBuilder builder;

        synchronized(signingCreateList) {
            reentrantLock.lock();
            Iterator i = signingCreateList.iterator();
            while (i.hasNext()) {
                builder = (ProcessBuilder) i.next();
                builder.redirectErrorStream(true);
                Process p = null;
                try {
                    builder.redirectErrorStream(true);
                    p = builder.start();
                    OutputStream in = p.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(in));
                    InputStreamReader rd=new InputStreamReader(p.getInputStream());

                    BufferedReader reader1 =new BufferedReader(new InputStreamReader(p.getInputStream()));
                    StringBuffer out=new StringBuffer();
                    String output_line = "";
                    String input_line = "";

                    // while ((input_line = bw.toString()))
                    System.out.println(bw.toString());
                    System.out.println(reader1.readLine());
                    while ((output_line = reader1.readLine())!= null)
                    {
                        out=out.append(output_line+"/n");
                        System.out.println(output_line);

                    }

//                    signingCreateTask.setProcess(p);
//                    signingCreateTask.setSigningKeyService(this);
//                    signingCreateTask.setSigningCreateTaskId(createTaskId);
//                    asyncThreadPool.execute(signingCreateTask);

                } catch (Exception e) {

                    log.error("project create git bare init error ",e);
                    if (p != null) {
                        p.destroy();
                    }
                } finally {

                    reentrantLock.unlock();
                }
            }
        }

    }

}
