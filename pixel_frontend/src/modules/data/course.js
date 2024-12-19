import {v1, handleUnauthorized} from '../apiUtils';

async function fetchEngagedCourses(userid) {
    const response = await fetch(v1 + 'api/course/workedby/' + userid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const courses = await response.json();
    return courses;
}

async function fetchSuperviseCourses(userid) {
    const response = await fetch(v1 + 'api/course/supervisedby/' + userid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const courses = await response.json();
    return courses;
}

async function getCourseById(courseId) {
    const response = await fetch(v1 + 'api/course/' + courseId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const course = await response.json();
    return course;
}

async function insertCourse(course) {
    const response = await fetch(v1 + 'api/course', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify(course)
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return true;
}

async function updateCourse(course) {
    const response = await fetch(v1 + 'api/course/' + course.courseid, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify(course)
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return true;
}

async function getAllCourses() {
    const response = await fetch(v1 + 'api/course', {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    const courses = await response.json();
    return courses;
}

async function updateCourseAssignment(courseid, listofusers) {
    const response = await fetch(v1 + 'api/course/assign/' + courseid, {
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

export {updateCourseAssignment, getAllCourses, fetchEngagedCourses, fetchSuperviseCourses, getCourseById, insertCourse, updateCourse};
