#!/bin/zsh

# 1. plugin parameter
# 2. project path
# 3.
# 4. rootpath + workspace + projectname

#set -e
#echo "PluginManager ... "

case "$1" in
   getinformation)
    projRoot=$3
      #echo `cd $projRoot && ./plugman $1 $2`
      echo `whmanager $1 -r $2 -p $3`
#      echo `cd $projRoot && whmanager $1`
      ;;
   addplugin)
     projRoot=$4
      #echo `cd $projRoot && ./plugman $1 $3`
      echo `whmanager $1 -n $2 -r $3 -p $4`
#      echo `cd $projRoot && whmanager $1 -n $2`
      ;;
   removeplugin)
     projRoot=$4
      #echo `cd $projRoot && ./plugman $1 $3`
      echo `whmanager $1 -n $2 -r $3 -p $4`
      #echo `whmanager $1 -n $2 -p $4`
#      echo `cd $projRoot && whmanager $1 -n $2`
      ;;
   applyPlugin)
    projRoot=$2
      cd $projRoot && ./ProjManager $3 $4 $5
      ;;
   setCertificate)
      #echo $2
      whmanager setcertificate $2 $3
      ;;
   archive)
    projRoot=$2
     cd $projRoot && ./archive.sh $3 $4 $5
     ;;
esac
