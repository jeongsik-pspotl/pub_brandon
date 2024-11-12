#!/bin/bash

# ./projectVCS.sh git clone https://kimchangok@github.com/kimchangok/wkwebview.git /Users/whive/ios/project1
# shell script parameter
# 1. vcsType
# 2. command
# 3. remoteRepository url
# 4. gitRepositoryPath

# gitClone parameter
# 1:remoteServer 2:gitRespositoryPath
function gitClone ()
{
  echo "git clone start"
  echo $1
  echo $2

  cd $1 && git clone $2

  echo "git clone complete"
}

#function gitClone ()
#{
#  echo "git clone start"
#  echo $1
#  echo $2
#  if [ -d $2 ]; then
#      # 기존경로에 폴더가 있다면 폴더삭제후 Clone
#      git clone $1 $2
#  else
#      echo $1
#      echo $2
#      mkdir $2 && git clone $1 $2
#  fi
#  echo "git clone complete"
#}



# ./projectVCS.sh git add --all /Users/whive/ios/project1
# shell script parameter
# 1. VCSType
# 2. command
# 3. path
# 4. gitRespositoryPath
# 전체추가 --all, 개발 파일추가는 변경되거나 추가된 file path

# gitAdd parameter
# 1: gitRespositoryPath 2:path
function gitAdd ()
{
  echo "git add start"
  if [ -d $1 ]; then
    cd $1 && git add $2
  fi
  echo "git add end"
}


# ./projectVCS.sh git commit message123 /Users/whive/ios/project1
# shell script parameter
# 1. VCSType
# 2. command
# 3. commit message
# 4. gitRespositoryPath

# gitCommit parameter
# 1:gitRespositoryPath 2:commit message
function gitCommit ()
{
  echo "git commit start"
  if [ -d $1 ]; then
    if [ -n $2 ]; then
      cd $1 && git commit -m $2
    else
      cd $1 && git commit -m ""
    fi
  fi
  echo "git commit end"
}

# ./projectVCS.sh git push origin master /Users/whive/ios/project1
# shell script parameter
# 1. VCSType
# 2. command
# 3. remote
# 4. branch
# 5. gitRespositoryPath

# gitPush parameter
# 1:gitRespositoryPath 2:remote 3:branch
function gitPush ()
{
  echo "git push start"
  if [ -d $1 ]; then
      cd $1 && git push $2 $3
  fi
  echo "git push end"
}

# ./projectVCS.sh git push origin master /Users/whive/ios/project1
# shell script parameter
# 1. VCSType
# 2. command
# 3. remote
# 4. branch
# 5. gitRespositoryPath

# gitPush parameter
# 1:gitRespositoryPath 2:remote 3:branch
function gitPull ()
{
  echo "git pull start"
  if [ -d $1 ]; then
      cd $1 && git pull
  fi
  echo "git pull end"
}

#./projectVCS.sh git status /Users/whive/ios/project1
# shell script parameter
# 1:gitRespositoryPath

# gitStatus parameter
# 1:gitRespositoryPath
function gitStatus ()
{
  echo "git status start"
  if [ -d $1 ]; then
      cd $1 && git status
  fi
  echo "git status end"
}

#./projectVCS.sh git bare /Users/whive/ios/project1
# shell script parameter
# 1:gitRespositoryPath

# gitBare parameter
# 1:gitRespositoryPath
function gitBare ()
{
  echo "git bare start"
  echo $1
  cd $1 && git init --bare
  echo "git bare end"
}


#./projectVCS.sh git init /Users/whive/ios/project1
# shell script parameter
# 1:gitRespositoryPath

# gitBare parameter
# 1:gitRespositoryPath
function gitInitAndAddCommit()
{
  echo "git add commit start"
  echo $1
  cd $1 && git add . && git commit -m "init first commit"
  echo "git add commit end"
}

function gitAppConfigSettingAddCommit()
{
  echo "git add commit start"
  echo $1
  echo $2
  cd $1 && git add . && git commit -m "$2"
  echo "git add commit end"
}


#./projectVCS.sh git remote /Users/whive/ios/project1
function gitRemoteSetUrl(){

  echo "git remote set-url start"
  echo $1
  echo $2
  cd $1 && git remote set-url --add origin $2
  echo "git remote set-url end"
}


# ./projectVCS.sh svn checkout 212 212 http://192.168.100.70:8181/svn/websquare/branches/BR_DEV_websquare_3_0/websquarehybridtemplate5.0 ./websquareHybrid 
# shell script parameter
# 1. vcsType
# 2. command
# 3. svn remoteRepository url
# 4. svnRepositoryPath

# svnCheckout parameter
# 1:username 2:password 3:svn remoteRepository url 4:svnRepositoryPath
function svnCheckout ()
{
  echo "svn checkout start"
  if [ -d $4 ]; then
      rm -rf $4
      svn checkout --username $1 --password $2 $3 $4
  else
      svn checkout --username $1 --password $2 $3 $4
  fi
  echo "svn checkout end"
}


# ./projectVCS.sh svn add * svnRepositoryPath
# shell script parameter
# 1. vcsType
# 2. command
# 3. filePath
# 4. svnRepositoryPath

# svnAdd parameter
# 1:svnRepositoryPath 2:path
function svnAdd ()
{
    echo "svn add start"
    if [ -d $1 ]; then
        cd $1 && svn st | grep '^?' | sed 's/^? *//' | xargs svn add
#        cd $1 && svn st | grep '^ \?' | sed 's/^\? *//'| xargs -I % svn add %
    else
        echo "not found svn repository"
        exit -1
    fi
    echo "svn add start"
}


# ./projectVCS.sh svn commit commitMessage svnRepositoryPath
# shell script parameter
# 1. vcsType
# 2. command
# 3. commitMessage
# 4. svnRepositoryPath

# gitClone parameter
# 1:svnRepositoryPath 2:commitMessage
function svnCommit ()
{
    echo "svn commit start"
    if [ -d $1 ]; then
        if [ -n $2 ]; then
            cd $1 && svn commit -m $2
        else
            cd $1 && svn commit -m ""
        fi
    else
        echo "not found svn repository"
        exit -1
    fi
    echo "svn commit end"
}

# ./projectVCS.sh svn update svnRepositoryPath
# shell script parameter
# 1. vcsType
# 2. command
# 3. svnRepositoryPath

# gitClone parameter
# 1:svnRepositoryPath
function svnUpdate ()
{
    echo "svn update start"
    if [ -d $1 ]; then
        cd $1 && svn update
    else
        echo "not found svn repository"
        exit -1
    fi
    echo "svn update end"
}


echo $2
echo $1

case "$1" in
  git)
      if [ -f "/usr/bin/git" ]; then
          case $2 in
            clone)
                repositoryPath=$4
                remote=$3
                #dest=$4
                #gitClone $remote $repositoryPath $dest
                gitClone $remote $repositoryPath
                ;;
            add)
                repositoryPath=$4
                path=$3
                gitAdd $repositoryPath $path
                ;;
            commit)
                repositoryPath=$4
                msg=$3
                gitCommit $repositoryPath $msg
                ;;
            push)
                repositoryPath=$5
                remote=$3
                branch=$4
                gitPush $repositoryPath $remote $branch
                ;;
            pull)
                repositoryPath=$3
                #remote=$3
                #branch=$5
                gitPull $repositoryPath
                ;;
            status)
                repositoryPath=$3
                gitStatus $repositoryPath
                ;;
            bare)
                repositoryPath=$3
                gitBare $repositoryPath
                ;;
            init)
              repositoryPath=$3
              gitInitAndAddCommit $repositoryPath
              ;;
            remote-set-url)
              repositoryPath=$3
              remote=$4
              gitRemoteSetUrl $repositoryPath $remote
              ;;
            appconfigset)
              repositoryPath=$3
              commitMsg=$4
              gitAppConfigSettingAddCommit $repositoryPath $commitMsg
              ;;
          esac
      else
          echo "not found git client"
          exit -1
      fi
      ;;
  svn)
      if [ -f "/usr/local/bin/svn" ]; then
          case $2 in
            checkout)
                user=$3
                password=$4
                remoteServer=$5
                svnRepositoryPath=$6
                svnCheckout $user $password $remoteServer $svnRepositoryPath
            ;;
            update)
                svnRepositoryPath=$3
                svnUpdate $svnRepositoryPath
            ;;
            add)
                svnRepositoryPath=$3
                #filePath=$4
                svnAdd $svnRepositoryPath
            ;;
            commit)
                svnRepositoryPath=$3
                commitMsg=$4
                svnCommit $svnRepositoryPath $commitMsg
            ;;
          esac
      else
          echo "not found svn client"
          exit -1
      fi
      ;;
esac
