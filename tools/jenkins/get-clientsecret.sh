envValue=$1
OPENSHIFT_NAMESPACE=$2

SOAM_KC_REALM_ID="master"
KCADM_FILE_BIN_FOLDER="/tmp/keycloak-9.0.3/bin"

oc project "$OPENSHIFT_NAMESPACE"-"$envValue"

SOAM_KC_LOAD_USER_ADMIN=$(oc -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"username": "\(.*\)"/\1/p' | base64 --decode)
SOAM_KC_LOAD_USER_PASS=$(oc -o json get secret sso-admin-"${envValue}" | sed -n 's/.*"password": "\(.*\)",/\1/p' | base64 --decode)
SOAM_KC=$OPENSHIFT_NAMESPACE-$envValue.pathfinder.gov.bc.ca

$KCADM_FILE_BIN_FOLDER/kcadm.sh config credentials --server https://$SOAM_KC/auth --realm $SOAM_KC_REALM_ID --user "$SOAM_KC_LOAD_USER_ADMIN" --password "$SOAM_KC_LOAD_USER_PASS"

getPenRequestItClientID(){
    executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get clients -r $SOAM_KC_REALM_ID --fields 'id,clientId' | grep -B2 '"clientId" : "pen-request-api-it"' | grep -Po "(\{){0,1}[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}(\}){0,1}"
}
penRequestItClientID=$(getPenRequestItClientID)

getPenRequestItClientSecret(){
    executorID= $KCADM_FILE_BIN_FOLDER/kcadm.sh get clients/$penRequestItClientID/client-secret -r $SOAM_KC_REALM_ID | grep -Po "(\{){0,1}[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}(\}){0,1}"
}
penRequestItClientSecret=$(getPenRequestItClientSecret)
echo penRequestItClientSecret