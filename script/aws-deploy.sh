REPOSITORY=/home/ec2-user/shared-food
PROJECT_NAME=Android-Team-2-Backend
API_MODULE_NAME=module-api

cd $REPOSITORY/$PROJECT_NAME/

echo "> Git Pull"

git pull origin dev

echo "> 프로젝트 build 시작"

chmod +x ./gradlew

./gradlew clean :module-api:build

echo "> 기본 dir 이동"

cd $REPOSITORY

echo "> Build 파일 복사"

cp $REPOSITORY/$PROJECT_NAME/API_MODULE_NAME/build/lib/*.jar $REPOSITORY/

echo "> 이동 SUCCESS"

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=${pgrep-f ${PROJECT_NAME}.*jar}

echo "현재 구동중인 애플리케이션 pid : $CURRENT_PID"

if [ -z "$CURRENT_PID"]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행 권한 부여"

nohup java -jar $REPOSITORY/$JAR_NAME --spring.profiles.active=dev --spring.datasource.url=jdbc:mysql://172.17.0.2:3306/dev?serverTimezone=UTC 2>&1 &
