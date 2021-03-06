-- Table Comments
COMMENT ON TABLE FLYWAY_SCHEMA_HISTORY IS 'This table achieves the database migration history used by flwyway.';
COMMENT ON TABLE PEN_RETRIEVAL_REQUEST IS 'PEN Retrieval Request is a transaction record of a request by a student to retrieve their PEN.';
COMMENT ON TABLE PEN_RETRIEVAL_REQUEST_COMMENT IS 'Holds comments made by staff or students, related to PEN Retrieval Requests.';
COMMENT ON TABLE PEN_RETRIEVAL_REQUEST_EVENT IS 'This table achieves the events sent to messaging system by EventPoller.';
COMMENT ON TABLE PEN_RETRIEVAL_REQUEST_GENDER_CODE IS 'Gender Code lists the standard codes for Gender: Female, Male, Diverse.';

-- Column Comments
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_COMMENT.PEN_RETRIEVAL_REQUEST_COMMENT_ID IS 'Unique surrogate primary key for each comment.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_COMMENT.PEN_RETRIEVAL_REQUEST_ID IS 'Foreign key to the PEN Retrieval Request.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_COMMENT.STAFF_MEMBER_IDIR_GUID IS 'The IDIR GUID of staff who made the comment.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_COMMENT.STAFF_MEMBER_NAME IS 'The name of staff who made the comment.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_COMMENT.COMMENT_CONTENT IS 'The content of comment.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_COMMENT.COMMENT_TIMESTAMP IS 'Date and time that the comment was submitted.';

COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.EVENT_ID IS 'The unique ID of event.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.EVENT_PAYLOAD IS 'The payload of event.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.EVENT_STATUS IS 'The status of event: DB_COMMITTED, MESSAGE_PUBLISHED.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.EVENT_TYPE IS 'The type of event: UPDATE_PEN_REQUEST, GET_PEN_REQUEST, PEN_REQUEST_EVENT_OUTBOX_PROCESSED, ADD_PEN_REQUEST_COMMENT.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.SAGA_ID IS 'The unique ID of saga.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.EVENT_OUTCOME IS 'The outcome of processing event: PEN_REQUEST_UPDATED, PEN_REQUEST_FOUND, PEN_REQUEST_NOT_FOUND, PEN_REQUEST_COMMENT_ADDED, PEN_REQUEST_COMMENT_ALREADY_EXIST.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_EVENT.REPLY_CHANNEL IS 'The topic where the event will be sent.';

COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_MACRO.PEN_RETRIEVAL_REQUEST_MACRO_ID IS 'Unique surrogate key for each macro.';

COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_SHEDLOCK.NAME IS 'The lock name.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_SHEDLOCK.LOCK_UNTIL IS 'The time when the lock will be released.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_SHEDLOCK.LOCKED_AT IS 'The time when the lock was acquired.';
COMMENT ON COLUMN PEN_RETRIEVAL_REQUEST_SHEDLOCK.LOCKED_BY IS 'The component which acquired the lock.';
