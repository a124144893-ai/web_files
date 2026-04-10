#!/bin/bash
set -e

java -jar /home/ec2-user/ykm/web_files/movie-booking-app-1.0.0.jar \
  --spring.config.location=/home/ec2-user/ykm/web_files/application-prod.yml \
  --spring.profiles.active=prod