#!/bin/zsh

# 1. plugin parameter
# 2. project path
# 3.
# 4. rootpath + workspace + projectname

# android alpha set
case "$2" in
   android-alpha)
    projRoot=$1
      cd $projRoot && fastlane alpha
    ;;
   android-beta)
      projRoot=$1
        cd $projRoot && fastlane beta
    ;;
   android-deploy)
         projRoot=$1
           cd $projRoot && fastlane deploy
    ;;
   ios-beta)
    projRoot=$1
      cd $projRoot && fastlane beta
    ;;
   ios-deploy)
    projRoot=$1
      cd $projRoot && fastlane deploy
    ;;
# android fastlane init
android-init)
projRoot=$1
cd $projRoot && fastlane init << EOF
n
EOF
;;
# android fastlane appfile set
android-appfile)
projRoot=$1
googlePlayAccess=$3
ANDROID_APP_BUNDLE_ID=$4
cat > $projRoot/fastlane/Appfile << EOL
json_key_file("$googlePlayAccess")
package_name("$ANDROID_APP_BUNDLE_ID")
EOL
;;
# android dotenv set
android-env-create)
projRoot=$1
app_path=$3
appfile_name=$4
touch $projRoot/fastlane/.env
cat > $projRoot/fastlane/.env << EOL
BUILD_APP_FILE_PATH=$3$4
BUILD_APP_VERSION_CODE=$5
SKIP_UPLOAD_AAB=false
SKIP_UPLOAD_METADATA=false
SKIP_UPLOAD_CHANGELOGS=false
SKIP_UPLOAD_IMAGES=false
SKIP_UPLOAD_SCREENSHOTS=false
VALIDATE_ONLY=false
CHANGES_NOT_SENT_FOR_REVIEW=false
RESCUE_CHANGES_NOT_SENT_FOR_REVIEW=true
ACK_BUNDLE_INSTALLATION_WARNING=false
EOL
;;
# android fastfile create
android-fastfile-create)
projRoot=$1
cat > $projRoot/fastlane/Fastfile << EOL
default_platform(:android)
platform :android do
desc "Runs all the tests"
lane :test do
gradle(task: "test")
end
desc "Submit a new Alpha Build to Alpha Test"
lane :alpha do
# gradle(
#     task: "clean assemble",
#     build_type: "Release"
# )
upload_to_play_store(
track: "alpha",
aab: ENV["BUILD_APP_FILE_PATH"],
version_code: ENV["BUILD_APP_VERSION_CODE"]
)
end
desc "Submit a new Beta Build to Crashlytics Beta"
lane :beta do
# gradle(
#     task: "clean assemble",
#     build_type: "Release"
# )
upload_to_play_store(track: "beta",
aab: ENV["BUILD_APP_FILE_PATH"],
version_code: ENV["BUILD_APP_VERSION_CODE"]
)
end
desc "Deploy a new version to the Google Play"
lane :deploy do
#gradle(task: "clean assembleRelease")
upload_to_play_store(
track: "production",
aab: ENV["BUILD_APP_FILE_PATH"],
version_code: ENV["BUILD_APP_VERSION_CODE"],
skip_upload_aab: ENV["SKIP_UPLOAD_AAB"],
skip_upload_metadata: ENV["SKIP_UPLOAD_METADATA"],
skip_upload_changelogs: ENV["SKIP_UPLOAD_CHANGELOGS"],
skip_upload_images: ENV["SKIP_UPLOAD_IMAGES"],
skip_upload_screenshots: ENV["SKIP_UPLOAD_SCREENSHOTS"],
validate_only: ENV["VALIDATE_ONLY"],
changes_not_sent_for_review: ENV["CHANGES_NOT_SENT_FOR_REVIEW"],
rescue_changes_not_sent_for_review: ENV["RESCUE_CHANGES_NOT_SENT_FOR_REVIEW"],
ack_bundle_installation_warning: ENV["ACK_BUNDLE_INSTALLATION_WARNING"]
)
end
end
EOL
;;
# android-metadata-init
android-metadata-init)
projRoot=$1
cd $projRoot && fastlane supply init
;;
# ios fastlane init
ios-env-create)
projRoot=$1
#해당 구간 수정필요
applekeyid=$5
appleissuerid=$6
appleAppStoreConnectKeyFile=$7
cat > $projRoot/fastlane/.env << EOL
ASC_ISSUER_ID=$appleissuerid
ASC_KEY_ID=$applekeyid
ASC_KEY_FILEPATH=$appleAppStoreConnectKeyFile
BUILD_APP_FILE_PATH=$3$4
BUILD_APP_BUILD_NUMBER=$8
SKIP_SUBMISSION=false
SKIP_WAITING_FOR_BUILD_PROCESSING=false
DISTRIBUTE_ONLY=false
USES_NON_EXEMPT_ENCRYPTION=false
DISTRIBUTE_EXTERNAL=false
EXPIRE_PREVIOUS_BUILDS=false
REJECT_BUILD_WAITING_FOR_REVIEW=false
SUBMIT_BETA_REVIEW=true
EOL
;;
ios-init)
projRoot=$1
cd $projRoot && fastlane init << EOF
4
EOF
;;
ios-fastfile-create)
projRoot=$1
projectname=$3
cat > $projRoot/fastlane/Fastfile << EOL
# 소스 코드 생성
default_platform(:ios)
platform :ios do
desc "Description of what the lane does"
lane :beta do
# add actions here: https://docs.fastlane.tools/actions
#unlock_keychain(
#path:"~/Library/Keychains/login.keychain-db", # .env 처리
#password:"" # .env 처리
#)
api_key = app_store_connect_api_key(
key_id: ENV["ASC_KEY_ID"],
issuer_id: ENV["ASC_ISSUER_ID"],
key_filepath: ENV["ASC_KEY_FILEPATH"],
in_house: false
)
upload_to_testflight(
api_key: api_key,
build_number: ENV["BUILD_APP_BUILD_NUMBER"],
ipa: ENV["BUILD_APP_FILE_PATH"],
skip_submission: ENV["SKIP_SUBMISSION"],
skip_waiting_for_build_processing: ENV["SKIP_WAITING_FOR_BUILD_PROCESSING"],
distribute_only: ENV["DISTRIBUTE_ONLY"],
uses_non_exempt_encryption: ENV["USES_NON_EXEMPT_ENCRYPTION"],
distribute_external: ENV["DISTRIBUTE_EXTERNAL"],
expire_previous_builds: ENV["EXPIRE_PREVIOUS_BUILDS"],
reject_build_waiting_for_review: ENV["REJECT_BUILD_WAITING_FOR_REVIEW"],
submit_beta_review: ENV["SUBMIT_BETA_REVIEW"]
)
end
lane :deploy do
#unlock_keychain(
#path:"/Users/jeongsikkim/Library/Keychains/login.keychain-db", # .env 처리
#password:"Rlatlr-7763" # .env 처리
#)
api_key = app_store_connect_api_key(
key_id: ENV["ASC_KEY_ID"],
issuer_id: ENV["ASC_ISSUER_ID"],
key_filepath: ENV["ASC_KEY_FILEPATH"],
in_house: false
)
upload_to_app_store(
api_key: api_key,
ipa: ENV["BUILD_APP_FILE_PATH"],
skip_metadata: true,
skip_screenshots: true,
force: true
)
end
end
EOL
;;
ios-metadata-init)
projRoot=$1
cd $projRoot && fastlane deliver init --api_key_path $3 --app_identifier $4 << EOF
y
y
EOF
;;
clean)
projRoot=$1
echo `cd $projRoot && ./gradlew $2`
;;

esac