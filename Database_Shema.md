# Database Schema Description

This document describes the database schema for a learning management system. The schema supports functionalities such as user management, course creation, task assignments, discussions, messaging, and notifications. Each table in the schema serves a specific purpose and is interconnected to ensure data consistency and integrity.

## Tables

### 1. **User_**
Stores user-related data, such as email, name, birthdate, role, and account details. Users can manage courses and participate in various activities within the system.

| Field      | Type        | Description                         |
|------------|-------------|-------------------------------------|
| user_id    | INT         | Unique identifier for the user     |
| email      | VARCHAR     | User's email address               |
| name       | VARCHAR     | User's full name                   |
| birthdate  | DATE        | User's birthdate                   |
| role       | VARCHAR     | User's role (admin, teacher, student) |
| password   | VARCHAR     | User's hashed password             |
| created_at | DATETIME    | Timestamp of user account creation |

### 2. **Course**
Holds information about courses, including their titles, descriptions, start and end dates, and statuses. Courses are managed by users and have strong ties to tasks, discussions, and calendar events.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| course_id    | INT         | Unique identifier for the course   |
| title        | VARCHAR     | Course title                       |
| description  | TEXT        | Detailed course description        |
| start_date   | DATE        | Course start date                  |
| end_date     | DATE        | Course end date                    |
| status       | VARCHAR     | Status of the course (active, archived) |
| created_by   | INT         | Foreign key referencing User_ table (creator) |

### 3. **Task**
Represents individual tasks or assignments within a course. Each task is tightly associated with a course and includes details like title, description, due date, and status.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| task_id      | INT         | Unique identifier for the task     |
| title        | VARCHAR     | Task title                         |
| description  | TEXT        | Task description                   |
| due_date     | DATE        | Task due date                      |
| status       | VARCHAR     | Task status (pending, completed, etc.) |
| course_id    | INT         | Foreign key referencing Course table |

### 4. **Discussion**
Facilitates discussions related to a course. Each discussion is linked to a specific course and contains a title and description.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| discussion_id| INT         | Unique identifier for the discussion |
| title        | VARCHAR     | Discussion title                   |
| description  | TEXT        | Discussion description             |
| course_id    | INT         | Foreign key referencing Course table |

### 5. **Message**
Stores messages exchanged within discussions. Each message includes its content, timestamp, and associations with a user and a discussion.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| message_id   | INT         | Unique identifier for the message  |
| content      | TEXT        | Message content                    |
| timestamp    | DATETIME    | Timestamp of the message           |
| user_id      | INT         | Foreign key referencing User_ table (sender) |
| discussion_id| INT         | Foreign key referencing Discussion table |

### 6. **File_**
Handles file attachments across the system. Files can be linked to tasks, messages, or user profiles, and include details like file path and timestamp.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| file_id      | INT         | Unique identifier for the file     |
| file_path    | VARCHAR     | Path to the file on the server     |
| timestamp    | DATETIME    | Timestamp of file upload           |
| user_id      | INT         | Foreign key referencing User_ table (uploader) |
| task_id      | INT         | Foreign key referencing Task table (linked task) |
| message_id   | INT         | Foreign key referencing Message table (linked message) |

### 7. **CalendarEvent**
Represents events scheduled for a course. Each event includes a title, description, and start and end times.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| event_id     | INT         | Unique identifier for the event    |
| title        | VARCHAR     | Event title                        |
| description  | TEXT        | Event description                  |
| start_time   | DATETIME    | Start time of the event            |
| end_time     | DATETIME    | End time of the event              |
| course_id    | INT         | Foreign key referencing Course table |

### 8. **EngagesIn**
Tracks the relationship between users and the courses they are enrolled in, including the date they joined.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| user_id      | INT         | Foreign key referencing User_ table (student) |
| course_id    | INT         | Foreign key referencing Course table |
| join_date    | DATETIME    | Date the user joined the course    |

### 9. **UserDoesTask**
Manages associations between users and the tasks they are responsible for or assigned to.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| user_id      | INT         | Foreign key referencing User_ table (student or teacher) |
| task_id      | INT         | Foreign key referencing Task table |
| status       | VARCHAR     | Task status (pending, completed, etc.) |

### 10. **Notification**
Handles notifications sent to users. Each notification includes a title, content, and timestamp to keep users informed.

| Field        | Type        | Description                         |
|--------------|-------------|-------------------------------------|
| notification_id| INT       | Unique identifier for the notification |
| title        | VARCHAR     | Notification title                 |
| content      | TEXT        | Content of the notification        |
| timestamp    | DATETIME    | Timestamp when the notification was sent |
| user_id      | INT         | Foreign key referencing User_ table (recipient) |
