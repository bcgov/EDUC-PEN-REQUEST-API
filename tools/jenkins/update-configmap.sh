envValue=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3
APP_NAME_UPPER=${APP_NAME^^}

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"
KCADM_FILE_BIN_FOLDER="/tmp/keycloak-9.0.3/bin"

oc project "$OPENSHIFT_NAMESPACE"-"$envValue"

SOAM_KC_LOAD_USER_ADMIN=$(oc -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)
DB_JDBC_CONNECT_STRING=$(oc -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n 's/.*"DB_JDBC_CONNECT_STRING": "\(.*\)",/\1/p')
DB_PWD=$(oc -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n "s/.*\"DB_PWD_${APP_NAME_UPPER}\": \"\(.*\)\",/\1/p")
DB_USER=$(oc -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n "s/.*\"DB_USER_${APP_NAME_UPPER}\": \"\(.*\)\",/\1/p")
SPLUNK_TOKEN=$(oc -o json get configmaps "${APP_NAME}"-"${envValue}"-setup-config | sed -n "s/.*\"SPLUNK_TOKEN_${APP_NAME_UPPER}\": \"\(.*\)\"/\1/p")
SOAM_KC=soam-$envValue.apps.silver.devops.gov.bc.ca
NATS_CLUSTER=educ_nats_cluster
NATS_URL="nats://nats.${OPENSHIFT_NAMESPACE}-${envValue}.svc.cluster.local:4222"

oc project "$OPENSHIFT_NAMESPACE"-tools

###########################################################
#Fetch the public key
###########################################################
$KCADM_FILE_BIN_FOLDER/kcadm.sh config credentials --server https://$SOAM_KC/auth --realm $SOAM_KC_REALM_ID --user "$SOAM_KC_LOAD_USER_ADMIN" --password "$SOAM_KC_LOAD_USER_PASS"
getPublicKey(){
    # shellcheck disable=SC1007
    executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get keys -r $SOAM_KC_REALM_ID | grep -Po 'publicKey" : "\K([^"]*)'
}

echo Fetching public key from SOAM
soamFullPublicKey="-----BEGIN PUBLIC KEY----- $(getPublicKey) -----END PUBLIC KEY-----"

###########################################################
#Setup for scopes
###########################################################

#READ_PEN_REQUEST
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"Read scope for PEN request\",\"id\": \"READ_PEN_REQUEST\",\"name\": \"READ_PEN_REQUEST\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#WRITE_PEN_REQUEST
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"Write scope for PEN request\",\"id\": \"WRITE_PEN_REQUEST\",\"name\": \"WRITE_PEN_REQUEST\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#READ_DOCUMENT
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DOCUMENT\",\"name\": \"READ_DOCUMENT\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#READ_DOCUMENT_REQUIREMENTS
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DOCUMENT_REQUIREMENTS\",\"name\": \"READ_DOCUMENT_REQUIREMENTS\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#WRITE_DOCUMENT
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"WRITE_DOCUMENT\",\"name\": \"WRITE_DOCUMENT\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#READ_DOCUMENT_TYPES
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"READ_DOCUMENT_TYPES\",\"name\": \"READ_DOCUMENT_TYPES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#READ_PEN_REQUEST_STATUSES
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"READ_PEN_REQUEST_STATUSES\",\"name\": \"READ_PEN_REQUEST_STATUSES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#READ_PEN_REQUEST_CODES
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"READ_PEN_REQUEST_CODES\",\"name\": \"READ_PEN_REQUEST_CODES\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#READ_PEN_REQ_MACRO
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM read pen request macro scope\",\"id\": \"READ_PEN_REQ_MACRO\",\"name\": \"READ_PEN_REQ_MACRO\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#WRITE_PEN_REQ_MACRO
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM write pen request macro scope\",\"id\": \"WRITE_PEN_REQ_MACRO\",\"name\": \"WRITE_PEN_REQ_MACRO\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"
#DELETE_DOCUMENT
$KCADM_FILE_BIN_FOLDER/kcadm.sh create client-scopes -r $SOAM_KC_REALM_ID --body "{\"description\": \"SOAM send email scope\",\"id\": \"DELETE_DOCUMENT\",\"name\": \"DELETE_DOCUMENT\",\"protocol\": \"openid-connect\",\"attributes\" : {\"include.in.token.scope\" : \"true\",\"display.on.consent.screen\" : \"false\"}}"

###########################################################
#Setup for clients
###########################################################

if [ "$envValue" = "dev" ]
then
  getPenRequestItClientID(){
      executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get clients -r $SOAM_KC_REALM_ID --fields 'id,clientId' | grep -B2 '"clientId" : "pen-request-api-it"' | grep -Po "(\{){0,1}[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}(\}){0,1}"
  }
  penRequestItClientID=$(getPenRequestItClientID)
  
  echo Removing PEN Request IT client if exists
  $KCADM_FILE_BIN_FOLDER/kcadm.sh delete clients/$penRequestItClientID -r $SOAM_KC_REALM_ID
  
  echo Creating pen-request-api-it Keycloak client
  $KCADM_FILE_BIN_FOLDER/kcadm.sh create clients -r $SOAM_KC_REALM_ID --body "{\"clientId\" : \"pen-request-api-it\", \"name\" : \"Pen Request API IT\", \"description\" : \"Pen Request API Client for Integration Testing\", \"surrogateAuthRequired\" : false, \"enabled\" : true, \"clientAuthenticatorType\" : \"client-secret\", \"redirectUris\" : [ ], \"webOrigins\" : [ ], \"notBefore\" : 0, \"bearerOnly\" : false, \"consentRequired\" : false, \"standardFlowEnabled\" : false, \"implicitFlowEnabled\" : false, \"directAccessGrantsEnabled\" : false, \"serviceAccountsEnabled\" : true, \"publicClient\" : false, \"frontchannelLogout\" : false, \"protocol\" : \"openid-connect\", \"attributes\" : { \"saml.assertion.signature\" : \"false\", \"saml.multivalued.roles\" : \"false\", \"saml.force.post.binding\" : \"false\", \"saml.encrypt\" : \"false\", \"saml.server.signature\" : \"false\", \"saml.server.signature.keyinfo.ext\" : \"false\", \"exclude.session.state.from.auth.response\" : \"false\", \"saml_force_name_id_format\" : \"false\", \"saml.client.signature\" : \"false\", \"tls.client.certificate.bound.access.tokens\" : \"false\", \"saml.authnstatement\" : \"false\", \"display.on.consent.screen\" : \"false\", \"saml.onetimeuse.condition\" : \"false\" }, \"authenticationFlowBindingOverrides\" : { }, \"fullScopeAllowed\" : true, \"nodeReRegistrationTimeout\" : -1, \"protocolMappers\" : [ { \"name\" : \"last_name\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-usermodel-attribute-mapper\", \"consentRequired\" : false, \"config\" : {\"userinfo.token.claim\" : \"true\",\"user.attribute\" : \"last_name\",\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"claim.name\" : \"last_name\",\"jsonType.label\" : \"String\" } }, { \"name\" : \"first_name\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-usermodel-attribute-mapper\", \"consentRequired\" : false, \"config\" : {\"userinfo.token.claim\" : \"true\",\"user.attribute\" : \"first_name\",\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"claim.name\" : \"first_name\",\"jsonType.label\" : \"String\" } }, { \"name\" : \"middle_names\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-usermodel-attribute-mapper\", \"consentRequired\" : false, \"config\" : {\"userinfo.token.claim\" : \"true\",\"user.attribute\" : \"middle_names\",\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"claim.name\" : \"middle_names\",\"jsonType.label\" : \"String\" } }, { \"name\" : \"SOAM Mapper\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-soam-mapper\", \"consentRequired\" : false, \"config\" : {\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"userinfo.token.claim\" : \"true\" } }, { \"name\" : \"idir_guid\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-usermodel-attribute-mapper\", \"consentRequired\" : false, \"config\" : {\"userinfo.token.claim\" : \"true\",\"user.attribute\" : \"idir_guid\",\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"claim.name\" : \"idir_guid\",\"jsonType.label\" : \"String\" } }, { \"name\" : \"bceid_guid\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-usermodel-attribute-mapper\", \"consentRequired\" : false, \"config\" : {\"userinfo.token.claim\" : \"true\",\"user.attribute\" : \"bceid_guid\",\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"claim.name\" : \"bceid_guid\",\"jsonType.label\" : \"String\" } }, { \"name\" : \"email_address\", \"protocol\" : \"openid-connect\", \"protocolMapper\" : \"oidc-usermodel-attribute-mapper\", \"consentRequired\" : false, \"config\" : {\"userinfo.token.claim\" : \"true\",\"user.attribute\" : \"email_address\",\"id.token.claim\" : \"true\",\"access.token.claim\" : \"true\",\"claim.name\" : \"email_address\",\"jsonType.label\" : \"String\" } } ], \"defaultClientScopes\" : [ \"web-origins\", \"role_list\", \"READ_PEN_REQUEST\", \"WRITE_PEN_REQUEST\", \"READ_PEN_REQUEST_STATUSES\", \"profile\", \"roles\", \"email\", \"READ_PEN_REQUEST_CODES\", \"DELETE_PEN_REQUEST\", \"READ_DOCUMENT\", \"WRITE_DOCUMENT\", \"DELETE_DOCUMENT\", \"READ_DOCUMENT_REQUIREMENTS\", \"READ_DOCUMENT_TYPES\", \"READ_PEN_REQ_MACRO\", \"WRITE_PEN_REQ_MACRO\"], \"optionalClientScopes\" : [ \"address\", \"phone\", \"offline_access\" ], \"access\" : { \"view\" : true, \"configure\" : true, \"manage\" : true }}"
fi


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
   HTTP_Port     2020
[INPUT]
   Name   tail
   Path   /mnt/log/*
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

echo
echo Creating config map "$APP_NAME"-config-map
oc create -n "$OPENSHIFT_NAMESPACE"-"$envValue" configmap "$APP_NAME"-config-map --from-literal=TZ=$TZVALUE --from-literal=NATS_URL="$NATS_URL" --from-literal=NATS_CLUSTER=$NATS_CLUSTER --from-literal=JDBC_URL="$DB_JDBC_CONNECT_STRING" --from-literal=ORACLE_USERNAME="$DB_USER" --from-literal=ORACLE_PASSWORD="$DB_PWD" --from-literal=KEYCLOAK_PUBLIC_KEY="$soamFullPublicKey" --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO --from-literal=SPRING_WEB_LOG_LEVEL=INFO --from-literal=APP_LOG_LEVEL=INFO --from-literal=HIBERNATE_STATISTICS=false --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO --from-literal=SPRING_SHOW_REQUEST_DETAILS=false --from-literal=FILE_EXTENSIONS="image/jpeg,image/png,application/pdf,.jpg,.jpeg,.jpe,.jfif,.jif,.jfi" --from-literal=FILE_MAXSIZE=10485760 --from-literal=BCSC_AUTO_MATCH_OUTCOMES="RIGHTPEN,WRONGPEN,ZEROMATCHES,MANYMATCHES,ONEMATCH" --dry-run -o yaml | oc apply -f -
echo

echo Setting environment variables for "$APP_NAME"-$SOAM_KC_REALM_ID application
oc project "$OPENSHIFT_NAMESPACE"-"$envValue"
oc set env --from=configmap/"$APP_NAME"-config-map dc/"$APP_NAME"-$SOAM_KC_REALM_ID

echo Creating config map "$APP_NAME"-flb-sc-config-map
oc create -n "$OPENSHIFT_NAMESPACE"-"$envValue" configmap "$APP_NAME"-flb-sc-config-map --from-literal=fluent-bit.conf="$FLB_CONFIG"  --dry-run -o yaml | oc apply -f -