// FixMate - Authentication & Session Management Module
const Auth = {
  user: null,

  init() {
    const saved = localStorage.getItem('fixmate_user');
    if (saved) {
      try {
        this.user = JSON.parse(saved);
      } catch (e) {
        this.logout();
      }
    }
    this.updateNavbar();
  },

  isLoggedIn() {
    return !!this.user && !!localStorage.getItem('fixmate_token');
  },

  getRole() {
    return this.user ? this.user.role : null;
  },

  async login(email, password) {
    try {
      const data = await window.Api.post('/auth/login', { email, password });
      localStorage.setItem('fixmate_token', data.token);
      
      this.user = {
        userId: data.userId,
        profileId: data.profileId,
        fullName: data.fullName,
        email: data.email,
        role: data.role,
        status: data.status
      };

      localStorage.setItem('fixmate_user', JSON.stringify(this.user));
      this.updateNavbar();
      window.Toast.success(`Welcome back, ${data.fullName}!`);
      
      // Auto-navigate to respective dashboard
      if (data.role === 'ADMIN') {
        window.App.switchView('admin-view');
      } else if (data.role === 'PROVIDER') {
        window.App.switchView('provider-view');
      } else {
        window.App.switchView('customer-view');
      }

      return data;
    } catch (e) {
      throw e;
    }
  },

  async registerCustomer(payload) {
    try {
      const data = await window.Api.post('/auth/register/customer', payload);
      localStorage.setItem('fixmate_token', data.token);
      
      this.user = {
        userId: data.userId,
        profileId: data.profileId,
        fullName: data.fullName,
        email: data.email,
        role: data.role,
        status: data.status
      };

      localStorage.setItem('fixmate_user', JSON.stringify(this.user));
      this.updateNavbar();
      window.Toast.success('Account created successfully!');
      window.App.switchView('marketplace-view');
      return data;
    } catch (e) {
      throw e;
    }
  },

  async registerProvider(payload) {
    try {
      const data = await window.Api.post('/auth/register/provider', payload);
      localStorage.setItem('fixmate_token', data.token);
      
      this.user = {
        userId: data.userId,
        profileId: data.profileId,
        fullName: data.fullName,
        email: data.email,
        role: data.role,
        status: data.status
      };

      localStorage.setItem('fixmate_user', JSON.stringify(this.user));
      this.updateNavbar();
      window.Toast.success('Professional registration submitted! Verification in review.');
      window.App.switchView('provider-view');
      return data;
    } catch (e) {
      throw e;
    }
  },

  logout() {
    localStorage.removeItem('fixmate_token');
    localStorage.removeItem('fixmate_user');
    this.user = null;
    this.updateNavbar();
    window.Toast.info('Logged out successfully');
    window.App.switchView('marketplace-view');
  },

  updateNavbar() {
    const navAuth = document.getElementById('nav-auth-container');
    const rolePortals = document.getElementById('nav-role-portals');
    if (!navAuth) return;

    if (this.isLoggedIn()) {
      let portalBtn = '';
      if (this.user.role === 'CUSTOMER') {
        portalBtn = `<button class="nav-link" onclick="App.switchView('customer-view')">My Bookings</button>`;
      } else if (this.user.role === 'PROVIDER') {
        portalBtn = `<button class="nav-link" onclick="App.switchView('provider-view')">Provider Desk</button>`;
      } else if (this.user.role === 'ADMIN') {
        portalBtn = `<button class="nav-link" onclick="App.switchView('admin-view')">Admin Center</button>`;
      }

      if (rolePortals) rolePortals.innerHTML = portalBtn;

      navAuth.innerHTML = `
        <div style="display: flex; align-items: center; gap: 0.75rem;">
          <span style="font-size: 0.9rem; color: #cbd5e1;">Hi, <strong>${this.user.fullName.split(' ')[0]}</strong></span>
          <span class="status-badge ${this.user.role === 'ADMIN' ? 'status-pending' : (this.user.role === 'PROVIDER' ? 'status-in_progress' : 'status-accepted')}">${this.user.role}</span>
          <button class="btn btn-secondary btn-sm" onclick="Auth.logout()">Logout</button>
        </div>
      `;
    } else {
      if (rolePortals) rolePortals.innerHTML = '';
      navAuth.innerHTML = `
        <button class="btn btn-secondary btn-sm" onclick="App.openModal('login-modal')">Login</button>
        <button class="btn btn-primary btn-sm" onclick="App.openModal('register-choice-modal')">Sign Up</button>
      `;
    }
  }
};

window.Auth = Auth;
