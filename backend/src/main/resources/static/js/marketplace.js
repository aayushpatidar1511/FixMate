// FixMate - Marketplace Discovery & Provider Search Module
const Marketplace = {
  categories: [],
  services: [],
  providers: [],
  selectedCategoryId: null,
  selectedCity: 'Ujjain',

  async init() {
    await Promise.all([
      this.loadCategories(),
      this.loadServices(),
      this.searchProviders()
    ]);
  },

  async loadCategories() {
    try {
      this.categories = await window.Api.get('/categories');
      this.renderCategoryPills();
    } catch (e) {
      console.error('Failed to load categories', e);
    }
  },

  async loadServices() {
    try {
      this.services = await window.Api.get('/services');
      this.populateServiceDropdowns();
    } catch (e) {
      console.error('Failed to load services', e);
    }
  },

  renderCategoryPills() {
    const container = document.getElementById('category-pills-container');
    if (!container) return;

    let html = `
      <div class="category-pill ${!this.selectedCategoryId ? 'active' : ''}" onclick="Marketplace.filterCategory(null)">
        <span>✨ All Services</span>
      </div>
    `;

    this.categories.forEach(cat => {
      html += `
        <div class="category-pill ${this.selectedCategoryId === cat.categoryId ? 'active' : ''}" 
             onclick="Marketplace.filterCategory(${cat.categoryId})">
          <span>${this.getCategoryIcon(cat.slug)}</span>
          <span>${cat.name}</span>
        </div>
      `;
    });

    container.innerHTML = html;
  },

  getCategoryIcon(slug) {
    const map = {
      'electrical': '⚡',
      'ac-cooling': '❄️',
      'plumbing': '🔧',
      'cleaning': '✨',
      'carpentry': '🪚',
      'appliance-repair': '⚙️',
      'computer-repair': '💻',
      'mobile-repair': '📱',
      'painting': '🎨',
      'pest-control': '🛡️'
    };
    return map[slug] || '🛠️';
  },

  populateServiceDropdowns() {
    const searchSelect = document.getElementById('search-service-select');
    if (searchSelect) {
      searchSelect.innerHTML = '<option value="">All Service Specialities</option>' +
        this.services.map(s => `<option value="${s.serviceId}">${s.serviceName} (from ₹${s.basePrice})</option>`).join('');
    }
  },

  filterCategory(catId) {
    this.selectedCategoryId = catId;
    this.renderCategoryPills();
    this.searchProviders();
  },

  async searchProviders() {
    const cityInput = document.getElementById('search-city-select');
    const serviceSelect = document.getElementById('search-service-select');
    const keywordInput = document.getElementById('search-keyword-input');

    const city = cityInput ? cityInput.value : this.selectedCity;
    const serviceId = serviceSelect && serviceSelect.value ? serviceSelect.value : null;
    const query = keywordInput ? keywordInput.value.trim() : '';

    let endpoint = `/providers/search?city=${encodeURIComponent(city)}`;
    if (this.selectedCategoryId) endpoint += `&categoryId=${this.selectedCategoryId}`;
    if (serviceId) endpoint += `&serviceId=${serviceId}`;

    const grid = document.getElementById('providers-grid');
    if (grid) grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: #94a3b8;">Searching top verified providers...</div>';

    try {
      let results = await window.Api.get(endpoint);

      if (query) {
        const q = query.toLowerCase();
        results = results.filter(p => 
          p.fullName.toLowerCase().includes(q) || 
          p.bio.toLowerCase().includes(q) ||
          p.services.some(s => s.serviceName.toLowerCase().includes(q))
        );
      }

      this.providers = results;
      this.renderProviderCards();
    } catch (e) {
      if (grid) grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; color: #f43f5e;">Failed to load providers. Please try again.</div>';
    }
  },

  renderProviderCards() {
    const grid = document.getElementById('providers-grid');
    if (!grid) return;

    if (this.providers.length === 0) {
      grid.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 4rem 1rem;">
          <div style="font-size: 3rem; margin-bottom: 1rem;">🔍</div>
          <h3 style="font-size: 1.3rem; margin-bottom: 0.5rem;">No verified professionals found</h3>
          <p style="color: var(--text-muted);">Try adjusting your city filter or service category.</p>
        </div>
      `;
      return;
    }

    grid.innerHTML = this.providers.map(p => {
      const initials = (p.fullName || 'FixMate Pro').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
      const services = Array.isArray(p.services) ? p.services : [];
      const minPrice = services.length > 0 
        ? Math.min(...services.map(s => Number(s.customPrice) || 299))
        : 299;

      const servicesTags = services.length > 0 
        ? services.slice(0, 3).map(s => `<span class="service-tag">${s.serviceName}</span>`).join('')
        : '<span class="service-tag">Doorstep Inspection & Repair</span>';

      const rating = typeof p.ratingAvg === 'number' ? p.ratingAvg.toFixed(1) : '4.8';
      const ratingCount = p.ratingCount || 50;
      const exp = p.experienceYears || 5;

      return `
        <div class="provider-card">
          <div>
            <div class="card-header">
              <div class="provider-avatar">${initials}</div>
              <div class="provider-info">
                <div class="provider-name">
                  <span>${p.fullName}</span>
                  <svg class="verified-icon" viewBox="0 0 20 20" fill="currentColor" title="Verified Professional">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
                <div class="provider-meta">
                  <span class="rating-badge">★ ${rating} (${ratingCount})</span>
                  <span>•</span>
                  <span>${exp}+ yrs exp</span>
                  <span>•</span>
                  <span class="distance-badge">📍 ${p.city || 'Ujjain'}</span>
                </div>
              </div>
            </div>

            <p class="provider-bio">${p.bio || 'Experienced local technician offering guaranteed repair and doorstep installation.'}</p>
            
            <div class="services-tag-list">
              ${servicesTags}
              ${services.length > 3 ? `<span class="service-tag">+${services.length - 3} more</span>` : ''}
            </div>
          </div>

          <div class="card-footer">
            <div class="pricing-preview">
              <span class="price-label">Starts From</span>
              <span class="price-val">₹${minPrice}</span>
            </div>
            <button class="btn btn-primary btn-sm" onclick="BookingWizard.startBooking(${p.providerId})">
              Book Service
            </button>
          </div>
        </div>
      `;
    }).join('');
  }
};

window.Marketplace = Marketplace;
