#!/bin/bash

if ["$1" = "local"]
then
  git pull
  docker-compose down
  docker image prune -f
  docker volume prune -f
  docker-compose build --no-cache
  docker-compose up -d

else
  echo "plz put in profile"

fi