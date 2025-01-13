#!/bin/zsh

# 1. plugin parameter
# 2. project path
# 3.
# 4. rootpath + workspace + projectname

#set -e
#echo "PluginManager ... "

case "$2" in
   :whybrid-plugins:setConfig)
    projRoot=$1
     echo `cd $projRoot && ./gradlew $2 $3 $4"$5" --no-daemon`
    ;;
   :whybrid-plugins:GetInformationHive)
    projRoot=$1
      echo `cd $projRoot && ./gradlew -S $2 -P $projRoot`
      ;;
    getConfig)
   #:whybrid-plugins:GetInformation)
    projRoot=$1
    #echo `cd $projRoot && ./gradlew $2 -P $3 --no-daemon`
      echo `cd $projRoot && ./gradlew $2 --no-daemon`
#      echo `cd $projRoot && ./gradlew $2 --no-daemon`
      ;;
    getConfigAll)
    projRoot=$1
    echo `cd $projRoot && ./gradlew getConfig -P $3 --no-daemon`
      ;;
   :whybrid-plugins:AddPlugin)
    projRoot=$1
    if [ -n "$5" ]; then
        echo "$5 is not empty"
        echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 --no-daemon`
#        echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 -P $7 -P $8`
      else
#        echo `cd $projRoot && ./gradlew --stop && ./gradlew $2 -P $3`
        echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 --no-daemon`
#        echo `cd $projRoot && ./gradlew $2 -P $3 --no-daemon`
    fi
      ;;
   :whybrid-plugins:RemovePlugin)
    projRoot=$1
     if [ -n "$5" ]; then
        echo "$4 is not empty"
        echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 --no-daemon`
#        echo `cd $projRoot && ./gradlew --stop && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 -P $7 -P $8`
      else
        #echo `cd $projRoot && ./gradlew --stop && ./gradlew $2 -P $3`
        echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 --no-daemon`
#        echo `cd $projRoot && ./gradlew $2 -P $3 --no-daemon`
     fi
      ;;
    setConfig)
      #:wmatrix-plugins:setConfig)
    projRoot=$1
      echo `cd $projRoot && ./gradlew $2 $3 $4"$5" --no-daemon`
    ;;
    :wmatrix-plugins:GetInformationHive)
    projRoot=$1
      echo `cd $projRoot && ./gradlew -S $2 -P $projRoot`
    ;;
    :wmatrix-plugins:GetInformation)
    projRoot=$1
      echo `cd $projRoot && ./gradlew $2 -P $3 --no-daemon`
    #      echo `cd $projRoot && ./gradlew $2 --no-daemon`
    ;;
    #:wmatrix-plugins:AddPlugin)
    addPlugin)
    projRoot=$1
    if [ -n "$5" ]; then
       echo "$5 is not empty"
       echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 --no-daemon`
    #        echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 -P $7 -P $8`
    else
    #        echo `cd $projRoot && ./gradlew --stop && ./gradlew $2 -P $3`
       echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 --no-daemon`
    #        echo `cd $projRoot && ./gradlew $2 -P $3 --no-daemon`
    fi
    ;;
    #:wmatrix-plugins:RemovePlugin)
    removePlugin)
    projRoot=$1
    if [ -n "$5" ]; then
      echo "$4 is not empty"
      echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 --no-daemon`
    #        echo `cd $projRoot && ./gradlew --stop && ./gradlew $2 -P $3 -P $4 -P $5 -P $6 -P $7 -P $8`
    else
      #echo `cd $projRoot && ./gradlew --stop && ./gradlew $2 -P $3`
      echo `cd $projRoot && ./gradlew $2 -P $3 -P $4 --no-daemon`
    #        echo `cd $projRoot && ./gradlew $2 -P $3 --no-daemon`
    fi
    ;;
   :wmatrix-plugins:getConfig)
   projRoot=$1
     echo "getConfig start "
     cd $projRoot && ./gradlew clean && ./gradlew :wmatrix-plugins:getConfig
     echo "getConfig end "
   ;;
   clean)
    projRoot=$1
      echo `cd $projRoot && ./gradlew $2 --no-daemon`
      ;;
   debugbuild)
      projRoot=$1
      echo "debug build start "
#      echo `cd $projRoot && ./gradlew clean`
# -P flavor=$3
      cd $projRoot && ./gradlew clean && ./gradlew matrixAssembleBuild -P buildType=debug --stacktrace
      echo "debug build end "
      ;;
   releasebuild)
    projRoot=$1
      echo "release build start "
      cd $projRoot && ./gradlew clean && ./gradlew matrixAssembleBuild -P buildType=release --stacktrace
      echo "release build end "
      ;;
   :whybrid-plugins:getConfig)
    projRoot=$1
      echo "getConfig start "
      cd $projRoot && ./gradlew clean --no-daemon && ./gradlew :whybrid-plugins:getConfig --no-daemon
      echo "getConfig end "
      ;;
    makeDebugAab)
      projRoot=$1
      appName=$3
      appVersion=$4
      echo "Debug aab build start "
      # AAB 빌드 시, 매니저와의 세션이 끊어지는 현상 발견 2023-04-04
      # 원인: gradlew 실행 시, --stop 옵션을 주면, daemon이 죽기 때문에, 로그 기록이 안되고,
      # 해당 로그를 읽으려고 하기 때문에, 오류가 나면서 세션이 끊기게 된다.
      # @soorink
      #      cd $projRoot && ./gradlew --stop && ./gradlew clean --no-daemon && ./gradlew matrixBundleBuild -P buildType=debug --stacktrace
      cd $projRoot && ./gradlew clean --no-daemon && ./gradlew matrixBundleBuild -P buildType=debug --stacktrace
      echo "Debug aab build end "
      ;;
    makeReleaseAab)
      projRoot=$1
      appName=$3
      appVersion=$4
      echo "release aab build start "
#      cd $projRoot && ./gradlew --stop && ./gradlew clean --no-daemon && ./gradlew matrixBundleBuild -P buildType=release --stacktrace
      cd $projRoot && ./gradlew clean --no-daemon && ./gradlew matrixBundleBuild -P buildType=release --stacktrace
      echo "release aab build end "
      ;;
    getAllProfiles)
      projRoot=$1
      echo `cd $projRoot && ./gradlew $2 --no-daemon`
      ;;
    setActiveProfile)
      projRoot=$1
      echo `cd $projRoot && ./gradlew $2 $3 $4$5 --no-daemon`
      ;;
esac
