[![CodeQL](https://github.com/marcuslull/Auth/actions/workflows/codeql.yml/badge.svg)](https://github.com/marcuslull/Auth/actions/workflows/codeql.yml)
[![Java CI with Maven](https://github.com/marcuslull/Auth/actions/workflows/maven.yml/badge.svg?branch=dev)](https://github.com/marcuslull/Auth/actions/workflows/maven.yml)

# Auth Service

## An Authentication and Authorization Service
#### NOT READY FOR PRODUCTION
**Example:**
https://auth.marcuslull.com
___

## About
**Language, Frameworks, and Libraries**
* Java (21)
* Maven (3.9.5)
* Spring Boot(3.2.4)
    * spring-boot-starter-oauth2-authorization-server
    * spring-boot-starter-security
    * spring-boot-starter-data-jpa
    * spring-boot-starter-test
    * spring-security-test
    * spring-boot-starter-thymeleaf
    * spring-boot-starter-mail
* Postgresql
* Project Lombok
* BouncyCastle bcprov-jdk18on (1.78.1)

**Current build workflow**  
GitFlow: Feature >> Dev >> Main(protected)  
Features are developed on a named Feature branch then merged into Dev. Prior to the merge the feature is required to 
pass Maven Clean, Test, and Verify lifecycles as well as the GitHub CodeQL test suite.  
Once a suitable suite of features has been merged into Dev a merge to Main can be triggered where the same Maven build 
and CodeQL tests are ran again. The Merge to main triggers a Google Cloud Build job to orchestrate a deployment via 
Google Cloud Artifact registry.  
The app and all dependencies are packaged in a Docker container where they are deployed as a Google Cloud Run service.  

**License**  
&copy; 2024 MJL Apps. All rights reserved.  
Open Source Project | <a href="https://www.apache.org/licenses/LICENSE-2.0">https://www.apache.org/licenses/LICENSE-2.0</a>

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and limitations under the License.
___

## Motivation
A common theme among most of the apps I have written is the need for an authE/authO service. Rather than cobbling 
something together with each new app or integrating with a 3rd party solution I have decided to use this as a learning 
opportunity. I have an interest and many years of professional experience with IT security, so I thought this would be a 
suitable challenge, a great portfolio project and a useful tool for future applications.
___

## Description
An implementation of the OAuth 2.0 Framework using JSON Web Tokens (JWT) with authorization code and client credential grant 
types. These grant types are additionally secured with the Proof Key Code Exchange (PKCE) extension to provide further
protection against Cross Site Request Forgery (CSRF) and injection attacks.  
The JWTs are OpenID Connect 1.0 compliant which may expose authentication claims of an end-user

**More Information**  
<a href="https://oauth.net/2/">https://oauth.net/2/</a>  
<a href="https://openid.net/">https://openid.net/</a>  
<a href="https://github.com/P-H-C/phc-winner-argon2/blob/master/argon2-specs.pdf">https://github.com/P-H-C/phc-winner-argon2/blob/master/argon2-specs.pdf</a>
___

## Current Release (v0.1.0)
#### NOT READY FOR PRODUCTION
* Added basic authentication workflows
  * Login/logout
  * Email verification
  * Update password
* Added a custom user details service to facilitate persistence
* Added PostgreSQL support for user account persistence
* Added support for password hashing with the Argon2id cryptographic hashing algorithm
* Added support for logging via a Logback rolling size and time based log files
* Added unit and integration tests for controller and service classes
* Added Thymeleaf UI template pages along with common fragments to support the implemented workflows
* Current security filter supports CSRF, event monitoring, authorization exceptions and custom login/logout pages
* Email account verification via shared UUID with short time to live
* Password resets via shared UUID with short time to live

