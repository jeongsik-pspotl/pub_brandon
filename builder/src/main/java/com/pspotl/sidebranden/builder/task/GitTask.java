package com.pspotl.sidebranden.builder.task;

import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

/**
 * git 명령어를 구분하기 위한 enum
 */
enum GitCmdType {
    INIT,
    ADD,
    COMMIT,
    PUSH,
    PULL,
    CLONE
}

/**
 * jGit을 이용하여, Git push, pull, clone 구현
 * 인증은 ssh 및 id, password를 이용한다
 */
@Slf4j
@Component
public class GitTask {
//    private Git git;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    // server config setting
    @Value("${whive.gitserver.target}")
    private String serverIp;

    @Value("${whive.gitserver.port}")
    private int serverPort;

    @Value("${whive.gitserver.user}")
    private String user;

    @Value("${whive.gitserver.password}")
    private String password;
    private String systemUserHomePath = System.getProperty("user.home");


    /**
     * git 작업 진행 전 필요한 작업을 진행한다
     * 1. String 으로 전달받은 repo 경로를 File 객체로 변경
     * 2. ssh를 이용하여 인증할 경우, 홈 디렉토리의 .ssh 폴더 경로 설정
     * 2-1. SshSessionFactory에서 .ssh 폴더안에 있는 프라이빗 키를 이용해서 인증할 수 있도록 설정 (예: id_rsa 파일 등)
     * 3. http를 이용하여 인증할 경우, 사용자 아이디 및 비밀번호 설정
     *
     * @param localRepo 리포지터리 경로
     */
    private Git prepare(GitCmdType cmd, Map<String, Object> parseResult, String localRepo, JSONObject remoteRepo) {
        Git git = null;
        
        try {
            // localRepo가 공백으로 들어오는 경우는 로컬 경로를 만들어 준다
            localRepo = getLocalRepoUrl(parseResult, localRepo);

            // git clone 시에는 git 객체를 다른 방식으로 생성해야 한다
            if (cmd != GitCmdType.CLONE) {
                File repoFile = Paths.get(localRepo).toFile();

                git = Git.open(repoFile);
            }
            log.info("git prepare complete");
        } catch (Exception e) {
            log.error("git prepare error = {}", e.getLocalizedMessage(), e);
        }
        
        return git;
    }

    /**
     * git 작업 진행 전 필요한 작업을 진행한다
     *
     * @param remoteRepo W-Hive에서 사용하는 객체, 리모트 리포지터리 관련 정보
     */
    private Object prepareCredential(JSONObject remoteRepo) {
        if (remoteRepo.size() > 0) {
            String remote = remoteRepo.get("repositoryURL").toString();
            if (remote.toLowerCase().startsWith("https://") || remote.toLowerCase().startsWith("http://")) {
                return prepareHTTPCredential(remoteRepo);
            } else {
                return prepareSSH();
            }
        } else {
            return prepareSSH();
        }
    }

    /**
     * git url이 http로 시작할 경우, id, password를 담고 있는 credential을 생성해서 반환한다
     *
     * @param remoteRepo 리모트 리포지터리 정보를 담고 있는 JSON 객체
     * @return UsernamePasswordCredentialsProvider
     */
    private UsernamePasswordCredentialsProvider prepareHTTPCredential(JSONObject remoteRepo) {
        return new UsernamePasswordCredentialsProvider(remoteRepo.get("repositoryId").toString(), remoteRepo.get("repositoryPassword").toString());
    }

    /**
     * git url이 http로 시작하지 않는 경우는, ssh url이라고 판단
     * ssh를 사용하기 위한 작업을 진행한다
     */
    private SshdSessionFactory prepareSSH() {
        try {
            //for apache sshd
            File sshDir = new File(FS.DETECTED.userHome(), ".ssh");
            SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
                    .setPreferredAuthentications("publickey") // ssh key 방식만 허용
                    .setHomeDirectory(FS.DETECTED.userHome())
                    .setSshDirectory(sshDir).build(new JGitKeyCache());
            SshSessionFactory.setInstance(sshdSessionFactory);
            log.info("git prepare ssh complete");
            return sshdSessionFactory;
        } catch (Exception e) {
            log.error("prepare git ssh error = {}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * git init bare를 jsch를 이용해서, ssh 접속하여 처리한다.
     * 기존 /branch/util/SSHClientUtil 파일을 그대로 옮겨왔다
     *
     * @param command 실행할 명령어 커맨드 추후 확장 가능 (mkdir: git 디렉토리 생성, bareInit: git bare init)
     * @param gitRepoRootPath git repo가 존재하는 루트 디렉토리 path
     * @param repoPath git repo가 존재하는 루트디렉토리 이하의 path
     * @return 성공 true, 실패 false
     */
    public Boolean gitWithSSH(String command, String gitRepoRootPath, String repoPath) {
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

            switch (command) {
                case "mkdir":
                    ((ChannelExec)channel).setCommand("cd " + gitRepoRootPath + " && mkdir " + repoPath);
                    break;
                case "bareInit":
                    ((ChannelExec)channel).setCommand("cd " + gitRepoRootPath + repoPath + " && git init --bare");
                    break;
            }

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
            return true;
        } catch (JSchException | IOException e){
            log.error("git init bare error = {}", e.getLocalizedMessage());
            return false;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * GitTask 클래스 내부에서 사용하는 git add
     *
     * @return 성공 true, 실패 false
     */
    private Boolean gitAdd(Git git) {
        try {
            git.add()
                    .addFilepattern(".")
                    .call();
            log.info("git add complete");
            return true;
        } catch (GitAPIException e) {
            log.error("git add error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * git add
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @param localRepo 로컬 리포지터리 경로
     * @param remoteRepo W-Hive에서 사용하는 객체, 리포지터리 관련 정보
     * @return 성공 true, 실패 false
     */
    public Boolean gitAdd(Map<String, Object> parseResult, String localRepo, JSONObject remoteRepo) {
        Git git;
        git = prepare(GitCmdType.ADD, parseResult, localRepo, remoteRepo);

        try {
            git.add()
                    .addFilepattern(".")
                    .call();
            log.info("git add complete");
            return true;
        } catch (GitAPIException e) {
            log.error("git add error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * GitTask 클래스 내부에서 사용하는 git commit
     *
     * @return 성공 true, 실패 false
     */
    private Boolean gitCommit(Git git, String commitMessage) {
        try {
            git.commit()
                    .setAll(true)
                    .setMessage(commitMessage)
                    .call();
            log.info("git commit complete");
            return true;
        } catch (GitAPIException e) {
            log.error("git commit error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * git commit
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @param localRepo 로컬 리포지터리 경로
     * @param remoteRepo W-Hive에서 사용하는 객체, 리포지터리 관련 정보
     * @return 성공 true, 실패 false
     */
    public Boolean gitCommit(Map<String, Object> parseResult, String localRepo, JSONObject remoteRepo, String commitMessage) {
        Git git;
        git = prepare(GitCmdType.COMMIT, parseResult, localRepo, remoteRepo);

        try {
            git.commit()
                    .setAll(true)
                    .setMessage(commitMessage)
                    .call();
            log.info("git commit complete");
            return true;
        } catch (GitAPIException e) {
            log.error("git commit error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * 인자로 받은 remoteRepo를 locadlRepo 경로에 clone 한다.
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @param localRepo 로컬 리포지터리 경로
     * @param remoteRepo W-Hive에서 사용하는 객체, 리포지터리 관련 정보
     * @return 성공 true, 실패 false
     *
     * param ex:
     * - localRepo: git@github.com:inswave/w-hive.git
     * - remoteRepo:{ "repositoryId":" .. ", "repositoryPassword":" .. ", "repositoryURL":" .. ", .. }
     **/
    public Boolean gitClone(Map<String, Object> parseResult, String localRepo, JSONObject remoteRepo) {
        Git git;
        localRepo = getLocalRepoUrl(parseResult, localRepo);
        Object credential = prepareCredential(remoteRepo);

        try {
            String remote = remoteRepo.get("repositoryURL").toString();
            if (remote.contains("@localhost")) {
                remote = getRemoteRepoGitUrl(parseResult, remote);
            }
            File cloneDir = new File(localRepo);

            if (!cloneDir.exists()) {
                cloneDir.mkdirs();
                cloneDir.setReadable(true);
                cloneDir.setExecutable(true);
                cloneDir.setWritable(true);
            }

            git = Git.cloneRepository()
                    .setURI(remote)
                    .setDirectory(cloneDir)
                    .setCredentialsProvider(credential instanceof CredentialsProvider ? (UsernamePasswordCredentialsProvider) credential : null)
                    .call();
            log.info("git clone complete");
            return true;
        } catch (GitAPIException e) {
            log.error("git clone error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * 인자로 받은 remoteRepo 에서 id, password를 전달 받아서 git pull 한다
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @param localRepo 로컬 리포지터리 경로
     * @param remoteRepo W-Hive에서 사용하는 객체, 리모트 리포지터리 관련 정보
     * @return 성공 true, 실패 false
     *
     * param ex:
     * - localRepo ex: Users/soorink/w-hive/builder/builder_main/DOMAIN_5/USER_2/WORKSPACE_W5/PROJECT_P170/PROJECT_P170
     * - remoteRepo ex:{ "repositoryId":" .. ", "repositoryPassword":" .. ", "repositoryURL":" .. ", .. }
     */
    public Boolean gitPull(Map<String, Object> parseResult, String localRepo, JSONObject remoteRepo) {
        Git git;
        Object credential = prepareCredential(remoteRepo);
        git = prepare(GitCmdType.PULL, parseResult, localRepo, remoteRepo);

        try {
            PullCommand pull = git.pull();

            if (credential instanceof CredentialsProvider) {
                pull.setCredentialsProvider((UsernamePasswordCredentialsProvider) credential);
            }

            pull.call();
            log.info("git pull complete");
            return true;
        } catch (Exception e) {
            log.info("git pull error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

     /**
     * W-Hive에서 사용하는 객체를 인자로 받아서, git push 한다
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @param localRepo 로컬 리포지터리 경로 (모를 경우 공백 "")
     * @param remoteRepo 리모트 리포지터리 정보를 담고 있는 JSON 객체
     * @param commitMessage 커밋 메시지
     * @return 성공 true, 실패 false
     */
    public Boolean gitPush(Map<String, Object> parseResult, String localRepo, JSONObject remoteRepo, String commitMessage) {

        try {
            Git git;
            git = prepare(GitCmdType.PUSH, parseResult, localRepo, remoteRepo);
            Object credential = prepareCredential(remoteRepo);
            gitAdd(git);
            gitCommit(git, commitMessage);

            Iterable<PushResult> pushResults = git.push()
                    .setCredentialsProvider(credential instanceof CredentialsProvider ? (UsernamePasswordCredentialsProvider) credential : null)
                    .call();

            // Push 에 관한 로그를 보고 싶으면 아래 주석을 해제하면 된다
//                for (PushResult r : pushResults) {
//                    for (RemoteRefUpdate update : r.getRemoteUpdates()) {
//                        log.info("git push result: " + update);
//                        if (update.getStatus() != RemoteRefUpdate.Status.OK && update.getStatus() != RemoteRefUpdate.Status.UP_TO_DATE) {
//                            String errorMessage = "git push failed: " + update.getStatus();
//                            throw new RuntimeException(errorMessage);
//                        }
//                    }
//                }
            log.info("Pushed from repository: " + git.getRepository().getDirectory() + " to remote repository at " + localRepo.toString());
            return true;

        } catch(Exception e) {
            log.error("git push error = {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * remote git url 반환
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @param remoteRepo git-admin@localhost일 경우, 전체 리모트 repo url이 아닌, 일부분만 가지고 있다
     * @return local git url을 string 형태로 반환
     */
    private String getRemoteRepoGitUrl(Map<String, Object> parseResult, String remoteRepo) {
        return remoteRepo + "DOMAIN_" + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/USER_" + parseResult.get(PayloadMsgType.userID.name()).toString() +"/WORKSPACE_W"
                + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString() + ".git";
    }

    /**
     * local repo url 반환
     *
     * @param parseResult W-Hive에서 사용하는 Map 객체, 대부분의 필요한 정보
     * @return
     */
    private String getLocalRepoUrl(Map<String, Object> parseResult, String localRepo) {
        if ("".equals(localRepo)) {
            return systemUserHomePath + userRootPath + "builder_main/DOMAIN_" + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/USER_" + parseResult.get(PayloadMsgType.userID.name()).toString() + "/WORKSPACE_W"
                    + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString();
        }

        return localRepo;
    }
}