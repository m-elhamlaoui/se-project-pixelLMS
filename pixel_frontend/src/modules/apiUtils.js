const v1 = 'http://10.50.17.61:8080/';

async function handleUnauthorized(response) {
    if (response.status === 401) {
        const currentPath = window.location.pathname;
        if (currentPath === '/auth') {
            return;
        }796
        localStorage.removeItem('serversecuritytoken');
        localStorage.removeItem('user');
        localStorage.removeItem('course');
        window.location.reload();
    }
}

export { v1, handleUnauthorized };
