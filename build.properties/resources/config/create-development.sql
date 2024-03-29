
CREATE TABLE TBL_QUERY_CRITERIA (
		FLD_ID BIGINT NOT NULL,
		FLD_QUERY_ID BIGINT,
		FLD_VALUEORCOND VARCHAR(100),
		FLD_ROW_NO BIGINT,
		FLD_TYPE VARCHAR(100),
		FLD_USED SMALLINT,
		FLD_INDENT BIGINT,
		FLD_PARENT VARCHAR(100),
		FLD_NUMBER BIGINT,
		FLD_NAME VARCHAR(100),
		FLD_COMP_ID BIGINT,
		FLD_COMPARISON VARCHAR(100),
		FLD_VALUE VARCHAR(100),
		FLD_CONNECTION VARCHAR(100),
		FLD_VALUE2 VARCHAR(100),
		FLD_GROUPING VARCHAR(100),
		FLD_VALUE_USERINPUT_SEQ VARCHAR(100),
		FLD_VALUE2_USERINPUT_SEQ VARCHAR(100),
		FLD_OBJECT_TYPE VARCHAR(510)
	);

CREATE TABLE FW_MESSAGE (
		FLD_ID BIGINT NOT NULL,
		FLD_TYPE VARCHAR(100),
		FLD_DATE TIMESTAMP,
		FLD_DESTINATION VARCHAR(200),
		FLD_SUBJECT VARCHAR(200),
		FLD_TEXT VARCHAR(510),
		FLD_STATUS BIGINT
	);

CREATE TABLE TBL_TYPETEST (
		FLD_INT BIGINT NOT NULL,
		FLD_BOOL SMALLINT,
		FLD_STRING VARCHAR(56),
		FLD_DECIMAL DECIMAL(9 , 0),
		FLD_DATETIME TIMESTAMP,
		FLD_CURRENCY DECIMAL(31 , 2),
		FLD_MEMO LONG VARCHAR
	);

CREATE TABLE TBL_MSG_SEPARATOR (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_CONDITION_SEPERATOR VARCHAR(510),
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_USER_REPLY (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_TYPE VARCHAR(510),
		FLD_VIEW_TABLE_ID BIGINT,
		FLD_DB_ID BIGINT,
		FLD_CRITERIA VARCHAR(510),
		FLD_SORTS VARCHAR(510),
		FLD_GROUPFIELDID BIGINT,
		FLD_PARENT_ID BIGINT,
		FLD_CHILDREN VARCHAR(510),
		FLD_MSG_TEXT VARCHAR(510),
		FLD_TIMEOUT VARCHAR(510),
		FLD_RESPONSE LONG VARCHAR,
		FLD_PROJECT_ID BIGINT,
		FLD_DESCIPTION VARCHAR(510),
		FLD_QUERY_PARENT_ID BIGINT,
		FLD_DISPLAY_RESULT BIGINT,
		FLD_DS_STATUS BIGINT,
		FLD_WS_ID BIGINT,
		FLD_DEL_STATUS BIGINT
	);

CREATE TABLE TBL_GROUPDATAVIEW (
		FLD_ID BIGINT,
		FLD_GROUPID BIGINT NOT NULL,
		FLD_DATAVIEWID BIGINT NOT NULL 
	);

CREATE TABLE FW_GROUPMEMBERSHIP (
		FLD_ID BIGINT,
		FLD_GROUPID BIGINT,
		FLD_USERID BIGINT,
		FLD_PROJECT_ID BIGINT DEFAULT 0 NOT NULL
	);

CREATE TABLE TBL_JDBCDRIVER (
		FLD_ID BIGINT,
		FLD_NAME VARCHAR(100),
		FLD_DRIVER VARCHAR(254),
		FLD_URL_FORMAT VARCHAR(254),
		FLD_DESCRIPTION VARCHAR(510),
		FLD_FILE_NAME VARCHAR(200),
		FLD_DEL_STATUS BIGINT DEFAULT 0
	);

CREATE TABLE TBL_MSG_SERVER (
		FLD_ID BIGINT NOT NULL,
		FLD_TYPE VARCHAR(254),
		FLD_SERVER_NAME VARCHAR(254),
		FLD_SITE_NAME VARCHAR(254),
		FLD_ORG_NAME VARCHAR(254),
		FLD_CLASS VARCHAR(254),
		FLD_DESCRIPTION VARCHAR(510),
		FLD_ISLOCALHOST BIGINT
	);

CREATE TABLE TBL_LANGUAGE (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_FILE_NAME VARCHAR(100),
		FLD_DEFAULT SMALLINT,
		FLD_DEL_STATUS BIGINT DEFAULT 0
	);

CREATE TABLE TBL_DATATRANSFER (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_DESCRIPTION VARCHAR(100),
		FLD_DSID BIGINT,
		FLD_FILTER VARCHAR(510)
	);

CREATE TABLE TBL_SCHEDULING (
		FLD_ID BIGINT NOT NULL,
		FLD_DTID BIGINT,
		FLD_STARTDATE TIMESTAMP,
		FLD_ENDDATE TIMESTAMP,
		FLD_REPEATTYPE BIGINT,
		FLD_REPEATCOUNT BIGINT
	);

CREATE TABLE TBL_ENTITY (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_FLAGS BIGINT,
		FLD_DSID BIGINT,
		FLD_DESCRIPTION VARCHAR(510)
	);

CREATE TABLE ENT_LOGINSETTINGS (
		FLD_ID BIGINT NOT NULL,
		FLD_FLAGS INTEGER,
		FLD_SPLASHTITLE VARCHAR(100),
		FLD_SPLASHTEXT VARCHAR(510),
		FLD_LOGINTITLE VARCHAR(510),
		FLD_USERNAMEPROMPT VARCHAR(100),
		FLD_PASSWORDPROMPT VARCHAR(100),
		FLD_LOGOUTTITLE VARCHAR(100),
		FLD_LOGOUTTEXT VARCHAR(510)
	);

CREATE TABLE TBL_QUERY (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_NAME VARCHAR(100),
		FLD_PARENTID BIGINT,
		FLD_TYPE VARCHAR(100),
		FLD_CRITERIA LONG VARCHAR,
		FLD_SORTS VARCHAR(510),
		FLD_DESCRIPTION VARCHAR(100),
		FLD_GROUPFIELDID BIGINT,
		FLD_RAWQUERY LONG VARCHAR,
		FLD_CLASS VARCHAR(510),
		FLD_EMAIL_KEYWORD VARCHAR(510),
		FLD_EMAIL_ADDRESS_ID BIGINT,
		FLD_EMAIL_DISPLAY_RESULT BIGINT,
		FLD_MOBILE_STATUS BIGINT,
		FLD_MOBILE_DISPLAY_RESULT BIGINT,
		FLD_IM_STATUS BIGINT,
		FLD_IM_DISPLAY_RESULT BIGINT,
		FLD_DEL_STATUS BIGINT,
		FLD_SMS_KEYWORD VARCHAR(510),
		FLD_IM_KEYWORD VARCHAR(510),
		FLD_DB_ID BIGINT,
		FLD_CONDITION_SEPERATOR VARCHAR(100),
		FLD_RESPONSE LONG VARCHAR,
		FLD_TIMEOUT VARCHAR(510),
		FLD_DS_STATUS BIGINT,
		FLD_WS_ID BIGINT,
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_IM_CONTACT (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_IM_CONTACT_NAME VARCHAR(100),
		FLD_MDN_USER_ID BIGINT,
		FLD_IM_MDN_CONNECTION_TYPE BIGINT,
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_LOOKUP (
		FLD_ID BIGINT,
		FLD_TYPE VARCHAR(100) NOT NULL,
		FLD_VALUE VARCHAR(100) NOT NULL,
		FLD_DESCRIPTION VARCHAR(200),
		FLD_FLAGS BIGINT
	);

CREATE TABLE TBL_IM_MESSAGE (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_TEXT VARCHAR(510),
		FLD_RECEIVED_DATE TIMESTAMP,
		FLD_SENDER_USER_ID BIGINT,
		FLD_RECEIVER_CONNECTION_ID BIGINT,
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_WEB_SERVICES (
		FLD_ID BIGINT NOT NULL,
		FLD_PROVIDER_NAME VARCHAR(100),
		FLD_DESCRIPTION VARCHAR(100),
		FLD_URL LONG VARCHAR,
		FLD_TYPE VARCHAR(100),
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_NEXTKEY (
		FLD_MAPTABLE VARCHAR(100) NOT NULL,
		FLD_NEXTKEY BIGINT
	);

CREATE TABLE TBL_JOIN (
		FLD_ID BIGINT NOT NULL,
		FLD_DSID BIGINT,
		FLD_LEFT_ENT VARCHAR(100),
		FLD_LEFT_FIELD VARCHAR(100),
		FLD_RIGHT_ENT VARCHAR(100),
		FLD_RIGHT_FIELD VARCHAR(100),
		FLD_JOIN_TYPE VARCHAR(100)
	);

CREATE TABLE TBL_FIELDEXCLUSION (
		FLD_ID BIGINT,
		FLD_DVFIELDID BIGINT NOT NULL,
		FLD_GROUPID BIGINT NOT NULL
	);

CREATE TABLE TBL_CUSTOM_FIELD (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(510),
		FLD_CAPITAL_NAME VARCHAR(510)
	);

CREATE TABLE FW_PRIVILEGE (
		FLD_ID BIGINT,
		FLD_GROUPID BIGINT NOT NULL,
		FLD_FEATUREID VARCHAR(100) NOT NULL
	);

CREATE TABLE TBL_DATASOURCE (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_NAME VARCHAR(100),
		FLD_CLASS VARCHAR(510),
		FLD_DESCRIPTION VARCHAR(510),
		FLD_JDBC_DRIVER VARCHAR(510),
		FLD_JDBC_URL VARCHAR(510),
		FLD_JDBC_CATALOG VARCHAR(510),
		FLD_JDBC_USER VARCHAR(100),
		FLD_JDBC_PASSWORD VARCHAR(100),
		FLD_IS_MIRRORED BIGINT,
		FLD_IS_MIRROR_DB BIGINT,
		FLD_JDBC_DRIVERID BIGINT,
		FLD_MIRRORID BIGINT,
		FLD_PROJECT_ID BIGINT,
		FLD_DEL_STATUS BIGINT
	);

CREATE TABLE TBL_QUERY_CRITERIA_HISTORY (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_MSG_ID BIGINT,
		FLD_ORIGINAL_ID BIGINT,
		FLD_QUERY_ID BIGINT,
		FLD_VALUEORCOND VARCHAR(100),
		FLD_ROW_NO BIGINT,
		FLD_TYPE VARCHAR(100),
		FLD_USED SMALLINT,
		FLD_INDENT BIGINT,
		FLD_PARENT VARCHAR(100),
		FLD_NUMBER BIGINT,
		FLD_NAME VARCHAR(100),
		FLD_COMP_ID BIGINT,
		FLD_COMPARISON VARCHAR(100),
		FLD_VALUE VARCHAR(100),
		FLD_CONNECTION VARCHAR(100),
		FLD_VALUE2 VARCHAR(100),
		FLD_GROUPING VARCHAR(100),
		FLD_DATETIME TIMESTAMP
	);

CREATE TABLE TBL_FIELD (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_FLAGS BIGINT,
		FLD_TYPE BIGINT,
		FLD_DESCRIPTION VARCHAR(510),
		FLD_DSID BIGINT,
		FLD_ENTITYID BIGINT,
		FLD_COLUMN_SIZE BIGINT,
		FLD_DECIMAL_DIGITS BIGINT,
		FLD_NATIVETYPE BIGINT
	);

CREATE TABLE TBL_DVFIELD (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_DATAVIEWID BIGINT,
		FLD_DESCRIPTION VARCHAR(510),
		FLD_SOURCE_FIELD VARCHAR(100),
		FLD_SOURCE_ENTITY VARCHAR(100),
		FLD_DISPLAY_NAME VARCHAR(100),
		FLD_FLAGS BIGINT,
		FLD_OPTION_LIST LONG VARCHAR,
		FLD_TYPE BIGINT
	);

CREATE TABLE TBL_TRANSFERENTITY (
		FLD_ID BIGINT NOT NULL,
		FLD_DTID BIGINT,
		FLD_ENTITYNAME VARCHAR(100)
	);

CREATE TABLE TBL_EMAIL_SETTING (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_EMAIL_ADDRESS VARCHAR(510),
		FLD_IMAP_HOST VARCHAR(510),
		FLD_IMAP_USERNAME VARCHAR(510),
		FLD_IMAP_PASSWORD VARCHAR(510),
		FLD_IMAP_PORT VARCHAR(510),
		FLD_IMAP_ENCRYPTED_TYPE BIGINT,
		FLD_SMTP_HOST VARCHAR(510),
		FLD_SMTP_USERNAME VARCHAR(510),
		FLD_SMTP_PASSWORD VARCHAR(510),
		FLD_SMTP_PORT VARCHAR(510),
		FLD_SMTP_ENCRYPTED_TYPE BIGINT,
		FLD_PROJECT_ID BIGINT,
		FLD_DEL_STATUS BIGINT
	);	

CREATE TABLE TBL_PROJECT (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_DESCRIPTION VARCHAR(100),
		FLD_DEL_STATUS VARCHAR(100)
	);

CREATE TABLE TBL_IM_CONNECTION (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_NAME VARCHAR(100),
		FLD_PASSWORD VARCHAR(510),
		FLD_TYPE BIGINT,
		FLD_STATUS BIGINT,
		FLD_USER_NAME VARCHAR(100),
		FLD_STATUS_DESC TIMESTAMP,
		FLD_USER_ID BIGINT,
		FLD_PROJECT_ID BIGINT,
		FLD_DEL_STATUS BIGINT
	);

CREATE TABLE TBL_SMS_SETTING (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1),
		FLD_SIM_NUMBER VARCHAR(510),
		FLD_COMM VARCHAR(510),
		FLD_BAUDRATE VARCHAR(510),
		FLD_MODEM_MAN VARCHAR(510),
		FLD_MODEM_MODEL VARCHAR(510),
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_SMPP (
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 2 ,INCREMENT BY 1),
		FLD_NUMBER VARCHAR(510),
		FLD_HOST VARCHAR(510),
		FLD_PORT VARCHAR(510),
		FLD_USERNAME VARCHAR(510),
		FLD_PASSWORD VARCHAR(510),
		FLD_SOURCE_NPI VARCHAR(100),
		FLD_SOURCE_TON VARCHAR(100),
		FLD_DEST_NPI VARCHAR(100),
		FLD_DEST_TON VARCHAR(100),
		FLD_BIND_NPI VARCHAR(100),
		FLD_BIND_TON VARCHAR(100),				
		FLD_TYPE VARCHAR(100),
		FLD_INTERVAL BIGINT,
		FLD_USE_TLV BIGINT,
		FLD_USE_ADDRESS_RANGE BIGINT,
		FLD_DEL_STATUS BIGINT
	);
		
CREATE TABLE ENT_MENUACTION (
		FLD_ID BIGINT NOT NULL,
		FLD_CLASS VARCHAR(100),
		FLD_NAME VARCHAR(510),
		FLD_DESCRIPTION VARCHAR(510),
		FLD_SEQUENCE BIGINT,
		FLD_GROUPID BIGINT,
		FLD_PARENTMENUID BIGINT,
		FLD_DATAVIEWID BIGINT,
		FLD_QUERYDOBJID BIGINT,
		FLD_LINK VARCHAR(510),
		FLD_MSGSERVER_FLAGS BIGINT,
		FLD_MSGSERVERID BIGINT
	);

CREATE TABLE TBL_MESSAGE_LOG (
		FLD_ID BIGINT NOT NULL,
		FLD_TEXT VARCHAR(510),
		FLD_RECEIVED_DATE TIMESTAMP,
		FLD_SENDER_USER_ID BIGINT,
		FLD_RECEIVER_CONNECTION_ID BIGINT,
		FLD_MSG_TYPE VARCHAR(510)
	);

CREATE TABLE TBL_WEB_SERVICE_OPERATION (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_DESCRIPTION VARCHAR(100),
		FLD_URL LONG VARCHAR,
		FLD_SERVICE VARCHAR(100),
		FLD_PORT VARCHAR(100),
		FLD_OPERATION VARCHAR(100),
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_DATAVIEW (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_SOURCE_DSID BIGINT,
		FLD_DESCRIPTION VARCHAR(510),
		FLD_CLASS VARCHAR(510),
		FLD_DEL_STATUS BIGINT
	);

CREATE TABLE FW_FEATURE (
		FLD_NAME VARCHAR(100) NOT NULL,
		FLD_DESCRIPTION VARCHAR(400),
		FLD_TYPE VARCHAR(100)
	);

CREATE TABLE FW_USER (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_PASSWORD VARCHAR(100),
		FLD_FIRST_NAME VARCHAR(400),
		FLD_LAST_NAME VARCHAR(100),
		FLD_DEL_STATUS VARCHAR(100),
		FLD_EMAIL VARCHAR(100),
		FLD_MOBILE VARCHAR(100),
		FLD_NOTES VARCHAR(510),
		FLD_GROUP_ID BIGINT,
		FLD_PRIVILEGE VARCHAR(100),
		FLD_LICENSE_TYPE VARCHAR(100)
	);

CREATE TABLE FW_GROUP (
		FLD_ID BIGINT NOT NULL,
		FLD_NAME VARCHAR(100),
		FLD_DESCRIPTION VARCHAR(400),
		FLD_GUEST BIGINT,
		FLD_STATUS_DEL VARCHAR(100),
		FLD_PROJECT_ID BIGINT
	);

CREATE TABLE TBL_USER_CUSTOM_FIELD (
		FLD_USER_ID BIGINT,
		FLD_CUSTOM_ID BIGINT,
		FLD_CUSTOM_PARAM VARCHAR(510),
		FLD_ID BIGINT NOT NULL
	);

CREATE TABLE TBL_MSGSVR_PROFILE (
		FLD_ID BIGINT NOT NULL,
		FLD_USERID BIGINT,
		FLD_MSG_SERVERID BIGINT,
		FLD_PROFILE_NAME VARCHAR(254),
		FLD_PASSWORD VARCHAR(100),
		FLD_CLASS VARCHAR(254),
		FLD_DESCRIPTION VARCHAR(510)
	);
	
CREATE TABLE TBL_MSG_SETT_INFO (
		FLD_ID 						BIGINT NOT NULL,
		FLD_TYPE 					VARCHAR(200),
		FLD_STATUS 					BIGINT,
		FLD_TOTAL_MSG_COUNT 		BIGINT,
		FLD_SEARCH_TERM 			VARCHAR(200)
	);			
	
CREATE TABLE TBL_TEMP_BLOCK_INFO (
		FLD_ID 				BIGINT NOT NULL,
		FLD_MAX_MSG 		BIGINT,
		FLD_MAX_PERIOD 		VARCHAR(200),
		FLD_CANCEL_PERIOD 	VARCHAR(200),
		FLD_REPLY  	VARCHAR(200)
	);			
		
CREATE TABLE TBL_BLOCK_CONTACTS (
		FLD_ID 		BIGINT 		NOT NULL,
		FLD_TYPE 	VARCHAR(200),
		FLD_CONTACT VARCHAR(200)
	);			

CREATE TABLE TBL_GROUP_ENTITY_PERMISSION ( 
		FLD_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY  (START WITH 1 ,INCREMENT BY 1), 
		FLD_GROUPID BIGINT NOT NULL, 
		FLD_ENTITY_ID BIGINT NOT NULL
	);
	
CREATE UNIQUE INDEX SQL071026165747120 ON TBL_SCHEDULING (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746720 ON TBL_LOOKUP (FLD_TYPE ASC, FLD_VALUE ASC);
CREATE UNIQUE INDEX SQL071026165746560 ON TBL_IM_CONNECTION (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746280 ON TBL_DVFIELD (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746790 ON TBL_MSG_SERVER (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745660 ON ENT_MENUACTION (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746380 ON TBL_ENTITY (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747470 ON TBL_WEB_SERVICES (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746900 ON TBL_QUERY (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746240 ON TBL_DATAVIEW (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747260 ON TBL_TYPETEST (FLD_INT ASC);
CREATE UNIQUE INDEX SQL071026165747180 ON TBL_SMS_SETTING (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747010 ON TBL_QUERY_CRITERIA_HISTORY (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746820 ON TBL_MSGSVR_PROFILE (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746770 ON TBL_MSG_SEPARATOR (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746870 ON TBL_PROJECT (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746960 ON TBL_QUERY_CRITERIA (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747230 ON TBL_TRANSFERENTITY (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746600 ON TBL_IM_CONTACT (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747300 ON TBL_USER_CUSTOM_FIELD (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071205174950720 ON TBL_LANGUAGE (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746410 ON TBL_FIELD (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745990 ON FW_USER (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745890 ON FW_MESSAGE (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746740 ON TBL_MESSAGE_LOG (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745450 ON ENT_LOGINSETTINGS (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745720 ON FW_FEATURE (FLD_NAME ASC);
CREATE UNIQUE INDEX SQL071026165746080 ON TBL_CUSTOM_FIELD (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745780 ON FW_GROUP (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746340 ON TBL_EMAIL_SETTING (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746630 ON TBL_IM_MESSAGE (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747340 ON TBL_USER_REPLY (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746190 ON TBL_DATATRANSFER (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746490 ON TBL_GROUPDATAVIEW (FLD_GROUPID ASC, FLD_DATAVIEWID ASC);
CREATE UNIQUE INDEX SQL071026165746850 ON TBL_NEXTKEY (FLD_MAPTABLE ASC);
CREATE UNIQUE INDEX SQL071026165747420 ON TBL_WEB_SERVICE_OPERATION (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746440 ON TBL_FIELDEXCLUSION (FLD_DVFIELDID ASC, FLD_GROUPID ASC);
CREATE UNIQUE INDEX SQL071026165746680 ON TBL_JOIN (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165745950 ON FW_PRIVILEGE (FLD_GROUPID ASC, FLD_FEATUREID ASC);
CREATE UNIQUE INDEX SQL071203172341250 ON FW_GROUPMEMBERSHIP (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746130 ON TBL_DATASOURCE (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746131 ON TBL_MSG_SETT_INFO (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746132 ON TBL_BLOCK_CONTACTS (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746133 ON TBL_TEMP_BLOCK_INFO (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165747181 ON TBL_SMPP (FLD_ID ASC);
CREATE UNIQUE INDEX SQL071026165746491 ON TBL_GROUP_ENTITY_PERMISSION (FLD_ID ASC);

ALTER TABLE TBL_SCHEDULING ADD CONSTRAINT SQL071026165747120 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_DVFIELD ADD CONSTRAINT SQL071026165746280 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_FIELDEXCLUSION ADD CONSTRAINT SQL071026165746440 PRIMARY KEY (FLD_DVFIELDID, FLD_GROUPID);
ALTER TABLE TBL_IM_MESSAGE ADD CONSTRAINT SQL071026165746630 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_NEXTKEY ADD CONSTRAINT SQL071026165746850 PRIMARY KEY (FLD_MAPTABLE);
ALTER TABLE TBL_IM_CONNECTION ADD CONSTRAINT SQL071026165746560 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_SMS_SETTING ADD CONSTRAINT SQL071026165747180 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_SMPP ADD CONSTRAINT SQL071026165747181 PRIMARY KEY (FLD_ID);

ALTER TABLE TBL_ENTITY ADD CONSTRAINT SQL071026165746380 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_MSGSVR_PROFILE ADD CONSTRAINT SQL071026165746820 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_DATAVIEW ADD CONSTRAINT SQL071026165746240 PRIMARY KEY (FLD_ID);
ALTER TABLE FW_GROUP ADD CONSTRAINT SQL071026165745780 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_DATATRANSFER ADD CONSTRAINT SQL071026165746190 PRIMARY KEY (FLD_ID);
ALTER TABLE FW_USER ADD CONSTRAINT SQL071026165745990 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_DATASOURCE ADD CONSTRAINT SQL071026165746130 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_LOOKUP ADD CONSTRAINT SQL071026165746720 PRIMARY KEY (FLD_TYPE, FLD_VALUE);
ALTER TABLE TBL_QUERY ADD CONSTRAINT SQL071026165746900 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_PROJECT ADD CONSTRAINT SQL071026165746870 PRIMARY KEY (FLD_ID);
ALTER TABLE FW_MESSAGE ADD CONSTRAINT SQL071026165745890 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_TYPETEST ADD CONSTRAINT SQL071026165747260 PRIMARY KEY (FLD_INT);
ALTER TABLE TBL_LANGUAGE ADD CONSTRAINT SQL071205174950720 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_CUSTOM_FIELD ADD CONSTRAINT SQL071026165746080 PRIMARY KEY (FLD_ID);
ALTER TABLE FW_FEATURE ADD CONSTRAINT SQL071026165745720 PRIMARY KEY (FLD_NAME);
ALTER TABLE TBL_FIELD ADD CONSTRAINT SQL071026165746410 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_MSG_SERVER ADD CONSTRAINT SQL071026165746790 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_MESSAGE_LOG ADD CONSTRAINT SQL071026165746740 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_WEB_SERVICES ADD CONSTRAINT SQL071026165747470 PRIMARY KEY (FLD_ID);
ALTER TABLE ENT_MENUACTION ADD CONSTRAINT SQL071026165745660 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_GROUPDATAVIEW ADD CONSTRAINT SQL071026165746490 PRIMARY KEY (FLD_GROUPID, FLD_DATAVIEWID);
ALTER TABLE TBL_IM_CONTACT ADD CONSTRAINT SQL071026165746600 PRIMARY KEY (FLD_ID);
ALTER TABLE ENT_LOGINSETTINGS ADD CONSTRAINT SQL071026165745450 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_JOIN ADD CONSTRAINT SQL071026165746680 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_TRANSFERENTITY ADD CONSTRAINT SQL071026165747230 PRIMARY KEY (FLD_ID);
ALTER TABLE FW_PRIVILEGE ADD CONSTRAINT SQL071026165745950 PRIMARY KEY (FLD_GROUPID, FLD_FEATUREID);
ALTER TABLE TBL_EMAIL_SETTING ADD CONSTRAINT SQL071026165746340 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_WEB_SERVICE_OPERATION ADD CONSTRAINT SQL071026165747420 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_USER_REPLY ADD CONSTRAINT SQL071026165747340 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_USER_CUSTOM_FIELD ADD CONSTRAINT SQL071026165747300 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_QUERY_CRITERIA ADD CONSTRAINT SQL071026165746960 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_MSG_SEPARATOR ADD CONSTRAINT SQL071026165746770 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_QUERY_CRITERIA_HISTORY ADD CONSTRAINT SQL071026165747010 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_MSG_SETT_INFO ADD CONSTRAINT SQL071026165746131 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_BLOCK_CONTACTS ADD CONSTRAINT SQL071026165746132 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_TEMP_BLOCK_INFO ADD CONSTRAINT SQL071026165746133 PRIMARY KEY (FLD_ID);
ALTER TABLE TBL_GROUP_ENTITY_PERMISSION ADD CONSTRAINT SQL071026165746491 PRIMARY KEY (FLD_ID);
--- DATA SECTION

INSERT INTO ENT_LOGINSETTINGS VALUES(1,2,NULL,'Welcome to Mobile Data Now',NULL,'Login to MDN','User Name','Password',NULL);

INSERT INTO ENT_MENUACTION VALUES(23,'wsl.mdn.guiconfig.QueryRecords','test',NULL,1,1009,NULL,1032,44,NULL,NULL,NULL);

INSERT INTO FW_FEATURE VALUES('ADMIN','Administrator (has login privilege)', 'PRIVILEGE');
INSERT INTO FW_FEATURE VALUES('USER','Common User', 'PRIVILEGE');
INSERT INTO FW_FEATURE VALUES('PERPETUAL','Perpetual', 'LICENSE');
INSERT INTO FW_FEATURE VALUES('ANNUAL','Annual', 'LICENSE');

INSERT INTO FW_USER VALUES(1007,'admin','d033e22ae348aeb5660fc2140aec35850c4da997','Admin','Admin','0',NULL,NULL,NULL,NULL,'ADMIN','Perpetual');

INSERT INTO TBL_DATASOURCE VALUES(3036,'Default MDN Mirror','wsl.mdn.dataview.JdbcDataSourceDobj',NULL,NULL,'jdbc:derby:mdnmirror.mdb;create=true',NULL,NULL,NULL,0,1,2,0,1,0);
--INSERT INTO TBL_DATASOURCE VALUES(3089,'Northwind','wsl.mdn.dataview.JdbcDataSourceDobj',NULL,NULL,'jdbc:mysql://localhost/Northwind',NULL,NULL,NULL,0,0,1003,0,1,0);

--INSERT INTO TBL_JDBCDRIVER VALUES(1000,'MS Access','com.hxtt.sql.access.AccessDriver','jdbc:access:///c:/yourAccessDirectory', 'access','Access_JDBC30.jar',0);
INSERT INTO TBL_JDBCDRIVER VALUES(1001,'Oracle 8i Driver','oracle.jdbc.driver.OracleDriver','jdbc:oracle:thin:@[host]:[port]:[database name]','oracle','ojdbc6.jar',0);
--INSERT INTO TBL_JDBCDRIVER VALUES(1002,'Microsoft SQL Server 2005','com.microsoft.jdbc.sqlserver.SQLServerDriver','','SQL Server','sqljdbc.jar',0);
INSERT INTO TBL_JDBCDRIVER VALUES(1002,'Microsoft SQL(6.5,7,2000,2005)','com.microsoft.sqlserver.jdbc.SQLServerDriver','jdbc:sqlserver://[host]:[port];DatabaseName=[database name]','SQL Server','sqljdbc.jar',0);
INSERT INTO TBL_JDBCDRIVER VALUES(1003,'JDBC/ODBC Bridge','sun.jdbc.odbc.JdbcOdbcDriver','jdbc:odbc:[database name]','','',0);
INSERT INTO TBL_JDBCDRIVER VALUES(1004,'Cloudscape/Derby','org.apache.derby.jdbc.EmbeddedDriver','jdbc:derby:[database name]','Derby','derby.jar',0);
INSERT INTO TBL_JDBCDRIVER VALUES(1005,'MySQL Connector/Jdriver','org.gjt.mm.mysql.Driver','jdbc:mysql://[host]:[port]/[database name]','MySql','mysql-connector-java-5.1.6.jar',0);
--INSERT INTO TBL_JDBCDRIVER VALUES(1006,'Sybase(10,11,12,15)','net.sourceforge.jtds.jdbc.Driver','eg. jdbc:jtds:sybase://server[:port]/[database]','Sybase','jtds-1.2.2.jar',0);

INSERT INTO TBL_LANGUAGE VALUES(1,'English','gui_en.xml',1,0);
--INSERT INTO TBL_LANGUAGE VALUES(2,'Australian English','gui_au.xml',0,0);



INSERT INTO TBL_NEXTKEY VALUES ('ENT_LOGINSETTINGS',2);
INSERT INTO TBL_NEXTKEY VALUES ('ENT_MENUACTION',232);
INSERT INTO TBL_NEXTKEY VALUES ('FW_GROUP',1053);
INSERT INTO TBL_NEXTKEY VALUES ('FW_GROUPMEMBERSHIP',1017);
INSERT INTO TBL_NEXTKEY VALUES ('FW_PRIVILEGE',1000);
INSERT INTO TBL_NEXTKEY VALUES ('FW_USER',1069);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_BLOCK_CONTACTS',1);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_CUSTOM_FIELD',10);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_DATASOURCE',3090);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_DATATRANSFER',6);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_DATAVIEW',1085);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_DVFIELD',2484);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_EMAIL_SETTING',19);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_ENTITY',1960);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_FEATURE',1000);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_FIELD',9026);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_FIELDEXCLUSION',25);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_GROUPDATAVIEW',672);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_IM_CONNECTION',60);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_IM_CONTACT',47);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_IM_MESSAGE',894);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_JDBCDRIVER',10);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_JOIN',1065);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_LANGUAGE',3);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_LOOKUP',1000);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_MESSAGE_INFO',6);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_MESSAGE_LOG',78);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_MSGSVR_PROFILE',32);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_MSG_SEPARATOR',4);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_MSG_SERVER',18);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_PROJECT',2);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_MSG_SETT_INFO',5);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_QUERY',213);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_QUERY_CRITERIA',467);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_QUERY_CRITERIA_HISTORY',54);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_SCHEDULING',9);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_SMS_SETTING',6);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_TRANSFERENTITY',6);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_USER_CUSTOM_FIELD',10);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_USER_REPLY',75);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_TEMP_BLOCK_INFO',1);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_SMPP',2);
INSERT INTO TBL_NEXTKEY VALUES ('TBL_GROUP_ENTITY_PERMISSION',1);

INSERT INTO TBL_PROJECT VALUES (1,'Sample','Sample','0');

INSERT INTO TBL_MSG_SETT_INFO VALUES(1, 'Email', 0, 0, 'Help');
INSERT INTO TBL_MSG_SETT_INFO VALUES(2, 'IM', 0, 0, 'Help');
INSERT INTO TBL_MSG_SETT_INFO VALUES(3, 'SMS', 0, 0, 'Help');
INSERT INTO TBL_MSG_SETT_INFO VALUES(4, 'SMPP', 0, 0, 'Help');


INSERT INTO TBL_TEMP_BLOCK_INFO VALUES(1, 0,'0','0','Too many messages. Please try again later');

INSERT INTO TBL_WEB_SERVICES VALUES(1,'Weather Service','','http://www.ejse.com/WeatherService/Service.asmx?WSDL','SAMPLE',1);
INSERT INTO TBL_WEB_SERVICES VALUES(2,'StrikeIron','','http://ws.strikeiron.com/ypcom/yp1?WSDL','SAMPLE',1);

EXIT;