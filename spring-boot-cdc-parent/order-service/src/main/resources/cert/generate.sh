#!/usr/bin/env bash
keytool -genkeypair -alias order -keyalg RSA -keysize 4096 -validity 3650 -dname "CN=localhost" -keypass secret -keystore order.p12 -storeType PKCS12 -storepass secret
