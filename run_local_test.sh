#!/bin/bash
./gradlew build
java -jar NATT.jar -nc https://gitlab.com/ap5vs/test-config/restful-api-server-config/-/raw/main/test-config-6.yaml?ref_type=heads
