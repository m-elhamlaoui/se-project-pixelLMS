import { v1, handleUnauthorized } from './apiUtils.js';

async function login(email, password) {
    try {
        const response = await fetch(v1+'security/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                password: password,
            }),
        });
        await handleUnauthorized(response);
        if (!response.ok) {
            return false;
        }
        const token = await response.text();
        localStorage.setItem('serversecuritytoken', token);
        const user = await fetchUser();
        localStorage.setItem('user', JSON.stringify(user));
        return true;
    } catch (error) {
        console.error('Login error:', error);
        return false;
    }
}

async function fetchUser() {
    try {
        const response = await fetch(v1 + 'api/user/current', {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('serversecuritytoken')
            }
        });
        await handleUnauthorized(response);
        if (!response.ok) {
            console.error('Network response was not ok:', response.statusText);
            return null;
        }
        const user = await response.json();
        return user;
    } catch (error) {
        console.error('Fetch user error:', error);
        return null;
    }
}


async function getUser(){
    if(localStorage.getItem('user') === null || localStorage.getItem('user') === 'null'){
        const user = await fetchUser();
        localStorage.setItem('user', JSON.stringify(user));
        return user;
    } else {
        return JSON.parse(localStorage.getItem('user'));
    }
}

function isLogged(){
    if(localStorage.getItem('serversecuritytoken') === null){
        return false;
    } else {
        return true;
    }
}

async function logout(){
    await fetch(v1+'security/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            "Authorization": "Bearer " + localStorage.getItem('serversecuritytoken')
        },
    });
    localStorage.removeItem('serversecuritytoken');
    localStorage.removeItem('user');
    localStorage.removeItem('course');
    window.location.reload();
}

export { login, logout, getUser, isLogged };
