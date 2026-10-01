/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Tracking System
 * Central API Client Helper
 */

const API_BASE = (window.location.port === '8080') 
  ? '/api' 
  : 'http://localhost:8080/api';

const API = {
  // Authentication
  async login(username, password) {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    return res.json();
  },

  // Dashboard & Analytics
  async getDashboardStats() {
    const res = await fetch(`${API_BASE}/dashboard/stats`);
    return res.json();
  },

  async getZoneSummaries() {
    const res = await fetch(`${API_BASE}/dashboard/zone-summary`);
    return res.json();
  },

  async getDailyTrends() {
    const res = await fetch(`${API_BASE}/dashboard/daily-trends`);
    return res.json();
  },

  async getTopViolations() {
    const res = await fetch(`${API_BASE}/dashboard/top-violations`);
    return res.json();
  },

  // Noise Records
  async getRecords(filters = {}) {
    const params = new URLSearchParams();
    if (filters.search) params.append('search', filters.search);
    if (filters.zoneType) params.append('zoneType', filters.zoneType);
    if (filters.complianceStatus) params.append('complianceStatus', filters.complianceStatus);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const query = params.toString() ? `?${params.toString()}` : '';
    const res = await fetch(`${API_BASE}/noise-records${query}`);
    return res.json();
  },

  async getRecentRecords() {
    const res = await fetch(`${API_BASE}/noise-records/recent`);
    return res.json();
  },

  async getRecordById(id) {
    const res = await fetch(`${API_BASE}/noise-records/${id}`);
    return res.json();
  },

  async createRecord(recordData) {
    const res = await fetch(`${API_BASE}/noise-records`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(recordData)
    });
    return res.json();
  },

  async updateRecord(id, recordData) {
    const res = await fetch(`${API_BASE}/noise-records/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(recordData)
    });
    return res.json();
  },

  async deleteRecord(id) {
    const res = await fetch(`${API_BASE}/noise-records/${id}`, {
      method: 'DELETE'
    });
    return res.json();
  },

  // Spatial / Locations
  async getLocations() {
    const res = await fetch(`${API_BASE}/locations`);
    return res.json();
  },

  async getSpatialLocations() {
    const res = await fetch(`${API_BASE}/locations/spatial`);
    return res.json();
  },

  // Zone Limits
  async getZoneLimits() {
    const res = await fetch(`${API_BASE}/zone-limits`);
    return res.json();
  },

  async updateZoneLimit(id, limitData) {
    const res = await fetch(`${API_BASE}/zone-limits/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(limitData)
    });
    return res.json();
  },

  // CSV Import
  async importCsv(file) {
    const formData = new FormData();
    formData.append('file', file);

    const res = await fetch(`${API_BASE}/csv/import`, {
      method: 'POST',
      body: formData
    });
    return res.json();
  }
};
