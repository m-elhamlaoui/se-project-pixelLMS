import { v1, handleUnauthorized } from '../apiUtils';

// Upload a file
async function uploadFile(file, attachToId, foreignKeyType) {
    const allowedfiles = [
        'application/pdf',
        'application/zip',
        'application/x-zip-compressed',
        'application/x-zip',
        'application/x-7z-compressed',
        'application/x-rar-compressed',
        'application/x-tar',
        'application/x-gzip',
        'application/x-bzip2'
    ];
    if (!allowedfiles.includes(file.type)) {
        throw new Error('Le type de fichier n\'est pas autorisé. Les types autorisés sont : PDF ou dossier compressé ');
    }
    if (file.size > 50000000) {
        throw new Error('La taille du fichier ne doit pas dépasser 50 Mb');
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('attachToId', attachToId);
    formData.append('foreignKeyType', foreignKeyType);

    const response = await fetch(v1 + 'api/file/', {
        method: 'POST',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        },
        body: formData
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Upload failed: ${response.statusText}`);
    }
    return await response.text();
}

// Download a file
async function downloadFile(fileId) {
    const response = await fetch(v1 + 'api/file/download/' + fileId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });

    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Download failed: ${response.statusText}`);
    }

    // Create a Blob from the response
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `file_${fileId}`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url); 
}

// Fetch file metadata by ID
async function getFileById(fileId) {
    const response = await fetch(v1 + 'api/file/' + fileId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Fetch file metadata failed: ${response.statusText}`);
    }
    return await response.json();
}

// Fetch files associated with a course
async function getFilesByCourse(courseId) {
    const response = await fetch(v1 + 'api/file/course/' + courseId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Fetch files by course failed: ${response.statusText}`);
    }
    return await response.json();
}

// Fetch files associated with a task
async function getFilesByTask(taskId) {
    const response = await fetch(v1 + 'api/file/task/' + taskId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Fetch files by task failed: ${response.statusText}`);
    }
    return await response.json();
}

// Fetch files associated with a message
async function getFilesByMessage(messageId) {
    const response = await fetch(v1 + 'api/file/message/' + messageId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Fetch files by message failed: ${response.statusText}`);
    }
    return await response.json();
}

// Fetch files associated with a profile
async function getFilesByProfile(profileId) {
    const response = await fetch(v1 + 'api/file/profile/' + profileId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Fetch files by profile failed: ${response.statusText}`);
    }
    return await response.json();
}

// Fetch files associated with a user
async function getFilesByUser(userId) {
    const response = await fetch(v1 + 'api/file/user/' + userId, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('serversecuritytoken')
        }
    });
    await handleUnauthorized(response);

    if (!response.ok) {
        throw new Error(`Fetch files by user failed: ${response.statusText}`);
    }
    return await response.json();
}

export {
    uploadFile,
    downloadFile,
    getFileById,
    getFilesByCourse,
    getFilesByTask,
    getFilesByMessage,
    getFilesByProfile,
    getFilesByUser
};
