import {v1, handleUnauthorized} from '../apiUtils';

async function getEventsbyUserId(userid){
    const response = await fetch(v1 + 'api/calendar/events/' + userid, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        const errorMessage = `Network response was not ok: ${response.statusText}`;
        console.error(errorMessage);
        throw new Error(errorMessage);
    }
    const events = await response.json();
    return events;
}

async function createEvent(event){
    const response = await fetch(v1 + 'api/calendar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: JSON.stringify(event)
    });
    await handleUnauthorized(response);
    
    if (!response.ok) {
        console.error('Network response was not ok:', response.statusText);
    }
    return  response.statusText;
}

async function deleteEvent(eventid){
    const response = await fetch(v1 + 'api/calendar/' + eventid, {
        method: 'DELETE',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        console.error('Network response was not ok:', response.statusText);
    }
    return response.statusText;
}

export {deleteEvent, createEvent, getEventsbyUserId};
