// FixMate - Smart Hybrid API Client with Automatic Live/Offline Switching
const API_BASE_URL = window.location.origin.includes('onrender.com') || window.location.origin.includes(':8080')
  ? window.location.origin + '/api' 
  : 'https://fixmate-api.onrender.com/api';

const Api = {
  isLiveBackend: null,

  getToken() {
    return localStorage.getItem('fixmate_token');
  },

  updateConnectionBadge() {
    const badge = document.getElementById('connection-mode-badge');
    if (!badge) return;
    if (this.isLiveBackend) {
      badge.className = 'status-badge status-completed';
      badge.innerHTML = '🟢 Live Backend Connected';
      badge.title = 'Connected to Spring Boot REST API on http://localhost:8080';
    } else {
      badge.className = 'status-badge status-pending';
      badge.innerHTML = '⚡ Demo Sandbox Mode';
      badge.title = 'Running in instant offline evaluation mode with full interactivity';
    }
  },

  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
    
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(options.headers || {})
    };

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    // Try Live Spring Boot Backend first
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);

      const response = await fetch(url, {
        ...options,
        headers,
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      if (response.ok) {
        const json = await response.json();
        this.isLiveBackend = true;
        this.updateConnectionBadge();
        return json.data !== undefined ? json.data : json;
      }
      
      const errJson = await response.json().catch(() => ({}));
      throw new Error(errJson.message || `Server responded with status ${response.status}`);
    } catch (networkErr) {
      // Backend not running on :8080 or file:// protocol - switch gracefully to Local Store
      this.isLiveBackend = false;
      this.updateConnectionBadge();
      return this.handleOfflineSimulation(endpoint, options);
    }
  },

  handleOfflineSimulation(endpoint, options) {
    const method = (options.method || 'GET').toUpperCase();
    const body = options.body ? JSON.parse(options.body) : {};

    // 1. Auth Login
    if (endpoint.includes('/auth/login')) {
      const email = (body.email || '').toLowerCase().trim();
      const pass = body.password;

      let role = 'CUSTOMER';
      let fullName = 'Demo Customer';
      let profileId = 1;

      if (email.includes('admin')) {
        role = 'ADMIN';
        fullName = 'Platform Admin';
        profileId = 1;
      } else if (email.includes('provider')) {
        role = 'PROVIDER';
        fullName = 'Demo Provider';
        profileId = 1;
      }

      return {
        token: 'demo-jwt-token-fixmate-2026',
        userId: role === 'ADMIN' ? 1 : (role === 'PROVIDER' ? 22 : 2),
        profileId: profileId,
        fullName: fullName,
        email: email,
        role: role,
        status: 'ACTIVE'
      };
    }

    // 2. Auth Profile
    if (endpoint.includes('/auth/me')) {
      const user = window.Auth && window.Auth.user ? window.Auth.user : {
        userId: 2, profileId: 1, fullName: 'Demo Customer', email: 'customer123@fixmate.in', role: 'CUSTOMER'
      };
      return user;
    }

    // 3. Categories
    if (endpoint.startsWith('/categories')) {
      return window.MockData ? window.MockData.categories : [];
    }

    // 4. Services
    if (endpoint.startsWith('/services')) {
      return window.MockData ? window.MockData.services : [];
    }

    // 5. Provider Search
    if (endpoint.startsWith('/providers/search') || endpoint.startsWith('/providers/nearby')) {
      const params = new URLSearchParams(endpoint.split('?')[1] || '');
      const city = params.get('city');
      const categoryId = params.get('categoryId');

      let list = window.MockData ? [...window.MockData.providers] : [];
      if (city) {
        list = list.filter(p => p.city.toLowerCase() === city.toLowerCase());
      }
      if (categoryId) {
        list = list.filter(p => p.services.some(s => {
          const cat = window.MockData.categories.find(c => c.categoryId === Number(categoryId));
          return cat && s.categoryName === cat.name;
        }));
      }
      return list;
    }

    // 6. Provider Single Card
    if (endpoint.match(/\/providers\/(\d+)$/)) {
      const id = Number(endpoint.match(/\/providers\/(\d+)$/)[1]);
      const p = window.MockData.providers.find(x => x.providerId === id) || window.MockData.providers[0];
      return p;
    }

    // 7. Provider Slots
    if (endpoint.includes('/availability')) {
      return window.MockData ? window.MockData.slots : [];
    }

    // 8. Customer Addresses
    if (endpoint === '/customer/addresses') {
      if (method === 'POST') {
        const newId = Date.now();
        window.MockData.customerAddresses.push({ addressId: newId, ...body });
        return newId;
      }
      return window.MockData ? window.MockData.customerAddresses : [];
    }

    // 9. Bookings
    if (endpoint === '/bookings' && method === 'POST') {
      const provider = window.MockData.providers.find(p => p.providerId === body.providerId) || window.MockData.providers[0];
      const service = window.MockData.services.find(s => s.serviceId === body.serviceId) || window.MockData.services[0];
      const slot = window.MockData.slots.find(s => s.slotId === body.slotId) || window.MockData.slots[0];
      const address = window.MockData.customerAddresses.find(a => a.addressId === body.addressId) || window.MockData.customerAddresses[0];

      const base = body.basePrice || service.basePrice;
      const total = Math.round((base + base * 0.10 + base * 0.10 * 0.18) * 100) / 100;
      const bookingNumber = 'FM-' + new Date().toISOString().slice(0,10).replace(/-/g,'') + '-' + Math.floor(10000 + Math.random() * 90000);

      const newBooking = {
        bookingId: Date.now(),
        bookingNumber: bookingNumber,
        serviceName: service.serviceName,
        categoryName: 'General Care',
        providerName: provider.fullName,
        providerPhone: '+919826022001',
        providerRating: provider.ratingAvg,
        customerName: 'Demo Customer',
        customerPhone: '+919826011001',
        streetAddress: address ? address.streetAddress : '14, Main Road',
        city: provider.city,
        bookingDate: body.bookingDate,
        slotName: slot.slotName,
        totalAmount: total,
        baseAmount: base,
        bookingStatus: 'PENDING',
        paymentStatus: body.paymentMethod === 'CASH' ? 'PAY_ON_ARRIVAL' : 'PENDING',
        paymentMethod: body.paymentMethod || 'CASH',
        problemDescription: body.problemDescription || 'Doorstep service inspection'
      };

      window.MockData.customerBookings.unshift(newBooking);
      window.MockData.providerBookings.unshift(newBooking);
      return newBooking;
    }

    if (endpoint === '/customer/bookings') {
      return window.MockData ? window.MockData.customerBookings : [];
    }

    if (endpoint === '/provider/me') {
      return window.MockData ? window.MockData.providers[0] : {};
    }

    if (endpoint === '/provider/bookings') {
      return window.MockData ? window.MockData.providerBookings : [];
    }

    if (endpoint === '/provider/earnings') {
      const p = window.MockData.providers[0];
      return {
        providerId: p.providerId,
        fullName: p.fullName,
        walletBalance: p.walletBalance,
        totalCompletedJobs: p.totalCompletedJobs,
        ledger: window.MockData.providerLedger
      };
    }

    // 10. Booking Lifecycle State Transitions
    if (endpoint.includes('/bookings/') && method === 'PATCH') {
      const match = endpoint.match(/\/bookings\/(\d+)\/(accept|reject|start-travel|start-service|complete|cancel)/);
      if (match) {
        const bId = Number(match[1]);
        const action = match[2];
        const targetStatus = {
          'accept': 'ACCEPTED',
          'reject': 'REJECTED',
          'start-travel': 'ON_THE_WAY',
          'start-service': 'IN_PROGRESS',
          'complete': 'COMPLETED',
          'cancel': 'CANCELLED'
        }[action];

        const updateList = (arr) => {
          const item = arr.find(b => b.bookingId === bId);
          if (item) {
            item.bookingStatus = targetStatus;
            if (targetStatus === 'COMPLETED') {
              item.paymentStatus = 'PAID';
              const p = window.MockData.providers[0];
              p.walletBalance += item.baseAmount;
              p.totalCompletedJobs += 1;
              window.MockData.providerLedger.unshift({
                createdAt: new Date().toISOString().replace('T', ' ').slice(0, 16),
                transactionType: 'CREDIT_BOOKING_PAYOUT',
                amount: item.baseAmount,
                runningBalance: p.walletBalance,
                description: `Earnings credited for job #${item.bookingNumber}`
              });
            }
          }
        };

        updateList(window.MockData.customerBookings);
        updateList(window.MockData.providerBookings);
        return { success: true };
      }
    }

    // 11. Payments Mock Success
    if (endpoint.includes('/payments/mock-success')) {
      const bId = Number(body.bookingId);
      const b = window.MockData.customerBookings.find(x => x.bookingId === bId);
      if (b) b.paymentStatus = 'PAID';
      const pb = window.MockData.providerBookings.find(x => x.bookingId === bId);
      if (pb) pb.paymentStatus = 'PAID';
      return true;
    }

    // 12. Reviews
    if (endpoint === '/reviews' && method === 'POST') {
      const bId = Number(body.bookingId);
      const b = window.MockData.customerBookings.find(x => x.bookingId === bId);
      if (b) {
        b.reviewRating = body.rating;
        b.reviewComment = body.comment;
      }
      return 1;
    }

    // 13. Complaints
    if (endpoint === '/complaints' && method === 'POST') {
      const newCmp = {
        complaintId: Date.now(),
        complaintNumber: 'CMP-' + Math.floor(10000 + Math.random() * 90000),
        bookingNumber: 'FM-Live',
        customerName: 'Demo Customer',
        customerPhone: '+919826011001',
        subject: body.subject,
        description: body.description,
        status: 'OPEN'
      };
      window.MockData.adminComplaints.unshift(newCmp);
      return newCmp.complaintId;
    }

    // 14. Admin APIs
    if (endpoint === '/admin/dashboard') {
      return {
        grossRevenue: 84900.00,
        platformRevenue: 8490.00,
        totalBookings: 148,
        activeProviders: 18,
        totalProviders: 20,
        openComplaints: window.MockData.adminComplaints.filter(c => c.status === 'OPEN').length
      };
    }

    if (endpoint === '/admin/providers') {
      return window.MockData.providers;
    }

    if (endpoint.includes('/admin/providers/') && endpoint.includes('/verify')) {
      const id = Number(endpoint.match(/\/admin\/providers\/(\d+)\/verify/)[1]);
      const p = window.MockData.providers.find(x => x.providerId === id);
      if (p) p.verificationStatus = body.status;
      return { success: true };
    }

    if (endpoint === '/admin/complaints') {
      return window.MockData.adminComplaints;
    }

    if (endpoint.includes('/admin/complaints/') && endpoint.includes('/status')) {
      const id = Number(endpoint.match(/\/admin\/complaints\/(\d+)\/status/)[1]);
      const c = window.MockData.adminComplaints.find(x => x.complaintId === id);
      if (c) {
        c.status = body.status;
        c.remarks = body.remarks;
      }
      return { success: true };
    }

    return [];
  },

  get(endpoint) { return this.request(endpoint, { method: 'GET' }); },
  post(endpoint, body) { return this.request(endpoint, { method: 'POST', body: JSON.stringify(body) }); },
  put(endpoint, body) { return this.request(endpoint, { method: 'PUT', body: JSON.stringify(body) }); },
  patch(endpoint, body) { return this.request(endpoint, { method: 'PATCH', body: body ? JSON.stringify(body) : null }); },
  delete(endpoint) { return this.request(endpoint, { method: 'DELETE' }); }
};

window.Api = Api;
