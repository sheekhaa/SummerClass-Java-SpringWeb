// Configuration
const API_BASE_URL = 'http://localhost:8080/api';
const SESSION_KEY = 'user_session';
const TOKEN_KEY = 'jwt_token';

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
});

// Authentication Check
function checkAuth() {
    const token = localStorage.getItem(TOKEN_KEY);
    const username = localStorage.getItem('username');
    
    if (token && username) {
        showAuthenticatedUI(username);
    } else {
        showAuthenticationUI();
    }
}

// Show Authentication UI
function showAuthenticationUI() {
    hideAllSections();
    document.getElementById('authSection').style.display = 'block';
    document.getElementById('authSection').classList.add('active');
    clearForms();
}

// Show Authenticated UI
function showAuthenticatedUI(username) {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('authSection').classList.remove('active');
    document.getElementById('welcomeUser').textContent = username;
    document.getElementById('authLink').textContent = 'Logout';
    showSection('home');
}

// Section Navigation
function showSection(sectionName) {
    hideAllSections();
    
    const section = document.getElementById(sectionName + 'Section');
    if (section) {
        section.style.display = 'block';
        section.classList.add('active');
        
        // Load data for specific sections
        if (sectionName === 'users') {
            loadUsers();
        } else if (sectionName === 'gallery') {
            loadGallery();
        }
    }
}

function hideAllSections() {
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => {
        section.style.display = 'none';
        section.classList.remove('active');
    });
}

// Login Function
function login(event) {
    event.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const messageDiv = document.getElementById('message');
    
    messageDiv.classList.remove('success', 'error');
    messageDiv.style.display = 'none';
    
    fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.token) {
            // Store token and username
            localStorage.setItem(TOKEN_KEY, data.token);
            localStorage.setItem('username', username);
            
            showMessage('Login successful!', 'success');
            setTimeout(() => {
                showAuthenticatedUI(username);
            }, 500);
        } else {
            showMessage(data.message || 'Login failed', 'error');
        }
    })
    .catch(error => {
        console.error('Login error:', error);
        showMessage('Login failed. Please try again.', 'error');
    });
}

// Signup Function
function signup(event) {
    event.preventDefault();
    const username = document.getElementById('signupUsername').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const messageDiv = document.getElementById('message');
    
    messageDiv.classList.remove('success', 'error');
    messageDiv.style.display = 'none';
    
    fetch(`${API_BASE_URL}/auth/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.message && data.message.includes('successfully')) {
            showMessage('Signup successful! Please log in.', 'success');
            setTimeout(() => {
                toggleSignup();
                clearForms();
            }, 500);
        } else {
            showMessage(data.message || 'Signup failed', 'error');
        }
    })
    .catch(error => {
        console.error('Signup error:', error);
        showMessage('Signup failed. Please try again.', 'error');
    });
}

// Toggle Signup Form
function toggleSignup() {
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');
    
    if (loginForm.style.display === 'none') {
        loginForm.style.display = 'block';
        signupForm.style.display = 'none';
    } else {
        loginForm.style.display = 'none';
        signupForm.style.display = 'block';
    }
}

// Logout Function
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('username');
    showAuthenticationUI();
    showMessage('You have been logged out.', 'success');
}

// Load Users
function loadUsers() {
    const token = localStorage.getItem(TOKEN_KEY);
    const usersList = document.getElementById('usersList');
    
    usersList.innerHTML = '<p>Loading users...</p>';
    
    fetch(`${API_BASE_URL}/users`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.status === 401) {
            logout();
            return;
        }
        return response.json();
    })
    .then(data => {
        if (Array.isArray(data)) {
            if (data.length === 0) {
                usersList.innerHTML = '<p>No users found.</p>';
            } else {
                usersList.innerHTML = data.map(user => `
                    <div class="user-card">
                        <h3>${escapeHtml(user.username)}</h3>
                        <p><strong>Email:</strong> ${escapeHtml(user.email)}</p>
                        <p><strong>ID:</strong> ${user.id}</p>
                        <div class="user-card-actions">
                            <button class="btn btn-primary" onclick="editUser(${user.id})">Edit</button>
                            <button class="btn btn-danger" onclick="deleteUser(${user.id})">Delete</button>
                        </div>
                    </div>
                `).join('');
            }
        } else {
            usersList.innerHTML = '<p>Error loading users.</p>';
        }
    })
    .catch(error => {
        console.error('Load users error:', error);
        usersList.innerHTML = '<p>Failed to load users.</p>';
    });
}

// Show Add User Form
function showAddUserForm() {
    document.getElementById('addUserForm').style.display = 'block';
}

// Hide Add User Form
function hideAddUserForm() {
    document.getElementById('addUserForm').style.display = 'none';
    document.getElementById('newUsername').value = '';
    document.getElementById('newEmail').value = '';
}

// Add User
function addUser(event) {
    event.preventDefault();
    const token = localStorage.getItem(TOKEN_KEY);
    const username = document.getElementById('newUsername').value;
    const email = document.getElementById('newEmail').value;
    
    fetch(`${API_BASE_URL}/users`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, email })
    })
    .then(response => response.json())
    .then(data => {
        hideAddUserForm();
        loadUsers();
        showMessage('User created successfully!', 'success');
    })
    .catch(error => {
        console.error('Add user error:', error);
        showMessage('Failed to create user.', 'error');
    });
}

// Edit User
function editUser(userId) {
    const newUsername = prompt('Enter new username:');
    if (!newUsername) return;
    
    const newEmail = prompt('Enter new email:');
    if (!newEmail) return;
    
    const token = localStorage.getItem(TOKEN_KEY);
    
    fetch(`${API_BASE_URL}/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username: newUsername, email: newEmail })
    })
    .then(response => response.json())
    .then(data => {
        loadUsers();
        showMessage('User updated successfully!', 'success');
    })
    .catch(error => {
        console.error('Edit user error:', error);
        showMessage('Failed to update user.', 'error');
    });
}

// Delete User
function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    
    const token = localStorage.getItem(TOKEN_KEY);
    
    fetch(`${API_BASE_URL}/users/${userId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            loadUsers();
            showMessage('User deleted successfully!', 'success');
        } else {
            showMessage('Failed to delete user.', 'error');
        }
    })
    .catch(error => {
        console.error('Delete user error:', error);
        showMessage('Failed to delete user.', 'error');
    });
}

// Load Gallery
function loadGallery() {
    const token = localStorage.getItem(TOKEN_KEY);
    const galleryGrid = document.getElementById('galleryGrid');
    
    galleryGrid.innerHTML = '<p>Loading gallery...</p>';
    
    fetch(`${API_BASE_URL}/gallery`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.status === 401) {
            logout();
            return;
        }
        return response.json();
    })
    .then(data => {
        if (Array.isArray(data) && data.length > 0) {
            galleryGrid.innerHTML = data.map(item => `
                <div class="gallery-item">
                    <img src="${escapeHtml(item.imageUrl)}" alt="Gallery item">
                    <div style="padding: 1rem;">
                        <p><strong>${escapeHtml(item.title)}</strong></p>
                    </div>
                </div>
            `).join('');
        } else {
            galleryGrid.innerHTML = '<p>No gallery items found.</p>';
        }
    })
    .catch(error => {
        console.error('Load gallery error:', error);
        galleryGrid.innerHTML = '<p>Failed to load gallery.</p>';
    });
}

// Show Message
function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.classList.remove('success', 'error');
    messageDiv.classList.add(type);
    messageDiv.style.display = 'block';
    
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

// Clear Forms
function clearForms() {
    document.getElementById('loginUsername').value = '';
    document.getElementById('loginPassword').value = '';
    document.getElementById('signupUsername').value = '';
    document.getElementById('signupEmail').value = '';
    document.getElementById('signupPassword').value = '';
}

// Escape HTML to prevent XSS
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
