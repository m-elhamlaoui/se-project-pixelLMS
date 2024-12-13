import {v1, handleUnauthorized} from '../apiUtils';

async function fetchEngagedProjects(userid) {
    const response = await fetch(v1 + 'api/project/workedby/' + userid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const projects = await response.json();
    return projects;
}

async function fetchSuperviseProjects(userid) {
    const response = await fetch(v1 + 'api/project/supervisedby/' + userid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const projects = await response.json();
    return projects;
}

async function getProjectById(projectId) {
    const response = await fetch(v1 + 'api/project/' + projectId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const project = await response.json();
    return project;
}

async function insertProject(project) {
    const response = await fetch(v1 + 'api/project', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify(project)
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return true;
}

async function updateProject(project) {
    const response = await fetch(v1 + 'api/project/' + project.projectid, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify(project)
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return true;
}

async function getAllProjects() {
    const response = await fetch(v1 + 'api/project', {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const projects = await response.json();
    return projects;
}

async function updateProjectAssignment(projectid, listofusers) {
    const response = await fetch(v1 + 'api/project/assign/' + projectid, {
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

export {updateProjectAssignment, getAllProjects, fetchEngagedProjects, fetchSuperviseProjects, getProjectById, insertProject, updateProject};
