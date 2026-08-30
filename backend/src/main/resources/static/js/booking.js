// FixMate - Interactive Multi-Step Booking Wizard Module
const BookingWizard = {
  currentStep: 1,
  provider: null,
  selectedService: null,
  selectedDate: null,
  selectedSlotId: null,
  selectedAddressId: null,
  savedAddresses: [],
  createdBooking: null,

  async startBooking(providerId) {
    if (!window.Auth.isLoggedIn()) {
      window.Toast.info('Please login or register to book a service');
      window.App.openModal('login-modal');
      return;
    }

    if (window.Auth.getRole() !== 'CUSTOMER') {
      window.Toast.error('Only customers can book services. Please sign in as a customer.');
      return;
    }

    try {
      this.provider = await window.Api.get(`/providers/${providerId}`);
      await this.loadCustomerAddresses();
      this.resetWizard();
      this.renderStep1();
      window.App.openModal('booking-wizard-modal');
    } catch (e) {
      console.error('Failed to open booking wizard', e);
    }
  },

  resetWizard() {
    this.currentStep = 1;
    this.selectedService = null;
    this.selectedDate = new Date().toISOString().split('T')[0];
    this.selectedSlotId = null;
    this.selectedAddressId = this.savedAddresses.length > 0 ? this.savedAddresses[0].addressId : null;
    this.createdBooking = null;
  },

  async loadCustomerAddresses() {
    try {
      this.savedAddresses = await window.Api.get('/customer/addresses');
    } catch (e) {
      this.savedAddresses = [];
    }
  },

  renderStep1() {
    this.currentStep = 1;
    const body = document.getElementById('booking-wizard-body');
    if (!body) return;

    const providerServices = (this.provider && Array.isArray(this.provider.services) && this.provider.services.length > 0) 
      ? this.provider.services 
      : [{ serviceId: 1, serviceName: 'Standard Inspection & Repair', categoryName: 'General Maintenance', customPrice: 299.00 }];

    let servicesHtml = providerServices.map(s => `
      <label style="display: flex; align-items: center; justify-content: space-between; padding: 1rem; background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); margin-bottom: 0.75rem; cursor: pointer;">
        <div style="display: flex; align-items: center; gap: 0.75rem;">
          <input type="radio" name="wizard_service" value="${s.serviceId}" onchange="BookingWizard.onServiceSelected(${s.serviceId}, '${s.serviceName}', ${s.customPrice})" ${this.selectedService && this.selectedService.serviceId === s.serviceId ? 'checked' : ''} />
          <div>
            <div style="font-weight: 600;">${s.serviceName}</div>
            <div style="font-size: 0.8rem; color: var(--text-muted);">${s.categoryName}</div>
          </div>
        </div>
        <div style="font-weight: 700; font-size: 1.1rem; color: #fff;">₹${s.customPrice}</div>
      </label>
    `).join('');

    body.innerHTML = `
      <div style="margin-bottom: 1.5rem;">
        <h3 style="font-size: 1.3rem; margin-bottom: 0.25rem;">Step 1: Select Service</h3>
        <p style="font-size: 0.88rem; color: var(--text-muted);">Booking with <strong>${this.provider.fullName}</strong> (${this.provider.city})</p>
      </div>

      <div style="max-height: 280px; overflow-y: auto; margin-bottom: 1.5rem;">
        ${servicesHtml}
      </div>

      <div style="display: flex; justify-content: flex-end;">
        <button class="btn btn-primary" id="wizard-btn-next1" onclick="BookingWizard.goToStep2()" ${!this.selectedService ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>
          Next: Date & Slot &rarr;
        </button>
      </div>
    `;
  },

  onServiceSelected(serviceId, serviceName, price) {
    this.selectedService = { serviceId, serviceName, price };
    const btn = document.getElementById('wizard-btn-next1');
    if (btn) {
      btn.disabled = false;
      btn.style.opacity = '1';
      btn.style.cursor = 'pointer';
    }
  },

  async goToStep2() {
    this.currentStep = 2;
    const body = document.getElementById('booking-wizard-body');
    if (!body) return;

    body.innerHTML = '<div style="text-align: center; padding: 2rem;">Loading available slots...</div>';

    const slots = await window.Api.get(`/providers/${this.provider.providerId}/availability?date=${this.selectedDate}`);

    let slotsHtml = slots.length > 0 ? slots.map(sl => `
      <button type="button" class="btn btn-secondary ${this.selectedSlotId === sl.slotId ? 'btn-primary' : ''}" 
              style="padding: 0.6rem; font-size: 0.85rem;"
              onclick="BookingWizard.onSlotSelected(${sl.slotId})">
        ${sl.slotName.split('(')[0]}<br><small style="opacity: 0.7;">${sl.startTime.substring(0, 5)} - ${sl.endTime.substring(0, 5)}</small>
      </button>
    `).join('') : '<div style="grid-column: 1/-1; color: #f43f5e; padding: 1rem 0;">No slots remaining for this date. Try another date.</div>';

    body.innerHTML = `
      <div style="margin-bottom: 1.5rem;">
        <h3 style="font-size: 1.3rem; margin-bottom: 0.25rem;">Step 2: Choose Date & Time</h3>
        <p style="font-size: 0.88rem; color: var(--text-muted);">${this.selectedService.serviceName} - ₹${this.selectedService.price}</p>
      </div>

      <div class="form-group">
        <label class="form-label">Service Date</label>
        <input type="date" class="form-control" id="wizard-date-input" 
               min="${new Date().toISOString().split('T')[0]}" 
               value="${this.selectedDate}" 
               onchange="BookingWizard.onDateChanged(this.value)" />
      </div>

      <div class="form-group">
        <label class="form-label">Available Slots</label>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(130px, 1fr)); gap: 0.6rem;">
          ${slotsHtml}
        </div>
      </div>

      <div style="display: flex; justify-content: space-between; margin-top: 2rem;">
        <button class="btn btn-secondary" onclick="BookingWizard.renderStep1()">&larr; Back</button>
        <button class="btn btn-primary" id="wizard-btn-next2" onclick="BookingWizard.goToStep3()" ${!this.selectedSlotId ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>
          Next: Address &rarr;
        </button>
      </div>
    `;
  },

  async onDateChanged(newDate) {
    this.selectedDate = newDate;
    this.selectedSlotId = null;
    await this.goToStep2();
  },

  onSlotSelected(slotId) {
    this.selectedSlotId = slotId;
    this.goToStep2(); // re-render with active highlight
  },

  goToStep3() {
    this.currentStep = 3;
    const body = document.getElementById('booking-wizard-body');
    if (!body) return;

    let addressOptions = this.savedAddresses.map(a => `
      <label style="display: block; padding: 0.85rem 1rem; background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); margin-bottom: 0.6rem; cursor: pointer;">
        <div style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="radio" name="wizard_address" value="${a.addressId}" onchange="BookingWizard.selectedAddressId = ${a.addressId}" ${this.selectedAddressId === a.addressId ? 'checked' : ''} />
          <strong>${a.label}</strong>
          <span style="font-size: 0.8rem; color: var(--text-dim);">(${a.city})</span>
        </div>
        <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.3rem; margin-left: 1.5rem;">
          ${a.streetAddress}, ${a.landmark ? a.landmark + ', ' : ''}${a.city} - ${a.pincode}
        </div>
      </label>
    `).join('');

    body.innerHTML = `
      <div style="margin-bottom: 1.5rem;">
        <h3 style="font-size: 1.3rem; margin-bottom: 0.25rem;">Step 3: Service Address & Details</h3>
        <p style="font-size: 0.88rem; color: var(--text-muted);">Where should the technician arrive?</p>
      </div>

      <div class="form-group">
        <label class="form-label">Select Address</label>
        ${this.savedAddresses.length > 0 ? addressOptions : '<div style="color: #f59e0b; margin-bottom: 0.5rem;">No saved address found. Enter details below:</div>'}
      </div>

      <div style="padding: 1rem; background: rgba(0,0,0,0.2); border-radius: var(--radius-md); margin-bottom: 1rem;">
        <div style="font-size: 0.85rem; font-weight: 600; margin-bottom: 0.5rem;">Or Add Quick Address:</div>
        <input type="text" id="quick-street" class="form-control" placeholder="Enter house/flat number, building, street, area" style="margin-bottom: 0.5rem;" />
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem;">
          <input type="text" id="quick-city" class="form-control" placeholder="Enter city name" value="${this.provider.city}" />
          <input type="text" id="quick-pincode" class="form-control" placeholder="Enter 6-digit area pincode" />
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">Describe the Issue</label>
        <textarea id="wizard-problem-desc" class="form-control" rows="3" placeholder="Describe the issue or service requirements in detail"></textarea>
      </div>

      <div style="display: flex; justify-content: space-between; margin-top: 1.5rem;">
        <button class="btn btn-secondary" onclick="BookingWizard.goToStep2()">&larr; Back</button>
        <button class="btn btn-primary" onclick="BookingWizard.goToStep4()">Review Quote & Payment &rarr;</button>
      </div>
    `;
  },

  async goToStep4() {
    const desc = document.getElementById('wizard-problem-desc');
    const problemText = desc && desc.value.trim() ? desc.value.trim() : 'Doorstep service inspection';

    // If no address selected from saved list, save the quick address
    if (!this.selectedAddressId) {
      const streetInput = document.getElementById('quick-street');
      const cityInput = document.getElementById('quick-city');
      const pincodeInput = document.getElementById('quick-pincode');

      const street = streetInput ? streetInput.value.trim() : '';
      const city = cityInput ? cityInput.value.trim() : this.provider.city;
      const pincode = pincodeInput ? pincodeInput.value.trim() : '456001';

      if (!street) {
        window.Toast.error('Please enter your service street address');
        return;
      }

      const newAddrId = await window.Api.post('/customer/addresses', {
        label: 'Home',
        streetAddress: street,
        city: city,
        state: 'Madhya Pradesh',
        pincode: pincode || '456001',
        isDefault: true
      });
      this.selectedAddressId = newAddrId;
    }

    this.problemDescription = problemText;
    if (!this.selectedPaymentMethod) {
      this.selectedPaymentMethod = 'CASH'; // Default to Cash on Delivery / Pay on Arrival
    }

    this.renderStep4Quote();
  },

  setPaymentMethod(method) {
    this.selectedPaymentMethod = method;
    this.renderStep4Quote();
  },

  renderStep4Quote() {
    // Financial Calculation Breakdown
    const base = this.selectedService.price;
    const platformFee = Math.round(base * 0.10 * 100) / 100;
    const tax = Math.round(platformFee * 0.18 * 100) / 100;
    const total = Math.round((base + platformFee + tax) * 100) / 100;
    this.quoteTotal = total;

    const body = document.getElementById('booking-wizard-body');
    if (!body) return;

    const isCash = this.selectedPaymentMethod === 'CASH';
    const isUpi = this.selectedPaymentMethod === 'UPI';
    const isCard = this.selectedPaymentMethod === 'CARD';
    const isNetBanking = this.selectedPaymentMethod === 'NETBANKING';

    body.innerHTML = `
      <div style="margin-bottom: 1.25rem;">
        <h3 style="font-size: 1.3rem; margin-bottom: 0.25rem;">Step 4: Price Quote & Payment Option</h3>
        <p style="font-size: 0.88rem; color: var(--text-muted);">Transparent pricing • Zero hidden fees</p>
      </div>

      <!-- Price Breakdown Card -->
      <div style="background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 1.15rem; margin-bottom: 1.25rem;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.92rem;">
          <span style="color: var(--text-muted);">${this.selectedService.serviceName} (Service Fee)</span>
          <span>₹${base.toFixed(2)}</span>
        </div>
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.92rem;">
          <span style="color: var(--text-muted);">Platform Booking Fee (10%)</span>
          <span>₹${platformFee.toFixed(2)}</span>
        </div>
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.92rem;">
          <span style="color: var(--text-muted);">GST (18% on platform fee)</span>
          <span>₹${tax.toFixed(2)}</span>
        </div>
        <div style="height: 1px; background: var(--border-subtle); margin: 0.6rem 0;"></div>
        <div style="display: flex; justify-content: space-between; font-size: 1.25rem; font-weight: 800; color: #fff;">
          <span>Final Total</span>
          <span style="color: var(--accent-emerald);">₹${total.toFixed(2)}</span>
        </div>
      </div>

      <!-- Payment Method Selection Section -->
      <div style="margin-bottom: 1.5rem;">
        <label class="form-label" style="font-size: 0.95rem; color: #fff; font-weight: 700; margin-bottom: 0.6rem;">
          Select How You Want to Pay:
        </label>
        
        <div style="display: flex; flex-direction: column; gap: 0.65rem;">
          <!-- Option 1: Cash on Delivery / Pay on Arrival -->
          <label style="display: flex; align-items: flex-start; gap: 0.85rem; padding: 0.9rem 1rem; background: var(--bg-surface-elevated); border: 2px solid ${isCash ? 'var(--accent-emerald)' : 'var(--border-subtle)'}; border-radius: var(--radius-md); cursor: pointer;">
            <input type="radio" name="wizard_pay_mode" value="CASH" ${isCash ? 'checked' : ''} onchange="BookingWizard.setPaymentMethod('CASH')" style="margin-top: 4px;" />
            <div style="flex-grow: 1;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <strong style="font-size: 0.98rem; color: #fff;">💵 Pay on Doorstep Arrival (Cash / QR)</strong>
                <span class="status-badge status-completed" style="font-size: 0.7rem;">Recommended</span>
              </div>
              <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">
                Zero prepayment needed. Pay in cash or scan QR code after work is done.
              </p>
            </div>
          </label>

          <!-- Option 2: Instant UPI -->
          <label style="display: flex; align-items: flex-start; gap: 0.85rem; padding: 0.9rem 1rem; background: var(--bg-surface-elevated); border: 2px solid ${isUpi ? 'var(--brand-primary)' : 'var(--border-subtle)'}; border-radius: var(--radius-md); cursor: pointer;">
            <input type="radio" name="wizard_pay_mode" value="UPI" ${isUpi ? 'checked' : ''} onchange="BookingWizard.setPaymentMethod('UPI')" style="margin-top: 4px;" />
            <div style="flex-grow: 1;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <strong style="font-size: 0.98rem; color: #fff;">⚡ Instant UPI (GPay / PhonePe / Paytm / BHIM)</strong>
                <span class="status-badge status-accepted" style="font-size: 0.7rem;">Online Pay</span>
              </div>
              <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">
                Fast & secure online payment with money-back service guarantee.
              </p>
            </div>
          </label>

          <!-- Option 3: Credit / Debit Card -->
          <label style="display: flex; align-items: flex-start; gap: 0.85rem; padding: 0.9rem 1rem; background: var(--bg-surface-elevated); border: 2px solid ${isCard ? 'var(--brand-primary)' : 'var(--border-subtle)'}; border-radius: var(--radius-md); cursor: pointer;">
            <input type="radio" name="wizard_pay_mode" value="CARD" ${isCard ? 'checked' : ''} onchange="BookingWizard.setPaymentMethod('CARD')" style="margin-top: 4px;" />
            <div style="flex-grow: 1;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <strong style="font-size: 0.98rem; color: #fff;">💳 Credit / Debit Card</strong>
                <span class="status-badge status-accepted" style="font-size: 0.7rem;">Visa / RuPay</span>
              </div>
              <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">
                Pay securely using your Debit or Credit Card with 3D Secure OTP.
              </p>
            </div>
          </label>

          <!-- Option 4: Net Banking -->
          <label style="display: flex; align-items: flex-start; gap: 0.85rem; padding: 0.9rem 1rem; background: var(--bg-surface-elevated); border: 2px solid ${isNetBanking ? 'var(--brand-primary)' : 'var(--border-subtle)'}; border-radius: var(--radius-md); cursor: pointer;">
            <input type="radio" name="wizard_pay_mode" value="NETBANKING" ${isNetBanking ? 'checked' : ''} onchange="BookingWizard.setPaymentMethod('NETBANKING')" style="margin-top: 4px;" />
            <div style="flex-grow: 1;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <strong style="font-size: 0.98rem; color: #fff;">🏦 Net Banking</strong>
                <span class="status-badge status-accepted" style="font-size: 0.7rem;">50+ Banks</span>
              </div>
              <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">
                Direct bank transfer via SBI, HDFC, ICICI, Axis, PNB and other banks.
              </p>
            </div>
          </label>
        </div>
      </div>

      <div style="font-size: 0.82rem; color: var(--text-dim); margin-bottom: 1.25rem;">
        📅 <strong>${this.selectedDate}</strong> • 🛠️ Technician: <strong>${this.provider.fullName}</strong> (${this.provider.city})
      </div>

      <!-- Footer Buttons -->
      <div style="display: flex; justify-content: space-between; gap: 1rem;">
        <button class="btn btn-secondary" onclick="BookingWizard.goToStep3()">&larr; Back</button>
        ${isCash ? `
          <button class="btn btn-success" onclick="BookingWizard.processBooking('CASH')">
            💵 Confirm Booking (Pay ₹${total.toFixed(2)} on Arrival)
          </button>
        ` : `
          <button class="btn btn-primary" onclick="BookingWizard.processBooking('${this.selectedPaymentMethod}')">
            ⚡ Pay ₹${total.toFixed(2)} via ${this.selectedPaymentMethod} & Confirm &rarr;
          </button>
        `}
      </div>
    `;
  },

  async processBooking(paymentMethod) {
    const body = document.getElementById('booking-wizard-body');
    if (body) body.innerHTML = '<div style="text-align: center; padding: 2.5rem;">Reserving your slot and generating booking...</div>';

    try {
      const booking = await window.Api.post('/bookings', {
        providerId: this.provider.providerId,
        serviceId: this.selectedService.serviceId,
        addressId: this.selectedAddressId,
        bookingDate: this.selectedDate,
        slotId: this.selectedSlotId,
        paymentMethod: paymentMethod,
        problemDescription: this.problemDescription
      });

      this.createdBooking = booking;

      if (paymentMethod === 'CASH') {
        // Cash on Delivery / Pay on Arrival: Immediate confirmation
        this.renderSuccessStep('CASH');
      } else {
        // Online Payment: Render interactive payment modal
        this.renderPaymentStep(paymentMethod);
      }
    } catch (e) {
      this.goToStep4();
    }
  },

  renderPaymentStep(method) {
    const body = document.getElementById('booking-wizard-body');
    if (!body) return;

    let payInterface = '';

    if (method === 'UPI') {
      payInterface = `
        <div style="padding: 1.25rem; background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); margin-bottom: 1.5rem; text-align: center;">
          <div style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.75rem;">Scan QR code with any UPI App:</div>
          <div style="width: 140px; height: 140px; background: #fff; margin: 0 auto 0.75rem; display: flex; align-items: center; justify-content: center; border-radius: var(--radius-sm); color: #000; font-size: 2.5rem; font-weight: 800;">
            📱
          </div>
          <div style="font-size: 0.85rem; color: #a5b4fc; font-family: monospace;">UPI ID: fixmate.pay@upi</div>
        </div>
        <button class="btn btn-primary" style="width: 100%; margin-bottom: 0.75rem;" onclick="BookingWizard.executePayment('UPI')">
          ⚡ Complete 1-Click UPI Payment (₹${this.createdBooking.totalAmount})
        </button>
      `;
    } else if (method === 'CARD') {
      payInterface = `
        <div style="padding: 1.25rem; background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); margin-bottom: 1.5rem;">
          <div class="form-group">
            <label class="form-label">Card Number</label>
            <input type="text" class="form-control" placeholder="•••• •••• •••• 4242" value="4532 8921 4012 4242" />
          </div>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
            <div class="form-group">
              <label class="form-label">Valid Thru</label>
              <input type="text" class="form-control" placeholder="MM/YY" value="12/28" />
            </div>
            <div class="form-group">
              <label class="form-label">CVV</label>
              <input type="password" class="form-control" placeholder="•••" value="888" />
            </div>
          </div>
        </div>
        <button class="btn btn-primary" style="width: 100%; margin-bottom: 0.75rem;" onclick="BookingWizard.executePayment('CARD')">
          💳 Pay ₹${this.createdBooking.totalAmount} Securely
        </button>
      `;
    } else {
      payInterface = `
        <div style="padding: 1.25rem; background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); margin-bottom: 1.5rem;">
          <div class="form-group">
            <label class="form-label">Select Your Bank</label>
            <select class="form-control">
              <option>State Bank of India (SBI)</option>
              <option>HDFC Bank</option>
              <option>ICICI Bank</option>
              <option>Axis Bank</option>
              <option>Punjab National Bank</option>
            </select>
          </div>
        </div>
        <button class="btn btn-primary" style="width: 100%; margin-bottom: 0.75rem;" onclick="BookingWizard.executePayment('NETBANKING')">
          🏦 Authorize Net Banking Payment (₹${this.createdBooking.totalAmount})
        </button>
      `;
    }

    body.innerHTML = `
      <div style="margin-bottom: 1.25rem;">
        <h3 style="font-size: 1.3rem; margin-bottom: 0.25rem;">Complete Payment</h3>
        <p style="font-size: 0.88rem; color: var(--text-muted);">Booking <strong>#${this.createdBooking.bookingNumber}</strong></p>
      </div>

      <div style="padding: 1rem; background: rgba(99, 102, 241, 0.1); border: 1px solid rgba(99, 102, 241, 0.3); border-radius: var(--radius-md); margin-bottom: 1.25rem; text-align: center;">
        <div style="font-size: 0.85rem; color: #a5b4fc;">Amount to Pay</div>
        <div style="font-size: 2.2rem; font-weight: 800; color: #fff;">₹${this.createdBooking.totalAmount}</div>
        <span class="status-badge status-accepted" style="margin-top: 0.3rem;">Mode: ${method}</span>
      </div>

      ${payInterface}

      <div style="text-align: center; margin-top: 0.5rem;">
        <button class="btn btn-secondary btn-sm" onclick="BookingWizard.executePayment('CASH')">
          Or Switch to Pay on Doorstep Arrival (Cash)
        </button>
      </div>
    `;
  },

  async executePayment(method) {
    const body = document.getElementById('booking-wizard-body');
    if (body) body.innerHTML = '<div style="text-align: center; padding: 2.5rem;">Processing payment transaction...</div>';

    try {
      await window.Api.post('/payments/mock-success', {
        bookingId: this.createdBooking.bookingId,
        method: method
      });

      this.renderSuccessStep(method);
    } catch (e) {
      this.renderPaymentStep(method);
    }
  },

  renderSuccessStep(paymentMethod = 'CASH') {
    const body = document.getElementById('booking-wizard-body');
    if (!body) return;

    const isCash = paymentMethod === 'CASH';

    body.innerHTML = `
      <div style="text-align: center; padding: 1.5rem 0;">
        <div style="font-size: 3.5rem; margin-bottom: 0.75rem;">🎉</div>
        <h2 style="font-size: 1.6rem; font-weight: 700; margin-bottom: 0.4rem; color: var(--accent-emerald);">Booking Confirmed!</h2>
        <p style="color: var(--text-muted); font-size: 0.92rem; margin-bottom: 1.5rem;">
          Your service has been successfully scheduled. The technician has received your request.
        </p>

        <div style="background: var(--bg-surface-elevated); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 1.25rem; text-align: left; margin-bottom: 1.5rem;">
          <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem;">
            <span style="color: var(--text-muted);">Booking Number:</span>
            <strong>${this.createdBooking.bookingNumber}</strong>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem;">
            <span style="color: var(--text-muted);">Service:</span>
            <span>${this.createdBooking.serviceName}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem;">
            <span style="color: var(--text-muted);">Technician:</span>
            <span>${this.createdBooking.providerName}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem;">
            <span style="color: var(--text-muted);">Total Amount:</span>
            <strong>₹${this.createdBooking.totalAmount}</strong>
          </div>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 0.4rem; padding-top: 0.6rem; border-top: 1px solid var(--border-subtle);">
            <span style="color: var(--text-muted);">Payment Method:</span>
            ${isCash 
              ? '<span class="status-badge status-completed">💵 Pay on Arrival (Cash/UPI)</span>' 
              : '<span class="status-badge status-completed">✅ Paid Online (' + paymentMethod + ')</span>'}
          </div>
        </div>

        <button class="btn btn-primary" style="width: 100%;" onclick="App.closeAllModals(); App.switchView('customer-view');">
          View & Track in Customer Dashboard
        </button>
      </div>
    `;
  }
};

window.BookingWizard = BookingWizard;
