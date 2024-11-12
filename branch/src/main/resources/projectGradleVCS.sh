#!/bin/bash

case "$1" in
   :git:clone)
    projRoot=$2
      echo `cd $projRoot && ./gradlew $1 -P $3 -P $4 -P $5 -P $6 --no-daemon`
    ;;
   :git:pull)
    projRoot=$2
      echo `cd $projRoot && ./gradlew $1 -P $3 -P $4 -P $5 --no-daemon`
    ;;
   :git:addAll)
    projRoot=$2
      echo `cd $projRoot && ./gradlew $1 -P $3 --no-daemon`
    ;;
   :git:commitAll)
    projRoot=$2
      echo `cd $projRoot && ./gradlew $1 -P $3 -P $4 --no-daemon`
    ;;
   :git:push)
    projRoot=$2
      echo `cd $projRoot && ./gradlew $1 -P $3 -P $4 -P $5 --no-daemon`
    ;;
esac