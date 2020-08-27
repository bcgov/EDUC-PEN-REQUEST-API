# EDUC-PEN-REQUEST-API
The backend API logic for the PEN Request application

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=bcgov_EDUC-PEN-REQUEST-API)](https://sonarcloud.io/dashboard?id=bcgov_EDUC-PEN-REQUEST-API)

## Documentation
* [Online PEN Request API Spec](https://penrequest.docs.apiary.io/)
* [PEN Registry Wiki](https://github.com/bcgov/EDUC-PEN/wiki)

## Before deployment
In order to deploy this project into OpenShift, you must create a config-map by running the following command (be sure to replace the values in curly brackets with actual values):
``` sh
oc create -n {YOUR_OPENSHIFT_ENVIRONMENT} configmap pen-request-api-config 
--from-literal=HIBERNATE=DEBUG
--from-literal=PEN_REQUEST_LOG_LEVEL=DEBUG
--from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=DEBUG
--from-literal=SPRING_SECURITY_LOG_LEVEL=DEBUG
--from-literal=SPRING_WEB_LOG_LEVEL=DEBUG
--from-literal=JDBC_URL={JDBC URL}
--from-literal=KEYCLOAK_PUBLIC_KEY={PUBLIC KEY}
--from-literal=ORACLE_PASS={ORACLE DB PASSWORD}
--from-literal=ORACLE_USER={ORACLE DB USERNAME}
--from-literal=PORT_NUMBER={PORT NUMBER}
```

## Environment Variables
The following is a list of all environment variables consumed by the PEN Request Service

| Environment Variables            | Description                                                      |
|----------------------------------|:-----------------------------------------------------------------|
| HIBERNATE                        | Log level for hibernate                                          |
| PEN_REQUEST_LOG_LEVEL            | Log level for API elements                                       |
| SPRING_BOOT_AUTOCONFIG_LOG_LEVEL | Log level for Spring Boot autoconfig                             |
| SPRING_SECURITY_LOG_LEVEL        | Log level for Spring Security                                    |
| SPRING_WEB_LOG_LEVEL             | Log level for Spring Web                                         |
| JDBC_URL                         | The JDBC URL for the Oracle Database you are connecting to       |
| KEYCLOAK_PUBLIC_KEY              | Public key for the SOAM keycloak instance                        |
| ORACLE_PASS                      | The password for your oracle database                            |
| ORACLE_USER                      | Username for your oracle database                                |
| PORT_NUMBER                      | The port on which to serve the application                       |
