// FixMate - Service Provider Operations & Workbench Module
const ProviderWorkbench = {
  profile: null,
  bookings: [],
  earningsData: null,

  async init() {
    if (!window.Auth.isLoggedIn() || window.Auth.getRole() !== 'PROVIDER') {
      return;
    }
    await Promise.all([
      this.loadProfile(),
      this.loadBookings(),
      this.loadEarnings()
    ]);
  },

  async loadProfile() {
    try {
      this.profile = await window.Api.get('/provider/me');
      this.renderProfileCard();
    } catch (e) {
      console.error(e);
    }
  },

  async loadBookings() {
    try {
      this.bookings = await window.Api.get('/provider/bookings');
      this.renderPendingRequests();
      this.renderJobsTable();
    } catch (e) {
      console.error(e);
    }
  },

  async loadEarnings() {
    try {
      this.earningsData = await window.Api.get('/provider/earnings');
      this.renderEarningsCard();
    } catch (e) {
      console.error(e);
    }
  },

  renderProfileCard() {
    const banner = document.getElementById('provider-banner');
    if (!banner || !this.profile) return;

    banner.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); padding: 1.5rem; margin-bottom: 2rem;">
        <div style="display: flex; align-items: center; gap: 1rem;">
          <div class="provider-avatar" style="width: 64px; height: 64px; font-size: 1.5rem;">
            ${this.profile.fullName.substring(0, 2).toUpperCase()}
          </div>
          <div>
            <h2 style="font-size: 1.4rem; font-weight: 700;">${this.profile.fullName}</h2>
            <div style="font-size: 0.88rem; color: var(--text-muted);">
              📍 ${this.profile.city}, MP • 💼 ${this.profile.experienceYears} Years Exp • 
              <span class="rating-badge">★ ${this.profile.ratingAvg.toFixed(1)} (${this.profile.ratingCount} reviews)</span>
            </div>
          </div>
        </div>
        <div>
          <span class="status-badge status-completed" style="font-size: 0.85rem;">Status: ACTIVE</span>
        </div>
      </div>
    `;
  },

  renderEarningsCard() {
    if (!this.earningsData) return;
    const balanceEl = document.getElementById('provider-wallet-balance');
    const jobsEl = document.getElementById('provider-completed-count');
    const tbody = document.getElementById('provider-ledger-tbody');

    if (balanceEl) balanceEl.textContent = `₹${this.earningsData.walletBalance.toFixed(2)}`;
    if (jobsEl) jobsEl.textContent = this.earningsData.totalCompletedJobs;

    if (tbody && this.earningsData.ledger) {
      tbody.innerHTML = this.earningsData.ledger.map(l => `
        <tr>
          <td>${l.createdAt ? l.createdAt.replace('T', ' ').substring(0, 16) : '-'}</td>
          <td><span class="status-badge ${l.transactionType.includes('CREDIT') ? 'status-completed' : 'status-cancelled'}">${l.transactionType}</span></td>
          <td><strong>₹${l.amount.toFixed(2)}</strong></td>
          <td>₹${l.runningBalance.toFixed(2)}</td>
          <td>${l.description}</td>
        </tr>
      `).join('');
    }
  },

  renderPendingRequests() {
    const container = document.getElementById('provider-pending-requests');
    if (!container) return;

    const pending = this.bookings.filter(b => b.bookingStatus === 'PENDING');

    if (pending.length === 0) {
      container.innerHTML = `<div style="text-align: center; padding: 1.5rem; color: var(--text-muted);">No pending job requests at this moment.</div>`;
      return;
    }

    container.innerHTML = pending.map(b => `
      <div style="background: var(--bg-surface-elevated); border: 1px solid var(--accent-amber); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1rem;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
          <span class="status-badge status-pending">New Request #${b.bookingNumber}</span>
          <strong style="color: var(--accent-emerald); font-size: 1.1rem;">₹${b.baseAmount}</strong>
        </div>
        <h4 style="font-size: 1.1rem; margin-bottom: 0.25rem;">${b.serviceName}</h4>
        <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 0.75rem;">
          <strong>Issue:</strong> "${b.problemDescription}"
        </p>
        <div style="font-size: 0.82rem; color: var(--text-dim); margin-bottom: 1rem;">
          📅 Date: ${b.bookingDate} (${b.slotName.split('(')[0]}) • 📍 ${b.streetAddress}, ${b.city}
        </div>
        <div style="display: flex; gap: 0.75rem;">
          <button class="btn btn-success btn-sm" onclick="ProviderWorkbench.acceptJob(${b.bookingId})">Accept Job</button>
          <button class="btn btn-danger btn-sm" onclick="ProviderWorkbench.rejectJob(${b.bookingId})">Decline</button>
        </div>
      </div>
    `).join('');
  },

  renderJobsTable() {
    const tbody = document.getElementById('provider-jobs-tbody');
    if (!tbody) return;

    const activeOrPast = this.bookings.filter(b => b.bookingStatus !== 'PENDING');

    if (activeOrPast.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem; color: var(--text-muted);">No active or completed jobs.</td></tr>`;
      return;
    }

    tbody.innerHTML = activeOrPast.map(b => {
      let actionBtn = '-';

      if (b.bookingStatus === 'ACCEPTED') {
        actionBtn = `<button class="btn btn-primary btn-sm" onclick="ProviderWorkbench.startTravel(${b.bookingId})">Start Travel 🛵</button>`;
      } else if (b.bookingStatus === 'ON_THE_WAY') {
        actionBtn = `<button class="btn btn-primary btn-sm" onclick="ProviderWorkbench.startWork(${b.bookingId})">Start Work ⚙️</button>`;
      } else if (b.bookingStatus === 'IN_PROGRESS') {
        actionBtn = `<button class="btn btn-success btn-sm" onclick="ProviderWorkbench.completeJob(${b.bookingId})">Complete Job ✅</button>`;
      }

      return `
        <tr>
          <td>
            <strong>${b.bookingNumber}</strong><br>
            <small style="color: var(--text-dim);">${b.bookingDate}</small>
          </td>
          <td>${b.serviceName}</td>
          <td>
            <strong>${b.customerName}</strong><br>
            <small style="color: var(--accent-cyan);">📞 ${b.customerPhone}</small>
          </td>
          <td>
            <strong>₹${b.baseAmount}</strong><br>
            <small style="color: var(--text-muted);">${(b.paymentMethod === 'CASH' || b.paymentStatus === 'PAY_ON_ARRIVAL') ? '💵 Cash on Arrival' : '✅ Online Paid'}</small>
          </td>
          <td><span class="status-badge status-${b.bookingStatus.toLowerCase()}">${b.bookingStatus}</span></td>
          <td>${actionBtn}</td>
        </tr>
      `;
    }).join('');
  },

  async acceptJob(bookingId) {
    try {
      await window.Api.patch(`/bookings/${bookingId}/accept`);
      window.Toast.success('Booking accepted! Customer has been alerted.');
      await this.loadBookings();
    } catch (e) {
      console.error(e);
    }
  },

  async rejectJob(bookingId) {
    const reason = prompt('Reason for declining job:');
    if (!reason) return;

    try {
      await window.Api.patch(`/bookings/${bookingId}/reject`, { reason });
      window.Toast.info('Booking request declined');
      await this.loadBookings();
    } catch (e) {
      console.error(e);
    }
  },

  async startTravel(bookingId) {
    try {
      await window.Api.patch(`/bookings/${bookingId}/start-travel`);
      window.Toast.info('Status updated: Technician On The Way');
      await this.loadBookings();
    } catch (e) {
      console.error(e);
    }
  },

  async startWork(bookingId) {
    try {
      await window.Api.patch(`/bookings/${bookingId}/start-service`);
      window.Toast.info('Status updated: Service in progress');
      await this.loadBookings();
    } catch (e) {
      console.error(e);
    }
  },

  async completeJob(bookingId) {
    if (!confirm('Confirm that all requested work has been completed to the customer\'s satisfaction?')) {
      return;
    }

    try {
      await window.Api.patch(`/bookings/${bookingId}/complete`);
      window.Toast.success('Job marked completed! Payout added to your wallet.');
      await Promise.all([this.loadBookings(), this.loadEarnings()]);
    } catch (e) {
      console.error(e);
    }
  }
};

window.ProviderWorkbench = ProviderWorkbench;
