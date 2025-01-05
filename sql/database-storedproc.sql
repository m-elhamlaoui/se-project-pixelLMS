CREATE OR REPLACE FUNCTION assign_to_course(
    p_courseid INT,
    p_userid INT
)
RETURNS VOID AS $$
DECLARE
    curr_date DATE := CURRENT_DATE;
BEGIN
    INSERT INTO engagesin (userid, courseid, joindate)
    VALUES (p_userid, p_courseid, curr_date);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION assign_to_task(
    t_taskid INT,
    t_userid INT
)
RETURNS VOID AS $$ 
BEGIN
    INSERT INTO userdoestask (userid, taskid)
    VALUES (t_userid, t_taskid);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION plan_event(
    e_title VARCHAR(255),
    e_description TEXT,
    e_starttime TIMESTAMP,
    e_endtime TIMESTAMP,
    e_courseid INT
)
RETURNS VOID AS $$
BEGIN
    INSERT INTO calendarevent (courseid, title, description, starttime, endtime)
    VALUES (e_courseid, e_title, e_description, e_starttime, e_endtime);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION send_message(
    m_content TEXT,
    m_userid INT,
    m_discussionid INT
)
RETURNS INT AS $$
DECLARE
    curr_date TIMESTAMP := CURRENT_TIMESTAMP;
    new_message_id INT;
BEGIN
    INSERT INTO message (content, timestamp, userid, discussionid)
    VALUES (m_content, curr_date, m_userid, m_discussionid) 
    RETURNING messageid INTO new_message_id;
    
    RETURN new_message_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION attach_file(
    f_path VARCHAR(255),
    f_userid INT,
    attachtoID INT,
    foreignKeyType TEXT
)
RETURNS VOID AS $$
DECLARE
    curr_date TIMESTAMP := CURRENT_TIMESTAMP;
    new_file_id INT;
BEGIN
    INSERT INTO file_ (path, timestamp, userid)
    VALUES (f_path, curr_date, f_userid)
    RETURNING fileid INTO new_file_id;

    -- Update the file record to set the appropriate foreign key
    UPDATE file_
    SET 
        taskid = CASE WHEN foreignKeyType = 'task' THEN attachtoID ELSE NULL END,
        messageid = CASE WHEN foreignKeyType = 'message' THEN attachtoID ELSE NULL END,
        profileid = CASE WHEN foreignKeyType = 'profile' THEN attachtoID ELSE NULL END,
        courseid = CASE WHEN foreignKeyType = 'course' THEN attachtoID ELSE NULL END
    WHERE fileid = new_file_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION create_user(
    u_email VARCHAR(255),
    u_name VARCHAR(255),
    u_birthdate DATE,
    u_phonenumber VARCHAR(20),
    u_role VARCHAR(50),
    u_password VARCHAR(255)
)
RETURNS VOID AS $$
DECLARE
    curr_date DATE := CURRENT_DATE;
BEGIN
    INSERT INTO user_ (email, name, birthdate, phonenumber, role, password, creationdate)
    VALUES (u_email, u_name, u_birthdate, u_phonenumber, u_role, u_password, curr_date);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION delete_user(
    u_userid INT
)
RETURNS VOID AS $$
BEGIN
    UPDATE user_ SET isdeleted = TRUE WHERE userid = u_userid;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE sync_serial_sequence(table_name TEXT, column_name TEXT)
LANGUAGE plpgsql
AS $$
DECLARE
    sequence_name TEXT;
    max_value BIGINT;
BEGIN
    SELECT pg_get_serial_sequence(table_name, column_name) INTO sequence_name;

    IF sequence_name IS NULL THEN
        RAISE EXCEPTION 'No sequence found for table "%" and column "%"', table_name, column_name;
    END IF;

    EXECUTE FORMAT('SELECT COALESCE(MAX(%I), 0) FROM %I', column_name, table_name) INTO max_value;
    EXECUTE FORMAT('SELECT SETVAL(%L, %s)', sequence_name, max_value + 1);
    RAISE NOTICE 'Sequence "%" synchronized to %', sequence_name, max_value + 1;
END;
$$;