// FixMate - High-Fidelity Standalone Demo Dataset & Offline Fallback Engine
const MockData = {
  categories: [
    { categoryId: 1, name: 'Electrical Works', slug: 'electrical', description: 'Wiring, switchboards, MCBs, inverter installation & fan repairs', isActive: true },
    { categoryId: 2, name: 'AC & Cooling', slug: 'ac-cooling', description: 'Split & window AC servicing, gas charging, jet wash & cooling repair', isActive: true },
    { categoryId: 3, name: 'Plumbing Solutions', slug: 'plumbing', description: 'Pipe leakage, tap repair, bathroom fittings, water tank & motor fixes', isActive: true },
    { categoryId: 4, name: 'Deep Home Cleaning', slug: 'cleaning', description: 'Full home deep clean, bathroom scrubbing, kitchen de-greasing & sofa shampoo', isActive: true },
    { categoryId: 5, name: 'Carpentry & Furniture', slug: 'carpentry', description: 'Door locks, bed repair, modular kitchen hinges, wooden furniture & assembly', isActive: true },
    { categoryId: 6, name: 'Appliance Repair', slug: 'appliance-repair', description: 'Washing machine, refrigerator, microwave oven & geyser repair', isActive: true },
    { categoryId: 7, name: 'Computer & Laptop Care', slug: 'computer-repair', description: 'Hardware diagnostics, OS formatting, screen replacement & RAM upgrade', isActive: true },
    { categoryId: 8, name: 'Mobile Repair', slug: 'mobile-repair', description: 'Doorstep screen replacement, battery drain fix, charging port repair', isActive: true },
    { categoryId: 9, name: 'Painting & Waterproofing', slug: 'painting', description: 'Interior wall emulsion, putty smoothing, damp treatment & texture art', isActive: true },
    { categoryId: 10, name: 'Pest Control', slug: 'pest-control', description: 'Termite protection, herbal cockroach gel, bed bug & mosquito fogging', isActive: true }
  ],

  services: [
    { serviceId: 1, categoryId: 1, serviceName: 'Ceiling Fan Installation / Repair', basePrice: 199.00 },
    { serviceId: 2, categoryId: 1, serviceName: 'Switchboard / Socket Repair', basePrice: 149.00 },
    { serviceId: 3, categoryId: 1, serviceName: 'MCB Tripping & Short Circuit Diagnostic', basePrice: 349.00 },
    { serviceId: 4, categoryId: 2, serviceName: 'AC Foam Jet Deep Cleaning', basePrice: 499.00 },
    { serviceId: 5, categoryId: 2, serviceName: 'AC Gas Leakage Check & Top-up', basePrice: 1899.00 },
    { serviceId: 6, categoryId: 2, serviceName: 'Split AC Dismantling & Re-Installation', basePrice: 1199.00 },
    { serviceId: 7, categoryId: 3, serviceName: 'Tap / Mixer Faucet Repair & Replacement', basePrice: 149.00 },
    { serviceId: 8, categoryId: 3, serviceName: 'Concealed Pipe Leakage Detection', basePrice: 449.00 },
    { serviceId: 9, categoryId: 3, serviceName: 'Toilet Commode / Cistern Installation', basePrice: 599.00 },
    { serviceId: 10, categoryId: 4, serviceName: 'Complete 2BHK / 3BHK Deep Home Clean', basePrice: 2499.00 },
    { serviceId: 11, categoryId: 4, serviceName: 'Bathroom Acid Scrubbing & Disinfection', basePrice: 499.00 },
    { serviceId: 12, categoryId: 5, serviceName: 'Door Lock / Handle Fitting & Repair', basePrice: 249.00 },
    { serviceId: 13, categoryId: 6, serviceName: 'Automatic Washing Machine Drum Repair', basePrice: 499.00 },
    { serviceId: 14, categoryId: 6, serviceName: 'Refrigerator Compressor & Cooling Repair', basePrice: 599.00 }
  ],

  slots: [
    { slotId: 1, slotName: 'Morning Shift (09:00 - 11:00)', startTime: '09:00:00', endTime: '11:00:00' },
    { slotId: 2, slotName: 'Midday Shift (11:30 - 13:30)', startTime: '11:30:00', endTime: '13:30:00' },
    { slotId: 3, slotName: 'Afternoon Shift (14:00 - 16:00)', startTime: '14:00:00', endTime: '16:00:00' },
    { slotId: 4, slotName: 'Evening Shift (16:30 - 18:30)', startTime: '16:30:00', endTime: '18:30:00' }
  ],

  providers: [
    {
      providerId: 1,
      userId: 22,
      fullName: 'Demo Provider (Master Electrician & AC)',
      email: 'provider123@fixmate.in',
      bio: 'Govt certified master technician with 8+ years experience. Expert in residential wiring, short circuits, switchboards, and split AC foam jet servicing.',
      experienceYears: 8,
      city: 'Ujjain',
      state: 'Madhya Pradesh',
      pincode: '456001',
      ratingAvg: 4.9,
      ratingCount: 142,
      totalCompletedJobs: 184,
      walletBalance: 4250.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 1, serviceName: 'Ceiling Fan Installation / Repair', categoryName: 'Electrical Works', customPrice: 199.00 },
        { serviceId: 2, serviceName: 'Switchboard / Socket Repair', categoryName: 'Electrical Works', customPrice: 149.00 },
        { serviceId: 3, serviceName: 'MCB Tripping & Short Circuit Diagnostic', categoryName: 'Electrical Works', customPrice: 349.00 },
        { serviceId: 4, serviceName: 'AC Foam Jet Deep Cleaning', categoryName: 'AC & Cooling', customPrice: 499.00 }
      ]
    },
    {
      providerId: 2,
      userId: 23,
      fullName: 'Amit Verma (Cooling Solutions)',
      email: 'amit.cooling@fixmate.in',
      bio: 'Certified HVAC & refrigeration technician. Over 6 years repairing all major brands including Daikin, Voltas, LG, and Hitachi.',
      experienceYears: 6,
      city: 'Ujjain',
      state: 'Madhya Pradesh',
      pincode: '456010',
      ratingAvg: 4.8,
      ratingCount: 98,
      totalCompletedJobs: 130,
      walletBalance: 2800.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 4, serviceName: 'AC Foam Jet Deep Cleaning', categoryName: 'AC & Cooling', customPrice: 499.00 },
        { serviceId: 5, serviceName: 'AC Gas Leakage Check & Top-up', categoryName: 'AC & Cooling', customPrice: 1849.00 },
        { serviceId: 6, serviceName: 'Split AC Dismantling & Re-Installation', categoryName: 'AC & Cooling', customPrice: 1150.00 }
      ]
    },
    {
      providerId: 3,
      userId: 24,
      fullName: 'Dinesh Malviya (Expert Plumbing)',
      email: 'dinesh.plumbing@fixmate.in',
      bio: '10 years solving complex pipe leakages, bathroom fittings, motor installation and drainage blockage with modern diagnostic tools.',
      experienceYears: 10,
      city: 'Ujjain',
      state: 'Madhya Pradesh',
      pincode: '456006',
      ratingAvg: 4.7,
      ratingCount: 215,
      totalCompletedJobs: 260,
      walletBalance: 3100.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 7, serviceName: 'Tap / Mixer Faucet Repair & Replacement', categoryName: 'Plumbing Solutions', customPrice: 149.00 },
        { serviceId: 8, serviceName: 'Concealed Pipe Leakage Detection', categoryName: 'Plumbing Solutions', customPrice: 449.00 },
        { serviceId: 9, serviceName: 'Toilet Commode / Cistern Installation', categoryName: 'Plumbing Solutions', customPrice: 599.00 }
      ]
    },
    {
      providerId: 4,
      userId: 25,
      fullName: 'Sunil Carpenter (Wooden Craft)',
      email: 'sunil.carpenter@fixmate.in',
      bio: 'Master carpenter specializing in modular kitchen hinges, door locks, wardrobe repairs, and custom wooden furniture fixing.',
      experienceYears: 5,
      city: 'Ujjain',
      state: 'Madhya Pradesh',
      pincode: '456001',
      ratingAvg: 4.9,
      ratingCount: 78,
      totalCompletedJobs: 92,
      walletBalance: 1950.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 12, serviceName: 'Door Lock / Handle Fitting & Repair', categoryName: 'Carpentry & Furniture', customPrice: 249.00 }
      ]
    },
    {
      providerId: 5,
      userId: 26,
      fullName: 'Santosh Yadav (Spotless Clean)',
      email: 'santosh.cleaning@fixmate.in',
      bio: 'Professional deep cleaning team equipped with high-pressure machines, single-disc scrubbers, and biodegradable chemicals.',
      experienceYears: 4,
      city: 'Ujjain',
      state: 'Madhya Pradesh',
      pincode: '456010',
      ratingAvg: 4.6,
      ratingCount: 110,
      totalCompletedJobs: 145,
      walletBalance: 5100.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 10, serviceName: 'Complete 2BHK / 3BHK Deep Home Clean', categoryName: 'Deep Home Cleaning', customPrice: 2399.00 },
        { serviceId: 11, serviceName: 'Bathroom Acid Scrubbing & Disinfection', categoryName: 'Deep Home Cleaning', customPrice: 479.00 }
      ]
    },
    {
      providerId: 6,
      userId: 27,
      fullName: 'Mukesh Prajapati (Appliance Doctor)',
      email: 'mukesh.appliances@fixmate.in',
      bio: 'Specialist in inverter refrigerators, front/top load washing machines, and microwave magnetron replacements.',
      experienceYears: 7,
      city: 'Ujjain',
      state: 'Madhya Pradesh',
      pincode: '456006',
      ratingAvg: 4.8,
      ratingCount: 134,
      totalCompletedJobs: 172,
      walletBalance: 3450.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 13, serviceName: 'Automatic Washing Machine Drum Repair', categoryName: 'Appliance Repair', customPrice: 499.00 },
        { serviceId: 14, serviceName: 'Refrigerator Compressor & Cooling Repair', categoryName: 'Appliance Repair', customPrice: 599.00 }
      ]
    },
    {
      providerId: 7,
      userId: 29,
      fullName: 'Rahul Rathore (Indore Power Fix)',
      email: 'rahul.power@fixmate.in',
      bio: 'Commercial and residential certified electrician covering Vijay Nagar, Palasia, and AB Road in Indore.',
      experienceYears: 9,
      city: 'Indore',
      state: 'Madhya Pradesh',
      pincode: '452010',
      ratingAvg: 4.9,
      ratingCount: 180,
      totalCompletedJobs: 220,
      walletBalance: 6100.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 1, serviceName: 'Ceiling Fan Installation / Repair', categoryName: 'Electrical Works', customPrice: 219.00 },
        { serviceId: 2, serviceName: 'Switchboard / Socket Repair', categoryName: 'Electrical Works', customPrice: 159.00 },
        { serviceId: 3, serviceName: 'MCB Tripping & Short Circuit Diagnostic', categoryName: 'Electrical Works', customPrice: 389.00 }
      ]
    },
    {
      providerId: 8,
      userId: 30,
      fullName: 'Deepak Khandelwal (Indore Cooling Hub)',
      email: 'deepak.cooling@fixmate.in',
      bio: 'Expert in multi-split air conditioning systems, cassette units, and high-pressure chemical wash in Indore.',
      experienceYears: 7,
      city: 'Indore',
      state: 'Madhya Pradesh',
      pincode: '452001',
      ratingAvg: 4.8,
      ratingCount: 160,
      totalCompletedJobs: 195,
      walletBalance: 4800.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 4, serviceName: 'AC Foam Jet Deep Cleaning', categoryName: 'AC & Cooling', customPrice: 529.00 },
        { serviceId: 5, serviceName: 'AC Gas Leakage Check & Top-up', categoryName: 'AC & Cooling', customPrice: 1899.00 }
      ]
    },
    {
      providerId: 9,
      userId: 34,
      fullName: 'Vikram Joshi (Bhopal City Electrician)',
      email: 'vikram.bhopal@fixmate.in',
      bio: 'Serving MP Nagar, Arera Colony and Kolar Road Bhopal for 11 years. Guaranteed electrical diagnostics.',
      experienceYears: 11,
      city: 'Bhopal',
      state: 'Madhya Pradesh',
      pincode: '462011',
      ratingAvg: 4.9,
      ratingCount: 210,
      totalCompletedJobs: 255,
      walletBalance: 7200.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 1, serviceName: 'Ceiling Fan Installation / Repair', categoryName: 'Electrical Works', customPrice: 199.00 },
        { serviceId: 3, serviceName: 'MCB Tripping & Short Circuit Diagnostic', categoryName: 'Electrical Works', customPrice: 349.00 }
      ]
    },
    {
      providerId: 10,
      userId: 37,
      fullName: 'Hemant Shinde (Dewas Electrical & Solar)',
      email: 'hemant.dewas@fixmate.in',
      bio: 'Industrial and domestic wiring specialist covering Dewas city, Station Road, and industrial area.',
      experienceYears: 6,
      city: 'Dewas',
      state: 'Madhya Pradesh',
      pincode: '455001',
      ratingAvg: 4.7,
      ratingCount: 82,
      totalCompletedJobs: 104,
      walletBalance: 2400.00,
      verificationStatus: 'ACTIVE',
      services: [
        { serviceId: 1, serviceName: 'Ceiling Fan Installation / Repair', categoryName: 'Electrical Works', customPrice: 189.00 },
        { serviceId: 2, serviceName: 'Switchboard / Socket Repair', categoryName: 'Electrical Works', customPrice: 139.00 }
      ]
    }
  ],

  customerAddresses: [
    { addressId: 1, label: 'Home', streetAddress: 'Flat 402, Sunshine Heights, Freeganj', landmark: 'Near Tower Square', city: 'Ujjain', state: 'Madhya Pradesh', pincode: '456001', isDefault: true },
    { addressId: 2, label: 'Office', streetAddress: 'Shop 12, Ground Floor, Madhav Club Road', landmark: 'Opposite State Bank', city: 'Ujjain', state: 'Madhya Pradesh', pincode: '456010', isDefault: false }
  ],

  customerBookings: [
    {
      bookingId: 101,
      bookingNumber: 'FM-20260830-49211',
      serviceName: 'AC Foam Jet Deep Cleaning',
      categoryName: 'AC & Cooling',
      providerName: 'Demo Provider (Master Electrician & AC)',
      providerPhone: '+919826022001',
      providerRating: 4.9,
      bookingDate: '2026-08-30',
      slotName: 'Midday Shift (11:30 - 13:30)',
      streetAddress: 'Flat 402, Sunshine Heights, Freeganj',
      city: 'Ujjain',
      totalAmount: 559.00,
      baseAmount: 499.00,
      bookingStatus: 'ON_THE_WAY',
      paymentStatus: 'PAID',
      problemDescription: 'Split AC throwing warm air and water dripping inside bedroom'
    },
    {
      bookingId: 102,
      bookingNumber: 'FM-20260825-38104',
      serviceName: 'Switchboard / Socket Repair',
      categoryName: 'Electrical Works',
      providerName: 'Demo Provider (Master Electrician & AC)',
      providerPhone: '+919826022001',
      providerRating: 4.9,
      bookingDate: '2026-08-25',
      slotName: 'Morning Shift (09:00 - 11:00)',
      streetAddress: 'Flat 402, Sunshine Heights, Freeganj',
      city: 'Ujjain',
      totalAmount: 167.00,
      baseAmount: 149.00,
      bookingStatus: 'COMPLETED',
      paymentStatus: 'PAID',
      reviewRating: 5,
      reviewComment: 'Arrived exactly on time with all spare switches. Very polite and cleaned up afterward!'
    }
  ],

  providerBookings: [
    {
      bookingId: 101,
      bookingNumber: 'FM-20260830-49211',
      serviceName: 'AC Foam Jet Deep Cleaning',
      customerName: 'Demo Customer',
      customerPhone: '+919826011001',
      streetAddress: 'Flat 402, Sunshine Heights, Freeganj',
      city: 'Ujjain',
      bookingDate: '2026-08-30',
      slotName: 'Midday Shift (11:30 - 13:30)',
      baseAmount: 499.00,
      bookingStatus: 'ON_THE_WAY',
      paymentStatus: 'PAID',
      problemDescription: 'Split AC throwing warm air and water dripping inside bedroom'
    },
    {
      bookingId: 103,
      bookingNumber: 'FM-20260831-77218',
      serviceName: 'MCB Tripping & Short Circuit Diagnostic',
      customerName: 'Priya Patel',
      customerPhone: '+919826011002',
      streetAddress: '24, Mahakal Marg, Rishi Nagar',
      city: 'Ujjain',
      bookingDate: '2026-08-31',
      slotName: 'Evening Shift (16:30 - 18:30)',
      baseAmount: 349.00,
      bookingStatus: 'PENDING',
      paymentStatus: 'PAID',
      problemDescription: 'Main circuit breaker trips whenever microwave or geyser is switched on'
    }
  ],

  providerLedger: [
    { createdAt: '2026-08-25 11:30', transactionType: 'CREDIT_BOOKING_PAYOUT', amount: 149.00, runningBalance: 4250.00, description: 'Earnings credited for completed job #FM-20260825-38104' },
    { createdAt: '2026-08-22 17:15', transactionType: 'CREDIT_BOOKING_PAYOUT', amount: 499.00, runningBalance: 4101.00, description: 'Earnings credited for completed job #FM-20260822-19208' },
    { createdAt: '2026-08-20 14:00', transactionType: 'DEBIT_BANK_WITHDRAWAL', amount: 2000.00, runningBalance: 3602.00, description: 'Bank transfer payout to SBI A/C ending in 8421' }
  ],

  adminComplaints: [
    {
      complaintId: 1,
      complaintNumber: 'CMP-89421-102',
      bookingNumber: 'FM-20260820-11029',
      customerName: 'Vikram Malhotra',
      customerPhone: '+919826011005',
      subject: 'Delay in technician arrival',
      description: 'Technician arrived 45 minutes past the booked time slot. Work was done well though.',
      status: 'OPEN'
    },
    {
      complaintId: 2,
      complaintNumber: 'CMP-77123-204',
      bookingNumber: 'FM-20260818-40912',
      customerName: 'Sneha Gupta',
      customerPhone: '+919826011006',
      subject: 'Incomplete pipe sealant cleanup',
      description: 'Minor cement debris left behind in bathroom washbasin.',
      status: 'RESOLVED'
    }
  ]
};

window.MockData = MockData;
