import {v1, handleUnauthorized} from '../apiUtils';

async function getProjectDiscussions(projectid) {
    const response = await fetch(v1 + 'api/discussion/getbyproject/' + projectid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    })
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    const disc = await response.json();
    return disc;
}

async function createDiscussion(discussion) {
    const token = localStorage.getItem('serversecuritytoken');
    console.log('Authorization Token:', token);
    console.log('Request Payload:', JSON.stringify(discussion));

    try {
        const response = await fetch(v1 + 'api/discussion', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify(discussion)
        });

        console.log('Response Status:', response.status);
        const responseBody = await response.text();
        console.log('Response Body:', responseBody);

        if (!response.ok) {
            throw new Error(`Network response was not ok: ${response.statusText}`);
        }

        return response.statusText;
    } catch (error) {
        console.error('Fetch Error:', error);
        throw error;
    }
}



async function deleteDiscussion(discussionid) {
    const response = await fetch(v1 + 'api/discussion/' + discussionid, {
        method: 'DELETE',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    return response.statusText;
}

async function updateDiscussion(discussion) {
    const response = await fetch(v1 + 'api/discussion/'+ discussion.discussionid, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify(discussion)
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    return response.statusText;
}

async function getDiscussionById(id){
    const response = await fetch(v1 + 'api/discussion/' + id, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    const disc = await response.json();
    return disc;
}

async function fetchMessages(discussionid) {
    const response = await fetch(v1 + 'api/discussion/message/' + discussionid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    const messages = await response.json();
    return messages;
}

async function sendMessage(message, discussionid) {
    const response = await fetch(v1 + 'api/discussion/message/' + discussionid, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify({
            content: message
        })
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    return response.statusText;
}

export {sendMessage, fetchMessages, createDiscussion, deleteDiscussion, updateDiscussion, getProjectDiscussions, getDiscussionById}