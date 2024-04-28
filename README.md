[![CodeQL](https://github.com/marcuslull/Auth/actions/workflows/codeql.yml/badge.svg)](https://github.com/marcuslull/Auth/actions/workflows/codeql.yml)
[![Java CI with Maven](https://github.com/marcuslull/Auth/actions/workflows/maven.yml/badge.svg?branch=dev)](https://github.com/marcuslull/Auth/actions/workflows/maven.yml)

# Auth Service

## An Authentication and Authorization Service for my personal projects

  
**Languages**
* Java (21)
* Maven (3.9.5)
* Spring Boot(3.2.4)
    * spring-boot-starter-oauth2-authorization-server
    * spring-boot-starter-security
    * spring-boot-starter-data-jpa
    * spring-boot-starter-test
    * spring-security-test
    * lombok

  
**Current build workflow**
1. Docker
2. Google Cloud Build
3. Google Artifact Registry
4. Google Cloud Run

## Description
A common theme among most of the apps I have written is the need for an authE/authO service. Rather than cobbling something together with each new app I decided to develop a production grade solution.
