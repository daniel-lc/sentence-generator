#!/bin/bash

docker-compose up &

gradle server:bootRun
