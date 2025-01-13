#!/bin/bash


case "$1" in
   getTemplateVersion)
     FILENAME=($(ls .))
     projRoot=$2
      echo `cd $projRoot && ls . | grep .zip`
      ;;
    createTemplateMkdir)
      projRoot=$2
      echo `cd $projRoot && mkdir $3`
      ;;
    createTemplateCopy)
      projRoot=$2
      # cp 하기 전에 file name 검사
      echo `cd $projRoot && cp $projRoot $3`
      ;;
    createTemplateUnzip)
      # unzip 하기전에 filename 출력
      FILENAME=`${$3%%\]*}`
      projRoot=$2 #
      DOTZIP=`.zip`
      echo `cd $projRoot && unzip $FILENAME$DOTZIP `
      ;;
    createGitRepository)
      projRoot=$2
      echo `cd $projRoot && mkdir $3`
      ;;
esac