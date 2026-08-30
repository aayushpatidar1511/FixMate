# FixMate - Modern Frontend Web Application

## 1. Overview
FixMate's frontend is a responsive, glassmorphic single-page web marketplace crafted in **HTML5**, **CSS3**, and **Modern Vanilla JavaScript (ES6+)**.

It features:
- **Zero Heavy Framework Bloat**: Fast rendering, sub-millisecond DOM transitions, and zero unnecessary dependencies.
- **Glassmorphism Visual Aesthetic**: Dark luxury palette (`#090d16` background, `#1e293b` surface), glowing indigo buttons, amber badges, smooth hover depth, and backdrop-filter blur.
- **Multi-Role Portals in One SPA**:
  1. **Marketplace Discovery**: Instant search by keyword, city (Ujjain, Indore, Bhopal, Dewas), and service category with real-time provider card rendering.
  2. **Multi-Step Booking Wizard**: 5-step modal workflow (Service -> Date/Slot -> Address -> Quote Breakdown -> 1-Click Payment Simulator -> Confirmation).
  3. **Customer Dashboard**: Live progress stepper (`PENDING` -> `ACCEPTED` -> `ON_THE_WAY` -> `IN_PROGRESS` -> `COMPLETED`), past order table, 5-star review modal, and dispute resolution tickets.
  4. **Provider Workbench**: Real-time incoming job alerts with 1-click Accept/Reject, dispatch status toggles, wallet earnings, and transaction ledger.
  5. **Admin Governance Center**: KPI cards (GMV, net platform fees, orders), provider verification queue (Approve/Reject), and dispute resolution desk.
- **Demo Quick-Fill**: One-click demo credentials in login modal for Customer (`customer123`), Provider (`provider123`), and Admin (`admin123`). Password: `Password@123`.

---

## 2. Serving Locally

### Method A: Static Python Server
```bash
cd frontend
python -m http.server 3000
```
Open `http://localhost:3000` in any browser.

### Method B: Live Server / VS Code
Right-click `index.html` and select **"Open with Live Server"**.

### Method C: Spring Boot Static Resources
Copy `frontend/*` into `backend/src/main/resources/static/` to serve directly from Spring Boot on `http://localhost:8080`.
