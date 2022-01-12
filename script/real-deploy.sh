BUILD_JAR=$(ls /root/shared-food/shared-food/*.jar)
echo "> buil real path: $BUILD_JAR" >>/root/shared-food/deploy.log
JAR_NAME=$(basename $BUILD_JAR)
echo "> build 파일명: $JAR_NAME" >>/root/shared-food/deploy.log

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >>/root/shared-food/deploy.log
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME

echo "> DEPLOY_JAR 배포" >>/root/shared-food/deploy.log

nohup java -jar $DEPLOY_JAR --spring.profiles.active=prod 2>&1 &
echo "> 배포 성공" >>/root/shared-food/deploy.log
