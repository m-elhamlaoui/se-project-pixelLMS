import { v1, handleUnauthorized } from '../apiUtils';

// Fetches a task by its ID
async function getTaskById(taskId) {
    const response = await fetch(v1 + 'api/task/' + taskId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const task = await response.json();
    return task;
}

// Fetches all tasks associated with a specific course
async function getTasksByCourse(courseId) {
    const response = await fetch(v1 + 'api/task/course/' + courseId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const tasks = await response.json();
    return tasks;
}

// Fetches all tasks assigned to a specific user
async function getTasksByUser(userId) {
    const response = await fetch(v1 + 'api/task/user/' + userId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const tasks = await response.json();
    return tasks;
}

// Deletes a task by its ID
async function deleteTask(taskId) {
    const response = await fetch(v1 + 'api/task/' + taskId, {
        method: 'DELETE',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return true;
}

// Inserts or updates a task
async function insertOrUpdateTask(taskId, taskDetails) {
    const response = await fetch(v1 + 'api/task/' + taskId, {
        method: 'POST',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken'),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(taskDetails)
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return true;
}

async function updateTaskAssignment(taskId, listofusers) {
    const response = await fetch(v1 + 'api/task/assign/' + taskId, {
        method: 'PUT',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken'),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(listofusers)
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }

    return true;
}

export { getTaskById, getTasksByCourse, getTasksByUser, deleteTask, insertOrUpdateTask, updateTaskAssignment };
