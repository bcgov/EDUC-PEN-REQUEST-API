UPDATE PEN_RETRIEVAL_REQUEST_GENDER_CODE
SET EXPIRY_DATE = SYSDATE
WHERE GENDER_CODE in ('X','U');
