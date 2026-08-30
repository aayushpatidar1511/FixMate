// FixMate - Admin Governance, Analytics & Verification Module
const AdminCenter = {
  stats: null,
  providers: [],
  complaints: [],

  async init() {
    if (!window.Auth.isLoggedIn() || window.Auth.getRole() !== 'ADMIN') {
      return;
    }
    await Promise.all([
      this.loadDashboardStats(),
      this.loadProviders(),
      this.loadComplaints()
    ]);
  },

  async loadDashboardStats() {
    try {
      this.stats = await window.Api.get('/admin/dashboard');
      this.renderKPIs();
    } catch (e) {
      console.error(e);
    }
  },

  async loadProviders() {
    try {
      this.providers = await window.Api.get('/admin/providers');
      this.renderVerificationQueue();
    } catch (e) {
      console.error(e);
    }
  },

  async loadComplaints() {
    try {
      this.complaints = await window.Api.get('/admin/complaints');
      this.renderComplaintsTable();
    } catch (e) {
      console.error(e);
    }
  },

  renderKPIs() {
    if (!this.stats) return;

    const gmvEl = document.getElementById('admin-stat-gmv');
    const netEl = document.getElementById('admin-stat-net');
    const ordersEl = document.getElementById('admin-stat-orders');
    const provsEl = document.getElementById('admin-stat-provs');
    const dispEl = document.getElementById('admin-stat-disputes');

    if (gmvEl) gmvEl.textContent = `₹${this.stats.grossRevenue ? this.stats.grossRevenue.toFixed(2) : '0.00'}`;
    if (netEl) netEl.textContent = `₹${this.stats.platformRevenue ? this.stats.platformRevenue.toFixed(2) : '0.00'}`;
    if (ordersEl) ordersEl.textContent = this.stats.totalBookings;
    if (provsEl) provsEl.textContent = `${this.stats.activeProviders} / ${this.stats.totalProviders}`;
    if (dispEl) dispEl.textContent = this.stats.openComplaints;
  },

  renderVerificationQueue() {
    const tbody = document.getElementById('admin-providers-tbody');
    if (!tbody) return;

    tbody.innerHTML = this.providers.map(p => {
      let actionBtn = '-';
      if (p.verificationStatus === 'PENDING_VERIFICATION') {
        actionBtn = `
          <button class="btn btn-success btn-sm" onclick="AdminCenter.verifyProvider(${p.providerId}, 'ACTIVE')">Approve</button>
          <button class="btn btn-danger btn-sm" onclick="AdminCenter.verifyProvider(${p.providerId}, 'REJECTED')">Reject</button>
        `;
      } else if (p.verificationStatus === 'ACTIVE') {
        actionBtn = `<button class="btn btn-secondary btn-sm" onclick="AdminCenter.verifyProvider(${p.providerId}, 'BLOCKED')">Block</button>`;
      } else if (p.verificationStatus === 'BLOCKED') {
        actionBtn = `<button class="btn btn-success btn-sm" onclick="AdminCenter.verifyProvider(${p.providerId}, 'ACTIVE')">Unblock</button>`;
      }

      return `
        <tr>
          <td>
            <strong>${p.fullName}</strong><br>
            <small style="color: var(--text-dim);">${p.email}</small>
          </td>
          <td>${p.city}, ${p.state}</td>
          <td>${p.experienceYears} Years</td>
          <td>
            <span style="font-size: 0.8rem; font-family: monospace;">${p.idProofType}: ${p.idProofNumber}</span>
          </td>
          <td>
            <span class="status-badge ${p.verificationStatus === 'ACTIVE' ? 'status-completed' : (p.verificationStatus === 'PENDING_VERIFICATION' ? 'status-pending' : 'status-cancelled')}">
              ${p.verificationStatus}
            </span>
          </td>
          <td>
            ★ ${p.ratingAvg ? p.ratingAvg.toFixed(1) : '0.0'} (${p.totalCompletedJobs} jobs)
          </td>
          <td>${actionBtn}</td>
        </tr>
      `;
    }).join('');
  },

  renderComplaintsTable() {
    const tbody = document.getElementById('admin-complaints-tbody');
    if (!tbody) return;

    if (this.complaints.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 1.5rem; color: var(--text-muted);">No open customer disputes.</td></tr>`;
      return;
    }

    tbody.innerHTML = this.complaints.map(c => `
      <tr>
        <td><strong>${c.complaintNumber}</strong></td>
        <td>Booking #${c.bookingNumber}</td>
        <td>${c.customerName}<br><small style="color: var(--text-dim);">${c.customerPhone}</small></td>
        <td>
          <strong>${c.subject}</strong><br>
          <small style="color: var(--text-muted);">${c.description}</small>
        </td>
        <td>
          <span class="status-badge ${c.status === 'RESOLVED' ? 'status-completed' : (c.status === 'OPEN' ? 'status-cancelled' : 'status-pending')}">
            ${c.status}
          </span>
        </td>
        <td>
          ${c.status !== 'RESOLVED' ? `
            <button class="btn btn-primary btn-sm" onclick="AdminCenter.resolveDispute(${c.complaintId})">Resolve</button>
          ` : '<span style="color: var(--accent-emerald);">Resolved</span>'}
        </td>
      </tr>
    `).join('');
  },

  async verifyProvider(providerId, status) {
    try {
      await window.Api.patch(`/admin/providers/${providerId}/verify`, { status });
      window.Toast.success(`Provider status updated to ${status}`);
      await Promise.all([this.loadDashboardStats(), this.loadProviders()]);
    } catch (e) {
      console.error(e);
    }
  },

  async resolveDispute(complaintId) {
    const remarks = prompt('Enter resolution remarks for customer:');
    if (!remarks) return;

    try {
      await window.Api.patch(`/admin/complaints/${complaintId}/status`, {
        status: 'RESOLVED',
        remarks: remarks
      });
      window.Toast.success('Dispute resolved successfully');
      await this.loadComplaints();
    } catch (e) {
      console.error(e);
    }
  }
};

window.AdminCenter = AdminCenter;
