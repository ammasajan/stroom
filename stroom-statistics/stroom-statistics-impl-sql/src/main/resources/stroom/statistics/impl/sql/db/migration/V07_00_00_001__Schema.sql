-- ------------------------------------------------------------------------
-- Copyright 2020 Crown Copyright
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ------------------------------------------------------------------------

-- Stop NOTE level warnings about objects (not)? existing
SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0;

-- NOTE This is pretty much a copy of V4_0_60 in stroom v6, but as schema_version has
-- been renamed to statistics_schema_history, flyway will know nothing of the previous
-- schema. Thus we can start afresh with everything written to cope with the object already
-- existing. Renamed to V7_00_00_001 to avoid the confusion of a V4 script running in
-- a v6 ==> v7 migration.


-- Create the SQL_STAT_ tables for the New SQL stats process.  These are duplicates of the legacy tables (STAT_KEY, STAT_VAL and STAT_VAL_SRC)

--
-- Table structure for table sql_stat_key (idempotent)
--
CREATE TABLE IF NOT EXISTS SQL_STAT_KEY (
  ID 				bigint(20)   auto_increment PRIMARY KEY,
  VER 				tinyint(4)   NOT NULL,
  NAME 				varchar(766) NOT NULL,
  UNIQUE 			(NAME)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table sql_stat_val (idempotent)
--
CREATE TABLE IF NOT EXISTS SQL_STAT_VAL (
  TIME_MS               bigint(20) NOT NULL,
  PRES                  tinyint(4) NOT NULL,
  VAL_TP                tinyint(4) NOT NULL,
  VAL                   double     NOT NULL,
  CT                    bigint(20) NOT NULL,
  FK_SQL_STAT_KEY_ID    bigint(20) NOT NULL,
  PRIMARY KEY (FK_SQL_STAT_KEY_ID, TIME_MS, VAL_TP, PRES),
  CONSTRAINT 			SQL_STAT_VAL_FK_STAT_KEY_ID
      FOREIGN KEY (FK_SQL_STAT_KEY_ID)
      REFERENCES SQL_STAT_KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- 'f' == false, obvs
CALL statistics_create_non_unique_index_v1(
    'SQL_STAT_VAL',
    'SQL_STAT_VAL_TIME_MS',
    'TIME_MS');

-- Ensure VAL is a nullable double (idempotent)
-- VAL only applicable to VALUE stats
ALTER TABLE SQL_STAT_VAL
MODIFY COLUMN VAL double;

--
-- Table structure for table sql_stat_val_src (idempotent)
--
CREATE TABLE IF NOT EXISTS SQL_STAT_VAL_SRC (
  ID 				bigint(20)   auto_increment,
  TIME_MS			bigint(20)   NOT NULL,
  NAME 				varchar(766) NOT NULL,
  VAL_TP 			tinyint(4)   NOT NULL,
  VAL				double       NOT NULL,
  CT				bigint(20)   NOT NULL,
  PROCESSING        bit(1)       NOT NULL DEFAULT 0,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- used for deletions of old data where the FK is not involved
CALL statistics_create_non_unique_index_v1(
    'SQL_STAT_VAL_SRC',
    'SQL_STAT_VAL_SRC_PROCESSING_TIME_MS',
    'PROCESSING, TIME_MS');

-- Ensure VAL is a nullable double (idempotent)
-- VAL only applicable to VALUE stats
ALTER TABLE SQL_STAT_VAL_SRC
MODIFY COLUMN VAL double ;

ALTER TABLE SQL_STAT_VAL_SRC MODIFY COLUMN PROCESSING tinyint(1) NOT NULL DEFAULT '0';

--
-- Copy data into the job node table
--
DROP PROCEDURE IF EXISTS add_col_to_stat_val_src;
DROP PROCEDURE IF EXISTS add_id_to_stat_val_src;
DELIMITER //
CREATE PROCEDURE add_col_to_stat_val_src ()
BEGIN
    -- Idempotent
    IF NOT EXISTS (
            SELECT NULL
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = 'SQL_STAT_VAL_SRC'
            AND COLUMN_NAME = 'CT') THEN

        -- We are missing the CT col so add it
        ALTER TABLE SQL_STAT_VAL_SRC
        ADD COLUMN CT BIGINT(20) AFTER VAL;

    END IF;

    -- Populate values (idempotent)
    UPDATE SQL_STAT_VAL_SRC
    SET CT = VAL;

    -- Now make it not null (idempotent)
    ALTER TABLE SQL_STAT_VAL_SRC
    MODIFY COLUMN CT BIGINT(20) NOT NULL;

END//
CREATE PROCEDURE add_id_to_stat_val_src ()
BEGIN
    -- Idempotent
    IF NOT EXISTS (
            SELECT NULL
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = 'SQL_STAT_VAL_SRC'
            AND COLUMN_NAME = 'ID') THEN
        SELECT CONCAT('Beginning migration of ',
                          ' table SQL_STAT_VAL_SRC in database ',
                          database(), ' to version 7 (adding id field to stats)'
                          '.');
        -- We are missing the ID (autoincrement, pk) col.
        -- Create a new table and copy values back
        RENAME TABLE SQL_STAT_VAL_SRC TO SQL_STAT_VAL_SRC_OLD;

        -- Create the table with the new structure
        CREATE TABLE IF NOT EXISTS SQL_STAT_VAL_SRC (
          ID bigint(20) auto_increment,
          TIME_MS bigint(20) NOT NULL,
          NAME varchar(766) NOT NULL,
          VAL_TP tinyint(4) NOT NULL,
          VAL double NOT NULL,
          CT bigint(20) NOT NULL,
          PROCESSING bit(1) NOT NULL DEFAULT 0,
          PRIMARY KEY (ID)
        ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

        -- Copy the data over
        INSERT INTO SQL_STAT_VAL_SRC (
          TIME_MS, NAME, VAL_TP, VAL, CT, PROCESSING
        ) SELECT TIME_MS, NAME, VAL_TP, VAL, CT, PROCESSING FROM SQL_STAT_VAL_SRC_OLD;

        -- Check that the correct number of columns have been copied
        BEGIN
            DECLARE row_count_before integer;
            DECLARE row_count_after integer;

            SELECT COUNT(*)
              INTO row_count_before
              FROM SQL_STAT_VAL_SRC_OLD;

            SELECT COUNT(*)
              INTO row_count_after
              FROM SQL_STAT_VAL_SRC;

            SELECT CONCAT('There are ', row_count_before,
                          ' records to migrate.');
            -- Delete the old table if copy was successful
            IF row_count_before = row_count_after THEN
              DROP TABLE SQL_STAT_VAL_SRC_OLD;
              SELECT CONCAT('Successfully migrated ',row_count_before,
                            ' records into table SQL_STAT_VAL_SRC in database ',
                            database(), ' to version 7 (added id field to stats)',
                            '.');
            ELSE
            -- Attempt rollback
              DROP TABLE SQL_STAT_VAL_SRC;
              RENAME TABLE SQL_STAT_VAL_SRC_OLD TO SQL_STAT_VAL_SRC;

              SELECT CONCAT('Failed to copy all records! Copied ',
                          row_count_after,' of ',
                          row_count_before,
                          ' records from table SQL_STAT_VAL_SRC while migrating ',
                          database(), ' to version 7 (adding id field to stats)',
                          '.');
            END IF;
         END;
    END IF;
END//
DELIMITER ;
CALL add_col_to_stat_val_src();
CALL add_id_to_stat_val_src();
DROP PROCEDURE add_col_to_stat_val_src;
DROP PROCEDURE add_id_to_stat_val_src;

SET SQL_NOTES=@OLD_SQL_NOTES;
