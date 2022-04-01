#!/usr/bin/env bash

docker build -t hello-proxy - <<DOCKER
FROM php:7.4-apache
RUN a2enmod rewrite
COPY . /var/www/html
DOCKER

docker run -d -p 8088:80 --rm --name hello-proxy -v "$PWD":/var/www/html hello-proxy

curl --location --request GET 'http://localhost:8088/api.clickup.com/api/v2/user' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: pk_<ACCESS-TOKEN>'

docker rm -f hello-proxy
