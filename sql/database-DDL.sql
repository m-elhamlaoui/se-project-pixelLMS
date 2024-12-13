CREATE TABLE user_ (
    userid SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthdate DATE,
    phonenumber VARCHAR(20) UNIQUE,
    creationdate DATE NOT NULL,
    role VARCHAR(20),
    isdeleted BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE course (
    courseid SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    startdate DATE NOT NULL,
    enddate DATE,
    status VARCHAR(50) NOT NULL,
    userid INT,
    FOREIGN KEY (userid) REFERENCES user_(userid) ON DELETE SET NULL
);


CREATE TABLE discussion (
    discussionid SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    courseid INT NOT NULL,
    FOREIGN KEY (courseid) REFERENCES course(courseid) ON DELETE CASCADE
);

CREATE TABLE message (
    messageid SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    userid INT NOT NULL,
    discussionid INT NOT NULL,
    FOREIGN KEY (userid) REFERENCES user_(userid) ON DELETE SET NULL,
    FOREIGN KEY (discussionid) REFERENCES discussion(discussionid) ON DELETE CASCADE
);

CREATE TABLE task (
    taskid SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duedate DATE,
    status VARCHAR(50) NOT NULL,
    courseid INT NOT NULL,
    FOREIGN KEY (courseid) REFERENCES course(courseid) ON DELETE RESTRICT
);


CREATE TABLE file_ (
    fileid SERIAL PRIMARY KEY,
    path VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    userid INT NOT NULL,

    courseid INT,
    taskid INT,
    messageid INT,
    profileid INT,
    FOREIGN KEY (userid) REFERENCES user_(userid) ON DELETE SET NULL,
    FOREIGN KEY (courseid) REFERENCES course(courseid) ON DELETE SET NULL,
    FOREIGN KEY (taskid) REFERENCES task(taskid) ON DELETE SET NULL,
    FOREIGN KEY (messageid) REFERENCES message(messageid) ON DELETE SET NULL,
    FOREIGN KEY (profileid) REFERENCES user_(userid) ON DELETE SET NULL
);



CREATE TABLE calendarevent (
    eventnumber SERIAL PRIMARY KEY,
    courseid INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    starttime TIMESTAMP NOT NULL,
    endtime TIMESTAMP,
    FOREIGN KEY (courseid) REFERENCES course(courseid) ON DELETE CASCADE
);


CREATE TABLE engagesin (
    userid INT NOT NULL,
    courseid INT NOT NULL,
    joindate DATE NOT NULL,
    PRIMARY KEY (userid, courseid),
    FOREIGN KEY (userid) REFERENCES user_(userid) ON DELETE CASCADE,
    FOREIGN KEY (courseid) REFERENCES course(courseid) ON DELETE CASCADE
);

CREATE TABLE userdoestask (
    userid INT NOT NULL,
    taskid INT NOT NULL,
    PRIMARY KEY (userid, taskid),
    FOREIGN KEY (userid) REFERENCES user_(userid) ON DELETE CASCADE,
    FOREIGN KEY (taskid) REFERENCES task(taskid) ON DELETE CASCADE
);

CREATE TABLE notification (
    notificationid SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    userid INT NOT NULL,
    FOREIGN KEY (userid) REFERENCES user_(userid) ON DELETE CASCADE
);


CREATE INDEX idx_notification_userid ON notification (userid);
CREATE INDEX idx_user_email ON user_ (email);
CREATE INDEX idx_user_phonenumber ON user_ (phonenumber);
CREATE INDEX idx_message_userid ON message (userid);
CREATE INDEX idx_task_courseid ON task (courseid);
CREATE INDEX idx_file_userid ON file_ (userid);
CREATE INDEX idx_file_courseid ON file_ (courseid);