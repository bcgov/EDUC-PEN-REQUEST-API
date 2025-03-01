ENVIRONMENT=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3
DB_JDBC_CONNECT_STRING=$5
DB_PWD=$6
DB_USER=$7
SPLUNK_TOKEN=$8

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"

SOAM_KC_LOAD_USER_ADMIN=$(oc -n "$OPENSHIFT_NAMESPACE"-"$ENVIRONMENT" -o json get secret sso-admin-"${ENVIRONMENT}" | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -n "$OPENSHIFT_NAMESPACE"-"$ENVIRONMENT" -o json get secret sso-admin-"${ENVIRONMENT}" | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)

SOAM_KC=soam-$ENVIRONMENT.apps.silver.devops.gov.bc.ca
NATS_CLUSTER=educ_nats_cluster
NATS_URL="nats://nats.${OPENSHIFT_NAMESPACE}-${ENVIRONMENT}.svc.cluster.local:4222"

echo Fetching SOAM token
TKN=$(curl -s \
  -d "client_id=admin-cli" \
  -d "username=$SOAM_KC_LOAD_USER_ADMIN" \
  -d "password=$SOAM_KC_LOAD_USER_PASS" \
  -d "grant_type=password" \
  "https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID/protocol/openid-connect/token" | jq -r '.access_token')

###########################################################
#Setup for scopes
###########################################################

echo
echo Writing scope READ_PEN_REQUEST
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Read scope for PEN request\",\"id\": \"READ_PEN_REQUEST\",\"name\": \"READ_PEN_REQUEST\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_PEN_REQUEST
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Write scope for PEN request\",\"id\": \"WRITE_PEN_REQUEST\",\"name\": \"WRITE_PEN_REQUEST\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DOCUMENT
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DOCUMENT\",\"name\": \"READ_DOCUMENT\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DOCUMENT_REQUIREMENTS
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DOCUMENT_REQUIREMENTS\",\"name\": \"READ_DOCUMENT_REQUIREMENTS\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_DOCUMENT
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"WRITE_DOCUMENT\",\"name\": \"WRITE_DOCUMENT\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_DOCUMENT_TYPES
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DOCUMENT_TYPES\",\"name\": \"READ_DOCUMENT_TYPES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_PEN_REQUEST_STATUSES
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"READ_PEN_REQUEST_STATUSES\",\"name\": \"READ_PEN_REQUEST_STATUSES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_PEN_REQUEST_CODES
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"READ_PEN_REQUEST_CODES\",\"name\": \"READ_PEN_REQUEST_CODES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope READ_PEN_REQ_MACRO
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM read pen request macro scope\",\"id\": \"READ_PEN_REQ_MACRO\",\"name\": \"READ_PEN_REQ_MACRO\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope WRITE_PEN_REQ_MACRO
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM write pen request macro scope\",\"id\": \"WRITE_PEN_REQ_MACRO\",\"name\": \"WRITE_PEN_REQ_MACRO\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

echo
echo Writing scope DELETE_DOCUMENT
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"SOAM send email scope\",\"id\": \"DELETE_DOCUMENT\",\"name\": \"DELETE_DOCUMENT\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#SCOPE_READ_PEN_REQUEST_STATS
echo
echo Writing scope READ_PEN_REQUEST_STATS
curl -sX POST "https://$SOAM_KC/auth/admin/realms/$SOAM_KC_REALM_ID/client-scopes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TKN" \
  -d "{\"description\": \"Scope to query stats for reporting\",\"id\": \"READ_PEN_REQUEST_STATS\",\"name\": \"READ_PEN_REQUEST_STATS\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
###########################################################
#Setup for config-map
###########################################################
SPLUNK_URL="gww.splunk.educ.gov.bc.ca"
FLB_CONFIG="[SERVICE]
   Flush        1
   Daemon       Off
   Log_Level    debug
   HTTP_Server   On
   HTTP_Listen   0.0.0.0
   Parsers_File parsers.conf
[INPUT]
   Name   tail
   Path   /mnt/log/*
   Exclude_Path *.gz,*.zip
   Parser docker
   Mem_Buf_Limit 20MB
[FILTER]
   Name record_modifier
   Match *
   Record hostname \${HOSTNAME}
[OUTPUT]
   Name   stdout
   Match  *
[OUTPUT]
   Name  splunk
   Match *
   Host  $SPLUNK_URL
   Port  443
   TLS         On
   TLS.Verify  Off
   Message_Key $APP_NAME
   Splunk_Token $SPLUNK_TOKEN
"
PARSER_CONFIG="
[PARSER]
    Name        docker
    Format      json
"
echo
echo Creating config map "$APP_NAME-config-map"
oc create -n "$OPENSHIFT_NAMESPACE-$ENVIRONMENT" configmap \
  "$APP_NAME-config-map" \
  --from-literal=TZ=$TZVALUE \
  --from-literal=TOKEN_ISSUER_URL="https://$SOAM_KC/auth/realms/$SOAM_KC_REALM_ID" \
  --from-literal=NATS_URL="$NATS_URL" \
  --from-literal=NATS_CLUSTER=$NATS_CLUSTER \
  --from-literal=JDBC_URL="$DB_JDBC_CONNECT_STRING" \
  --from-literal=ORACLE_USERNAME="$DB_USER" \
  --from-literal=ORACLE_PASSWORD="$DB_PWD" \
  --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO \
  --from-literal=SPRING_WEB_LOG_LEVEL=INFO \
  --from-literal=APP_LOG_LEVEL=INFO \
  --from-literal=HIBERNATE_STATISTICS=false \
  --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO \
  --from-literal=SPRING_SHOW_REQUEST_DETAILS=false \
  --from-literal=FILE_EXTENSIONS="image/jpeg,image/png,application/pdf,.jpg,.jpeg,.jpe,.jfif,.jif,.jfi" \
  --from-literal=FILE_MAXSIZE=10485760 \
  --from-literal=FILE_MAX_ENCODED_SIZE=15485760 \
  --from-literal=BCSC_AUTO_MATCH_OUTCOMES="RIGHTPEN,WRONGPEN,ZEROMATCHES,MANYMATCHES,ONEMATCH" \
  --from-literal=REMOVE_BLOB_CONTENTS_DOCUMENT_AFTER_DAYS="365" \
  --from-literal=SCHEDULED_JOBS_REMOVE_BLOB_CONTENTS_DOCUMENT_CRON="@midnight" \
  --from-literal=NATS_MAX_RECONNECT=60 \
  --from-literal=PURGE_RECORDS_EVENT_AFTER_DAYS=365 \
  --from-literal=SCHEDULED_JOBS_PURGE_OLD_EVENT_RECORDS_CRON="@midnight" \
  --dry-run=client -o yaml | oc apply -f -
echo

echo Setting environment variables for "$APP_NAME-$ENVIRONMENT" application
oc -n "$OPENSHIFT_NAMESPACE-$ENVIRONMENT" set env \
  --from="configmap/$APP_NAME-config-map" "deployment/$APP_NAME-$ENVIRONMENT"

echo Creating config map "$APP_NAME-flb-sc-config-map"
oc create -n "$OPENSHIFT_NAMESPACE-$ENVIRONMENT" configmap \
  "$APP_NAME-flb-sc-config-map" \
  --from-literal=fluent-bit.conf="$FLB_CONFIG" \
  --from-literal=parsers.conf="$PARSER_CONFIG" \
  --dry-run=client -o yaml | oc apply -f -

echo Removing un-needed config entries
oc -n "$OPENSHIFT_NAMESPACE-$ENVIRONMENT" set env \
  "deployment/$APP_NAME-$ENVIRONMENT" KEYCLOAK_PUBLIC_KEY-
