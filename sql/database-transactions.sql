CREATE OR REPLACE FUNCTION create_task(
    t_title VARCHAR(255),
    t_description TEXT,
    t_duedate DATE, 
    t_status VARCHAR(50),
    t_courseid INT,
    taskdoer INT
)
RETURNS VOID AS $$
DECLARE
    task_id INT;
BEGIN
    INSERT INTO task (title, description, duedate, status, courseid)
    VALUES (t_title, t_description, t_duedate, t_status, t_courseid)
    RETURNING taskid INTO task_id;

    INSERT INTO userdoestask (userid, taskid)
    VALUES (taskdoer, task_id);
EXCEPTION
    WHEN others THEN
        RAISE;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION create_course(
    p_title VARCHAR(255),
    p_description TEXT,
    p_status VARCHAR(50),
    p_creator_userid INT,
	p_end_date DATE
)
RETURNS VOID AS $$
DECLARE
    curr_date DATE := CURRENT_DATE;
	p_course_id INT;
BEGIN
    INSERT INTO course (title, description, startdate, enddate, status, userid)
    VALUES (p_title, p_description, curr_date, p_end_date, p_status, p_creator_userid)
    RETURNING courseid INTO p_course_id;

    INSERT INTO engagesin (userid, courseid, joindate)
    VALUES (p_creator_userid, p_course_id, curr_date);
END;
$$ LANGUAGE plpgsql;
