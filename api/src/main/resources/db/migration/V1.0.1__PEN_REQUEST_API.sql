INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO_TYPE_CODE (PEN_RETRIEVAL_REQUEST_MACRO_TYPE_CODE, LABEL, DESCRIPTION,
                                                   DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER, CREATE_DATE,
                                                   UPDATE_USER, UPDATE_DATE)
values ('COMPLETE', 'Complete Reason Macro', 'Macros used when completing a PEN Retrieval Request', 3,
        to_date('2020-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        to_date('2099-12-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MVILLENE',
        to_date('2020-04-02 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MVILLENE',
        to_date('2020-04-02 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

DELETE
FROM PEN_RETRIEVAL_REQUEST_MACRO;

-- PEN Retrieval Request Macro
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'PCN',
        'A PEN number can not be located using the information in your PEN request.' || CHR(10) || CHR(10) ||
        'Please provide all other given names or surnames you have previously used or advise if you have never used any other names.',
        'MOREINFO', 'IDIR/JOCOX', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/JOCOX',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'PID',
        'To continue with your PEN request upload an IMG or PDF of your current Government Issued photo Identification (ID).' ||
        CHR(10) || CHR(10) ||
        'NOTE: If the name listed on the ID you upload is different from what''s in the PEN system, we will update our data to match. ID is covered by the B.C. Freedom of Information Protection of Privacy.',
        'MOREINFO', 'IDIR/JOCOX', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/JOCOX',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'SOA',
        'To continue with your PEN request please confirm the last B.C. Schools you attended or graduated from, including any applications to B.C. Post Secondary Institutions',
        'MOREINFO', 'IDIR/JOCOX', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/JOCOX',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'NPF',
        'A PEN number cannot be located using the information in your PEN request.' || CHR(10) || CHR(10) ||
        'For additional information visit: https://www2.gov.bc.ca/gov/content?id=74E29C67215B4988ABCD778F453A3129.' ||
        CHR(10) || CHR(10) ||
        'You do not require a PEN for an application to a B.C. school or PSI, a PEN will be assigned upon registration.',
        'REJECT', 'IDIR/JOCOX', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/JOCOX',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'OOP',
        'The information provided in your PEN request indicates you may not have attended a B.C. School or public Post-Secondary Institution (PSI).' ||
        CHR(10) || CHR(10) ||
        'You do not require a PEN for an application to a B.C. school or PSI, a PEN will be assigned upon registration.' ||
        CHR(10) || CHR(10) ||
        'For additional information visit: https://www2.gov.bc.ca/gov/content?id=74E29C67215B4988ABCD778F453A3129',
        'REJECT', 'IDIR/JOCOX', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/JOCOX',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'XPR',
        'The identity of the person making the request cannot be confirmed as the same as the PEN owner.' || CHR(10) ||
        CHR(10) ||
        'Under the B.C. Freedom of Information Protection of Privacy Act, the PEN number can only be provided to the person assigned the PEN, that person''s current or future school, or that person''s parent or guardian.' ||
        CHR(10) || CHR(10) ||
        'For additional information visit: https://www2.gov.bc.ca/gov/content?id=74E29C67215B4988ABCD778F453A3129',
        'REJECT', 'IDIR/JOCOX', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/JOCOX',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));


INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'NME',
        'Based on the information you have provided, we have updated your Legal Name format in the PEN system now.',
        'COMPLETE', 'IDIR/MVILLENE', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MVILLENE',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'NMG',
        'Based on the information you have provided, we have updated your Legal Name format and Gender in the PEN system now.',
        'COMPLETE', 'IDIR/MVILLENE', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MVILLENE',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PEN_RETRIEVAL_REQUEST_MACRO (PEN_RETRIEVAL_REQUEST_MACRO_ID, MACRO_CODE, MACRO_TEXT, MACRO_TYPE_CODE,
                                         CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES (sys_guid(), 'DOB',
        'Based on the information you have provided, we have updated your Date of Birth in the PEN system now.',
        'COMPLETE', 'IDIR/MVILLENE', to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'IDIR/MVILLENE',
        to_date('2020-04-06 00:00:00', 'YYYY-MM-DD HH24:MI:SS'));

--Add Columns to PEN_RETRIEVAL_REQUEST
ALTER TABLE PEN_RETRIEVAL_REQUEST
    ADD (
        DEMOG_CHANGED VARCHAR2(1),
        COMPLETE_COMMENT VARCHAR2(4000)
        );

COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST.EMAIL_VERIFIED IS 'Short value indicating whether the email of the student has been verified.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST.DEMOG_CHANGED IS 'Short value indicating whether the demographic information reported to PEN has been updated when completing PEN Retrieval Requests.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST.COMPLETE_COMMENT IS 'Free text message entered by PEN Staff when completing PEN Retrieval Requests.';
