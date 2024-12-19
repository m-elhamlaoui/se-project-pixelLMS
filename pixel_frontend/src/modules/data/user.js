import {v1, handleUnauthorized} from '../apiUtils';

async function getUserbyId(userid){
    const response = await fetch(v1 + 'api/user/' + userid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const user = await response.json();
    return user;
}

async function getUsersInCourse(courseid){
    const response = await fetch(v1 + 'api/user/workson/' + courseid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const users = await response.json();
    return users;
}

async function getUsersInTask(taskid){
    const response = await fetch(v1 + 'api/user/does/' + taskid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const users = await response.json();
    return users;
}

async function createUser(user, password) {
    const response = await fetch(v1 + 'api/user', {
        method: 'POST',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken'),
            'Password': password,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const result = await response.text();
    return result;
}

async function updateUser(user, password) {
    const response = await fetch(v1 + 'api/user/' + user.userid, {
        method: 'PUT',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken'),
            'Password': password,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const result = await response.text();
    return result;
}

async function getAllUsers() {
    const response = await fetch(v1 + 'api/user', {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const users = await response.json();
    return users;
}

async function getUserByEmail(email) {

    const response = await fetch(v1 + 'api/user/getbyemail/' + email, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const user = await response.json();
    return user;
}

export {getUserByEmail, getAllUsers, createUser, updateUser, getUserbyId, getUsersInCourse, getUsersInTask};
