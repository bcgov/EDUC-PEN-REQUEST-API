--Modify EVENT_PAYLOAD Column to BLOB
ALTER TABLE PEN_RETRIEVAL_REQUEST_EVENT
    ADD (
        EVENT_PAYLOAD_BLOB BLOB
        ) LOB (EVENT_PAYLOAD_BLOB) STORE AS EVENT_PAYLOAD_BLOB (TABLESPACE API_PEN_RETRIEVAL_BLOB_DATA);
UPDATE PEN_RETRIEVAL_REQUEST_EVENT SET EVENT_PAYLOAD_BLOB = UTL_RAW.CAST_TO_RAW(EVENT_PAYLOAD);
ALTER TABLE PEN_RETRIEVAL_REQUEST_EVENT DROP COLUMN EVENT_PAYLOAD;
ALTER TABLE PEN_RETRIEVAL_REQUEST_EVENT RENAME COLUMN EVENT_PAYLOAD_BLOB TO EVENT_PAYLOAD;
ALTER TABLE PEN_RETRIEVAL_REQUEST_EVENT
    MODIFY (
        EVENT_PAYLOAD NOT NULL
        );