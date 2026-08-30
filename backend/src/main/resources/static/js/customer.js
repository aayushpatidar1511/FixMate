// FixMate - Customer Dashboard & Booking Tracker Module
const CustomerDashboard = {
  bookings: [],
  addresses: [],

  async init() {
    if (!window.Auth.isLoggedIn() || window.Auth.getRole() !== 'CUSTOMER') {
      return;
    }
    await Promise.all([
      this.loadBookings(),
      this.loadAddresses()
    ]);
  },

  async loadBookings() {
    try {
      this.bookings = await window.Api.get('/customer/bookings');
      this.renderActiveTracker();
      this.renderBookingHistory();
    } catch (e) {
      console.error('Failed to load customer bookings', e);
    }
  },

  async loadAddresses() {
    try {
      this.addresses = await window.Api.get('/customer/addresses');
      this.renderAddresses();
    } catch (e) {
      console.error('Failed to load customer addresses', e);
    }
  },

  renderActiveTracker() {
    const container = document.getElementById('customer-active-tracker');
    if (!container) return;

    // Find the latest ongoing active booking
    const active = this.bookings.find(b => 
      ['PENDING', 'ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS'].includes(b.bookingStatus)
    );

    if (!active) {
      container.innerHTML = `
        <div style="text-align: center; padding: 2.5rem; background: var(--bg-card); border-radius: var(--radius-lg); border: 1px dashed var(--border-subtle);">
          <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">✨</div>
          <h4 style="font-size: 1.1rem; margin-bottom: 0.25rem;">No Active Service Underway</h4>
          <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 1.25rem;">Need something fixed or serviced today?</p>
          <button class="btn btn-primary btn-sm" onclick="App.switchView('marketplace-view')">Book A Technician</button>
        </div>
      `;
      return;
    }

    const statuses = ['PENDING', 'ACCEPTED', 'ON_THE_WAY', 'IN_PROGRESS', 'COMPLETED'];
    const currentIdx = statuses.indexOf(active.bookingStatus);

    container.innerHTML = `
      <div style="background: var(--bg-card); border: 1px solid var(--border-active); border-radius: var(--radius-lg); padding: 1.5rem; margin-bottom: 2rem; box-shadow: var(--shadow-glow);">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem;">
          <div>
            <span class="status-badge status-${active.bookingStatus.toLowerCase()}">Live Service: ${active.bookingStatus.replace(/_/g, ' ')}</span>
            <h3 style="font-size: 1.3rem; margin-top: 0.4rem;">${active.serviceName}</h3>
            <div style="font-size: 0.88rem; color: var(--text-muted);">Booking #${active.bookingNumber} • Scheduled: ${active.bookingDate} (${active.slotName.split('(')[0]})</div>
          </div>
          <div style="text-align: right;">
            <div style="font-size: 0.8rem; color: var(--text-dim);">Technician</div>
            <div style="font-weight: 700; font-size: 1.1rem;">${active.providerName}</div>
            <div style="font-size: 0.85rem; color: var(--accent-cyan);">📞 ${active.providerPhone}</div>
          </div>
        </div>

        <div class="stepper-container">
          <div class="step-item ${currentIdx >= 0 ? (currentIdx === 0 ? 'active' : 'completed') : ''}">
            <div class="step-dot">1</div>
            <span class="step-label">Requested</span>
          </div>
          <div class="step-item ${currentIdx >= 1 ? (currentIdx === 1 ? 'active' : 'completed') : ''}">
            <div class="step-dot">2</div>
            <span class="step-label">Accepted</span>
          </div>
          <div class="step-item ${currentIdx >= 2 ? (currentIdx === 2 ? 'active' : 'completed') : ''}">
            <div class="step-dot">3</div>
            <span class="step-label">On The Way</span>
          </div>
          <div class="step-item ${currentIdx >= 3 ? (currentIdx === 3 ? 'active' : 'completed') : ''}">
            <div class="step-dot">4</div>
            <span class="step-label">In Progress</span>
          </div>
          <div class="step-item ${currentIdx >= 4 ? 'completed' : ''}">
            <div class="step-dot">5</div>
            <span class="step-label">Done</span>
          </div>
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1rem;">
          <button class="btn btn-secondary btn-sm" onclick="CustomerDashboard.openComplaintModal(${active.bookingId})">Raise Dispute</button>
          <button class="btn btn-danger btn-sm" onclick="CustomerDashboard.cancelBooking(${active.bookingId})">Cancel Booking</button>
        </div>
      </div>
    `;
  },

  renderBookingHistory() {
    const tableBody = document.getElementById('customer-history-tbody');
    if (!tableBody) return;

    if (this.bookings.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem; color: var(--text-muted);">No bookings found</td></tr>`;
      return;
    }

    tableBody.innerHTML = this.bookings.map(b => {
      let actionBtn = '';
      if (b.bookingStatus === 'COMPLETED') {
        if (b.reviewRating) {
          actionBtn = `<span style="color: #fbbf24; font-weight: 700; font-size: 0.85rem;">★ ${b.reviewRating}/5 Rated</span>`;
        } else {
          actionBtn = `<button class="btn btn-primary btn-sm" onclick="CustomerDashboard.openReviewModal(${b.bookingId}, '${b.serviceName}', '${b.providerName}')">Write Review</button>`;
        }
      } else if (['PENDING', 'ACCEPTED'].includes(b.bookingStatus)) {
        actionBtn = `<button class="btn btn-danger btn-sm" onclick="CustomerDashboard.cancelBooking(${b.bookingId})">Cancel</button>`;
      }

      return `
        <tr>
          <td>
            <strong>${b.bookingNumber}</strong><br>
            <small style="color: var(--text-dim);">${b.bookingDate}</small>
          </td>
          <td>
            <strong>${b.serviceName}</strong><br>
            <small style="color: var(--text-muted);">${b.categoryName}</small>
          </td>
          <td>
            ${b.providerName}<br>
            <small style="color: var(--text-dim);">★ ${b.providerRating ? b.providerRating.toFixed(1) : '4.8'}</small>
          </td>
          <td>
            <strong>₹${b.totalAmount}</strong><br>
            <small style="color: var(--text-muted);">${(b.paymentMethod === 'CASH' || b.paymentStatus === 'PAY_ON_ARRIVAL') ? '💵 Pay on Arrival' : (b.paymentStatus === 'PAID' ? '✅ Paid' : b.paymentStatus)}</small>
          </td>
          <td>
            <span class="status-badge status-${b.bookingStatus.toLowerCase()}">${b.bookingStatus}</span>
          </td>
          <td>${actionBtn}</td>
        </tr>
      `;
    }).join('');
  },

  renderAddresses() {
    const container = document.getElementById('customer-addresses-grid');
    if (!container) return;

    container.innerHTML = this.addresses.map(a => `
      <div style="background: var(--bg-card); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 1.25rem;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
          <strong>${a.label} ${a.isDefault ? '<span class="status-badge status-accepted" style="font-size: 0.65rem;">Default</span>' : ''}</strong>
          <button style="background: transparent; border: none; color: #f43f5e; cursor: pointer; font-size: 0.85rem;" onclick="CustomerDashboard.deleteAddress(${a.addressId})">Delete</button>
        </div>
        <p style="font-size: 0.88rem; color: var(--text-muted);">${a.streetAddress}, ${a.landmark ? a.landmark + ', ' : ''}${a.city} - ${a.pincode}</p>
      </div>
    `).join('') + `
      <button class="btn btn-secondary" style="border: 2px dashed var(--border-subtle); height: 100%; min-height: 100px;" onclick="App.openModal('add-address-modal')">
        + Add New Address
      </button>
    `;
  },

  async cancelBooking(bookingId) {
    const reason = prompt('Please enter a cancellation reason:');
    if (!reason) return;

    try {
      await window.Api.patch(`/bookings/${bookingId}/cancel`, { reason });
      window.Toast.success('Booking cancelled successfully');
      await this.loadBookings();
    } catch (e) {
      console.error(e);
    }
  },

  openReviewModal(bookingId, serviceName, providerName) {
    document.getElementById('review-booking-id').value = bookingId;
    document.getElementById('review-service-title').textContent = `${serviceName} with ${providerName}`;
    window.App.openModal('submit-review-modal');
  },

  async submitReviewForm(e) {
    e.preventDefault();
    const bookingId = document.getElementById('review-booking-id').value;
    const rating = document.querySelector('input[name="review_rating"]:checked')?.value || 5;
    const comment = document.getElementById('review-comment-input').value.trim();

    try {
      await window.Api.post('/reviews', {
        bookingId: Number(bookingId),
        rating: Number(rating),
        comment: comment
      });

      window.Toast.success('Review submitted successfully!');
      window.App.closeAllModals();
      await this.loadBookings();
    } catch (e) {
      console.error(e);
    }
  },

  openComplaintModal(bookingId) {
    document.getElementById('complaint-booking-id').value = bookingId;
    window.App.openModal('raise-complaint-modal');
  },

  async submitComplaintForm(e) {
    e.preventDefault();
    const bookingId = document.getElementById('complaint-booking-id').value;
    const subject = document.getElementById('complaint-subject').value.trim();
    const description = document.getElementById('complaint-description').value.trim();

    try {
      await window.Api.post('/complaints', {
        bookingId: Number(bookingId),
        subject,
        description
      });

      window.Toast.success('Dispute registered. Our admin team has been alerted.');
      window.App.closeAllModals();
    } catch (e) {
      console.error(e);
    }
  },

  async saveNewAddress(e) {
    e.preventDefault();
    const label = document.getElementById('addr-label').value;
    const street = document.getElementById('addr-street').value;
    const landmark = document.getElementById('addr-landmark').value;
    const city = document.getElementById('addr-city').value;
    const pincode = document.getElementById('addr-pincode').value;

    try {
      await window.Api.post('/customer/addresses', {
        label,
        streetAddress: street,
        landmark,
        city,
        state: 'Madhya Pradesh',
        pincode,
        isDefault: false
      });

      window.Toast.success('Address saved');
      window.App.closeAllModals();
      await this.loadAddresses();
    } catch (e) {
      console.error(e);
    }
  }
};

window.CustomerDashboard = CustomerDashboard;
