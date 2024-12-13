const v1 = 'http://localhost:8080/';

async function handleUnauthorized(response) {
    if (response.status === 401) {
        const currentPath = window.location.pathname;
        if (currentPath === '/auth') {
            return;
        }
        localStorage.removeItem('serversecuritytoken');
        localStorage.removeItem('user');
        localStorage.removeItem('project');
        window.location.reload();
    }
}

export { v1, handleUnauthorized };
