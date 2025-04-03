document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    
    // Redirect if not authenticated
    if (!token) {
        window.location.href = '../login/login.html';
        return;
    }

    // Load profile
    try {
        const profileResponse = await fetch('http://localhost:8080/auth/check-auth', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const profile = await profileResponse.json();
        
        if (profile.authenticated) {
            // Update profile section
            document.querySelector('.profile-name').textContent = profile.username;
            document.querySelector('.profile-pic').src = 'https://bootdey.com/img/Content/avatar/avatar6.png'; // Update with real URL later
        }
    } catch (error) {
        console.error('Profile load error:', error);
    }

    // Load posts
    try {
        const postsResponse = await fetch('http://localhost:8080/api/post', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const posts = await postsResponse.json();
        
        // Clear existing posts
        const postContainer = document.querySelector('.middle-wrapper');
        postContainer.innerHTML = '';
        
        // Render posts
        posts.forEach(post => {
            postContainer.innerHTML += `
                <div class="col-md-12 grid-margin">
                    <div class="card rounded">
                        <div class="card-header">
                            <div class="d-flex align-items-center">
                                <img class="img-xs rounded-circle" src="https://bootdey.com/img/Content/avatar/avatar6.png" alt="">
                                <div class="ml-2">
                                    <p>${post.autor.username}</p>
                                    <p class="tx-11 text-muted">${new Date(post.createdAt).toLocaleDateString()}</p>
                                </div>
                            </div>
                        </div>
                        <div class="card-body">
                            <h5>${post.titulo}</h5>
                            <p class="mb-3 tx-14">${post.conteudo}</p>
                        </div>
                    </div>
                </div>
            `;
        });
    } catch (error) {
        console.error('Posts load error:', error);
    }
});

// Add logout functionality
document.querySelector('.logout-btn').addEventListener('click', () => {
    localStorage.removeItem('token');
    window.location.href = '../login/login.html';
});