/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Tracking System
 * Authentication & Session Management
 */

const Auth = {
  SESSION_KEY: 'noise_monitor_admin_session',

  saveSession(data) {
    const session = {
      username: data.username,
      fullName: data.fullName || 'Admin User',
      role: data.role || 'ADMIN',
      token: data.token || 'valid',
      loggedInAt: new Date().toISOString()
    };
    localStorage.setItem(this.SESSION_KEY, JSON.stringify(session));
  },

  getSession() {
    const raw = localStorage.getItem(this.SESSION_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch (e) {
      return null;
    }
  },

  isLoggedIn() {
    return this.getSession() !== null;
  },

  logout() {
    localStorage.removeItem(this.SESSION_KEY);
    window.location.href = 'login.html';
  },

  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = 'login.html';
    }
  },

  initNavbar() {
    const session = this.getSession();
    const userContainer = document.getElementById('navbarUserContainer');
    if (!userContainer) return;

    if (session) {
      userContainer.innerHTML = `
        <div class="user-badge me-2">
          <span class="user-status-dot"></span>
          <span class="fw-semibold text-light">${session.fullName}</span>
          <span class="badge bg-primary ms-1" style="font-size: 0.65rem;">${session.role}</span>
        </div>
        <button class="btn btn-sm btn-outline-danger" id="logoutBtn" title="Logout">
          <i class="bi bi-box-arrow-right"></i>
        </button>
      `;
      document.getElementById('logoutBtn')?.addEventListener('click', () => this.logout());
    } else {
      userContainer.innerHTML = `
        <a href="login.html" class="btn btn-sm btn-cyber-primary">
          <i class="bi bi-shield-lock"></i> Login
        </a>
      `;
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  // If not on login page, verify session
  const path = window.location.pathname;
  if (!path.endsWith('login.html') && !path.endsWith('/') && !path.endsWith('index.html')) {
    Auth.requireAuth();
  }
  Auth.initNavbar();
});
