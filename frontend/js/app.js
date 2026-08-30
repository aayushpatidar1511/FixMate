// FixMate - Main Application Orchestrator
const App = {
  currentView: 'marketplace-view',

  async init() {
    window.Auth.init();
    await window.Marketplace.init();

    // Event listener for modals outside click
    document.querySelectorAll('.modal-overlay').forEach(modal => {
      modal.addEventListener('click', (e) => {
        if (e.target === modal) {
          this.closeAllModals();
        }
      });
    });
  },

  switchView(viewId) {
    document.querySelectorAll('.view-section').forEach(sec => {
      sec.style.display = 'none';
    });

    const target = document.getElementById(viewId);
    if (target) {
      target.style.display = 'block';
      this.currentView = viewId;
      window.scrollTo({ top: 0, behavior: 'smooth' });

      // Trigger lazy loads
      if (viewId === 'customer-view') {
        window.CustomerDashboard.init();
      } else if (viewId === 'provider-view') {
        window.ProviderWorkbench.init();
      } else if (viewId === 'admin-view') {
        window.AdminCenter.init();
      } else if (viewId === 'marketplace-view') {
        window.Marketplace.searchProviders();
      }
    }
  },

  openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.add('active');
    }
  },

  closeAllModals() {
    document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('active'));
  },

  // Helper to pre-fill test login credentials for instant demo
  fillDemoCredentials(role) {
    const emailInput = document.getElementById('login-email');
    const passInput = document.getElementById('login-password');

    if (!emailInput || !passInput) return;

    if (role === 'CUSTOMER') {
      emailInput.value = 'customer123';
      passInput.value = 'Password@123';
    } else if (role === 'PROVIDER') {
      emailInput.value = 'provider123';
      passInput.value = 'Password@123';
    } else if (role === 'ADMIN') {
      emailInput.value = 'admin123';
      passInput.value = 'Password@123';
    }
  },

  async handleLoginSubmit(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const pass = document.getElementById('login-password').value;

    try {
      await window.Auth.login(email, pass);
      this.closeAllModals();
    } catch (err) {
      console.error(err);
    }
  },

  async handleCustomerRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('reg-c-name').value.trim();
    const email = document.getElementById('reg-c-email').value.trim();
    const phone = document.getElementById('reg-c-phone').value.trim();
    const password = document.getElementById('reg-c-pass').value;

    try {
      await window.Auth.registerCustomer({ fullName, email, phone, password });
      this.closeAllModals();
    } catch (err) {
      console.error(err);
    }
  },

  async handleProviderRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('reg-p-name').value.trim();
    const email = document.getElementById('reg-p-email').value.trim();
    const phone = document.getElementById('reg-p-phone').value.trim();
    const password = document.getElementById('reg-p-pass').value;
    const experienceYears = Number(document.getElementById('reg-p-exp').value);
    const city = document.getElementById('reg-p-city').value;
    const address = document.getElementById('reg-p-addr').value.trim();
    const pincode = document.getElementById('reg-p-pincode').value.trim();
    const idProofNumber = document.getElementById('reg-p-idnum').value.trim();

    try {
      await window.Auth.registerProvider({
        fullName,
        email,
        phone,
        password,
        experienceYears,
        city,
        state: 'Madhya Pradesh',
        address,
        pincode,
        latitude: 23.1765,
        longitude: 75.7885,
        idProofType: 'AADHAAR',
        idProofNumber,
        serviceIds: [1, 2, 3] // Default to general electrical / repair
      });
      this.closeAllModals();
    } catch (err) {
      console.error(err);
    }
  }
};

window.App = App;

// Bootstrap when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  window.App.init();
});
