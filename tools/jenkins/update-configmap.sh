envValue=$1
APP_NAME=$2
OPENSHIFT_NAMESPACE=$3
APP_NAME_UPPER=${APP_NAME^^}

TZVALUE="America/Vancouver"
SOAM_KC_REALM_ID="master"
KCADM_FILE_BIN_FOLDER="/tmp/keycloak-9.0.3/bin"

oc project $OPENSHIFT_NAMESPACE-$envValue

SOAM_KC_LOAD_USER_ADMIN=$(oc -o json get secret sso-admin-${envValue} | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -o json get secret sso-admin-${envValue} | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)
DB_JDBC_CONNECT_STRING=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n 's/.*"DB_JDBC_CONNECT_STRING": "\(.*\)",/\1/p')
DB_PWD=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n "s/.*\"DB_PWD_${APP_NAME_UPPER}\": \"\(.*\)\",/\1/p")
DB_USER=$(oc -o json get configmaps ${APP_NAME}-${envValue}-setup-config | sed -n "s/.*\"DB_USER_${APP_NAME_UPPER}\": \"\(.*\)\"/\1/p")
SOAM_KC=$OPENSHIFT_NAMESPACE-$envValue.pathfinder.gov.bc.ca
NATS_CLUSTER=educ_pen_nats_cluster
NATS_URL="nats://nats.${OPENSHIFT_NAMESPACE}-${envValue}.svc.cluster.local:4222"

oc project $OPENSHIFT_NAMESPACE-tools

###########################################################
#Fetch the public key
###########################################################
$KCADM_FILE_BIN_FOLDER/kcadm.sh config credentials --server https://$SOAM_KC/auth --realm $SOAM_KC_REALM_ID --user $SOAM_KC_LOAD_USER_ADMIN --password $SOAM_KC_LOAD_USER_PASS
getPublicKey(){
    executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get keys -r $SOAM_KC_REALM_ID | grep -Po 'publicKey" : "\K([^"]*)'
}

echo Fetching public key from SOAM
soamFullPublicKey="-----BEGIN PUBLIC KEY----- $(getPublicKey) -----END PUBLIC KEY-----"
newline=$'\n'
formattedPublicKey="${soamFullPublicKey:0:26}${newline}${soamFullPublicKey:27:64}${newline}${soamFullPublicKey:91:64}${newline}${soamFullPublicKey:155:64}${newline}${soamFullPublicKey:219:64}${newline}${soamFullPublicKey:283:64}${newline}${soamFullPublicKey:347:64}${newline}${soamFullPublicKey:411:9}${newline}${soamFullPublicKey:420}"

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
#Setup for config-map
###########################################################
echo
echo Creating config map $APP_NAME-config-map 
oc create -n $OPENSHIFT_NAMESPACE-$envValue configmap $APP_NAME-config-map --from-literal=TZ=$TZVALUE --from-literal=NATS_URL=$NATS_URL --from-literal=NATS_CLUSTER=$NATS_CLUSTER --from-literal=JDBC_URL=$DB_JDBC_CONNECT_STRING --from-literal=ORACLE_USERNAME="$DB_USER" --from-literal=ORACLE_PASSWORD="$DB_PWD" --from-literal=KEYCLOAK_PUBLIC_KEY="$soamFullPublicKey" --from-literal=SPRING_SECURITY_LOG_LEVEL=INFO --from-literal=SPRING_WEB_LOG_LEVEL=INFO --from-literal=APP_LOG_LEVEL=INFO --from-literal=HIBERNATE_STATISTICS=false --from-literal=SPRING_BOOT_AUTOCONFIG_LOG_LEVEL=INFO --from-literal=SPRING_SHOW_REQUEST_DETAILS=false --from-literal=FILE_EXTENSIONS="image/jpeg,image/png,application/pdf,.jpg,.jpeg,.jpe,.jfif,.jif,.jfi" --from-literal=FILE_MAXSIZE=10485760 --from-literal=BCSC_AUTO_MATCH_OUTCOMES="RIGHTPEN,WRONGPEN,ZEROMATCHES,MANYMATCHES,ONEMATCH" --dry-run -o yaml | oc apply -f -
echo
echo Setting environment variables for $APP_NAME-$SOAM_KC_REALM_ID application
oc project $OPENSHIFT_NAMESPACE-$envValue
oc set env --from=configmap/$APP_NAME-config-map dc/$APP_NAME-$SOAM_KC_REALM_ID