package com.pspotl.sidebranden.builder.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;

/**
 * SVN 기본 명령어 타입
 */
enum SvnCmdType {
    CREATE,
    CHECKOUT,
    ADD,
    COMMIT,
    UPDATE
}

enum SvnProtocols {
    svnSshProtocol("svn+ssh://"),
    svnProtocol("svn://");

    private final String protocol;

    SvnProtocols(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }
}

/**
 * SVN 기본동작을 CLI를 이용해서, W-Hive안에서 동작하게 한다
 * 현재는 비밀번호를 입력하게 하기 위해서는 다음과 같은 환경이 필요하다.
 * 1. sshpass
 * 2. export SSHPASS="macpassword"
 *
 * - create     (git init)
 * - checkout   (git clone)
 * - add        (git add)
 * - commit     (git commit)
 * - update     (git pull)
 */
@Slf4j
@Component
public class SvnTask {

    private static final String svnAccount = "svn-admin";
    private static final String svnDomain = "localhost";
    private static final String svnServerRepo = "/SvnRepo";

    private void svnServerCheckAndStart() {
        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();
        BufferedReader br = null;

        String tmp = "";
        String psResult = "";
        try {
            handler.start();
            executor.setStreamHandler(handler);
            CommandLine svnServer = CommandLine.parse("zsh");
            svnServer.addArguments(new String[]{"-c", "ps -ef | grep svnserve"}, false);
            executor.execute(svnServer, resultHandler);

            br = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            while ((tmp = br.readLine()) != null) {
                psResult += tmp;
            }
            br.close();

            // svnserver가 살행되고 있지 않으면 실행한다
            if (!psResult.contains("svnserve -d -r")) {
                CommandLine startSVN = CommandLine.parse("svnserve -d -r /Users/" + svnAccount + svnServerRepo);
                executor.execute(startSVN, resultHandler);
            }
            resultHandler.waitFor();
            handler.stop();

        } catch (Exception e) {
            log.error("svnServerCheckAndStart Error = {}", e.getLocalizedMessage(), e);
        }
    }

    public Boolean svnCreate(String svnRepoID, String svnRepoPwd, URI repoPath) {
        svnServerCheckAndStart();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();

        try {
            CommandLine svnCreateCLI = CommandLine.parse("svnadmin create");
            svnCreateCLI.addArgument(repoPath.toString());

            executor.execute(svnCreateCLI, resultHandler);
            resultHandler.waitFor();

            // 권한 추가
            CommandLine authCLI = CommandLine.parse("chmod -R");
            authCLI.addArgument("777");
            authCLI.addArgument(repoPath.toString());

            executor.execute(authCLI, resultHandler);
            resultHandler.waitFor();

            createAccountForSvnRepo(svnRepoID, svnRepoPwd, repoPath);
            createAuthForSvnRepo(svnRepoID, repoPath);
            createSvnServeConf(repoPath);

            return true;
        } catch (Exception e) {
            log.error("SvnTask svnCreate Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    private Boolean createAccountForSvnRepo(String svnRepoID, String svnRepoPwd, URI repoPath) {
        String passwdPath = repoPath.toString() + "/conf/passwd";

        try {
            File file = new File(passwdPath);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fw);

            writer.write(svnRepoID + "=" + svnRepoPwd + "\n");
            writer.close();

            return true;
        } catch (Exception e) {
            log.error("Create Account For SVN Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    private Boolean createAuthForSvnRepo(String svnRepoID, URI repoPath) {
        String authPath = repoPath.toString() + "/conf/authz";
        try {
            File file = new File(authPath);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fw);

            writer.write("[/]\n");
            writer.write(svnRepoID + " = rw\n");

            writer.close();

            return true;
        } catch (Exception e) {
            log.error("Create Auth For Svn Repo Erorr = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    private Boolean createSvnServeConf(URI repoPath) {
        String serverConfPath = repoPath.toString() + "/conf/svnserve.conf";
        try {
            File file = new File(serverConfPath);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, false);
            BufferedWriter writer = new BufferedWriter(fw);

            writer.write("[general]\n");
            writer.write("anon-access = none\n");
            writer.write("auth-access = write\n");
            writer.write("password-db = passwd\n");
            writer.write("authz-db = authz\n");

            writer.close();

            return true;
        } catch (Exception e) {
            log.error("Create Svn ServerConf Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    public Boolean svnCheckout(String svnRepoID, String svnRepoPwd, URI repoPath, URI localPath) {
        svnServerCheckAndStart();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();

        try {
            CommandLine svnCheckoutCLI = CommandLine.parse("svn checkout --username " + svnRepoID + " --password " + svnRepoPwd);
            svnCheckoutCLI.addArgument(SvnProtocols.svnProtocol.getProtocol() + svnAccount + "@" + svnDomain + repoPath.toString());
            svnCheckoutCLI.addArgument(localPath.toString());

            executor.execute(svnCheckoutCLI, resultHandler);
            resultHandler.waitFor();

            return true;
        } catch (Exception e) {
            log.error("SvnTask svnCheckout Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    public Boolean svnAdd(URI localRepoPath) {
        svnServerCheckAndStart();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();

        try {
            CommandLine svnAddCLI = CommandLine.parse("svn add " + localRepoPath.toString());
            svnAddCLI.addArgument("--force");

            executor.execute(svnAddCLI, resultHandler);
            resultHandler.waitFor();

            return true;
        } catch (Exception e) {
            log.error("SvnTask svnAdd Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    public Boolean svnCommit(String svnRepoID, String svnRepoPwd, URI localRepoPath, String commitMsg) {
        svnServerCheckAndStart();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();

        try {
            CommandLine svnCommitCLI = CommandLine.parse("svn commit --username " + svnRepoID + " --password " + svnRepoPwd + " -m");
            svnCommitCLI.addArgument(commitMsg);
            svnCommitCLI.addArgument(localRepoPath.toString());

            executor.execute(svnCommitCLI, resultHandler);
            resultHandler.waitFor();

            return true;
        } catch (Exception e) {
            log.error("SvnTask svnCommit Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    public Boolean svnUpdate(URI localRepoPath) {
        svnServerCheckAndStart();
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();

        try {
            CommandLine svnUpdateCLI = CommandLine.parse("svn update");
            svnUpdateCLI.addArgument(localRepoPath.toString());

            executor.execute(svnUpdateCLI, resultHandler);
            resultHandler.waitFor();

            return true;
        } catch (Exception e) {
            log.error("SvnTask svnUpdate Error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }
}
