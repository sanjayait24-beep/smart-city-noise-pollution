/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Dashboard
 * Spatial Map Visualization Logic
 */

let mapInstance = null;
let markersMap = {};
let allSpatialLocations = [];
let currentFilter = 'ALL';

document.addEventListener('DOMContentLoaded', async () => {
  initMap();
  await loadSpatialData();
  setupFilterControls();
});

function initMap() {
  // Center on Bangalore Smart City coordinates default
  mapInstance = L.map('mainMap', {
    zoomControl: true
  }).setView([12.9716, 77.5946], 12);

  // OpenStreetMap Tile Layer
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap contributors | Smart City Noise Spatial System'
  }).addTo(mapInstance);

  document.getElementById('recenterBtn')?.addEventListener('click', () => {
    recenterMap();
  });
}

async function loadSpatialData() {
  try {
    const resp = await API.getSpatialLocations();
    if (resp.success && resp.data) {
      allSpatialLocations = resp.data;
      updateStats();
      renderMarkers();
      renderLocationList();
      recenterMap();
    }
  } catch (err) {
    console.error('Failed to load spatial location data:', err);
  }
}

function updateStats() {
  let safe = 0;
  let nonCompliant = 0;
  allSpatialLocations.forEach(loc => {
    if ((loc.latestComplianceStatus || '').toUpperCase() === 'SAFE') {
      safe++;
    } else {
      nonCompliant++;
    }
  });

  document.getElementById('mapSafeCount').textContent = safe;
  document.getElementById('mapViolationCount').textContent = nonCompliant;
}

function renderMarkers() {
  // Clear existing markers
  Object.values(markersMap).forEach(m => mapInstance.removeLayer(m));
  markersMap = {};

  const filtered = getFilteredLocations();

  filtered.forEach(loc => {
    const isSafe = (loc.latestComplianceStatus || '').toUpperCase() === 'SAFE';
    const markerClass = isSafe ? 'pulse-marker safe' : 'pulse-marker danger';
    const color = isSafe ? '#10b981' : '#f43f5e';

    const customIcon = L.divIcon({
      className: 'custom-div-icon',
      html: `<div class="${markerClass}"></div>`,
      iconSize: [22, 22],
      iconAnchor: [11, 11]
    });

    const marker = L.marker([loc.latitude, loc.longitude], { icon: customIcon }).addTo(mapInstance);

    const popupHtml = `
      <div style="font-family:'Outfit', sans-serif; min-width: 220px; padding: 4px;">
        <div style="font-size: 15px; font-weight: 700; color: #ffffff; margin-bottom: 2px;">
          ${loc.name}
        </div>
        <div style="font-size: 11px; color: #94a3b8; margin-bottom: 8px;">
          <i class="bi bi-geo-alt"></i> ${loc.address || 'Smart City Zone'}
        </div>
        
        <div style="background: rgba(0,0,0,0.4); padding: 8px; border-radius: 8px; margin-bottom: 8px; border: 1px solid rgba(255,255,255,0.08);">
          <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
            <span style="color: #94a3b8; font-size: 12px;">Zone Category:</span>
            <span style="color: #38bdf8; font-weight: 600; font-size: 12px;">${loc.zoneType}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
            <span style="color: #94a3b8; font-size: 12px;">Noise Level:</span>
            <span style="color: ${color}; font-weight: 800; font-size: 14px;">${loc.latestNoiseLevel} dB</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
            <span style="color: #94a3b8; font-size: 12px;">Statutory Limit:</span>
            <span style="color: #cbd5e1; font-size: 12px;">${loc.limitApplied} dB</span>
          </div>
          <div style="display: flex; justify-content: space-between;">
            <span style="color: #94a3b8; font-size: 12px;">Compliance:</span>
            <span style="color: ${color}; font-weight: 700; font-size: 12px;">${loc.latestComplianceStatus}</span>
          </div>
        </div>

        <div style="font-size: 11px; color: #64748b; margin-bottom: 6px;">
          Last logged: ${loc.lastRecordedDate} ${loc.lastRecordedTime}
        </div>

        <div style="text-align: center; border-top: 1px solid rgba(255,255,255,0.08); padding-top: 6px;">
          <a href="add-noise.html" style="color: #38bdf8; text-decoration: none; font-size: 12px; font-weight: 600;">
            <i class="bi bi-plus-circle"></i> Log New Reading Here
          </a>
        </div>
      </div>
    `;

    marker.bindPopup(popupHtml);
    markersMap[loc.id] = marker;
  });
}

function renderLocationList() {
  const container = document.getElementById('locationListContainer');
  const filtered = getFilteredLocations();

  if (filtered.length === 0) {
    container.innerHTML = '<div class="text-center py-4 text-muted small">No locations match filter.</div>';
    return;
  }

  container.innerHTML = '';
  filtered.forEach(loc => {
    const isSafe = (loc.latestComplianceStatus || '').toUpperCase() === 'SAFE';
    const statusClass = isSafe ? 'safe' : 'non-compliant';
    const badgeColor = isSafe ? 'bg-success' : 'bg-danger';

    const item = document.createElement('div');
    item.className = 'location-list-item';
    item.dataset.id = loc.id;
    item.innerHTML = `
      <div class="d-flex align-items-center justify-content-between mb-1">
        <strong class="text-light text-truncate" style="max-width: 170px;">${loc.name}</strong>
        <span class="badge ${badgeColor}" style="font-size: 0.68rem;">${loc.latestComplianceStatus}</span>
      </div>
      <div class="d-flex align-items-center justify-content-between small text-muted">
        <span>${loc.zoneType}</span>
        <span class="fw-bold ${isSafe ? 'text-safe' : 'text-danger'}">${loc.latestNoiseLevel} dB</span>
      </div>
      <div class="text-dim" style="font-size: 0.72rem;">
        Coords: ${loc.latitude.toFixed(4)}, ${loc.longitude.toFixed(4)}
      </div>
    `;

    item.addEventListener('click', () => {
      focusLocation(loc.id, loc.latitude, loc.longitude);
      document.querySelectorAll('.location-list-item').forEach(el => el.classList.remove('active'));
      item.classList.add('active');
    });

    container.appendChild(item);
  });
}

function focusLocation(id, lat, lng) {
  mapInstance.flyTo([lat, lng], 15, { duration: 1.2 });
  const marker = markersMap[id];
  if (marker) {
    setTimeout(() => marker.openPopup(), 1300);
  }
}

function recenterMap() {
  const filtered = getFilteredLocations();
  if (filtered.length === 0) return;

  const bounds = filtered.map(l => [l.latitude, l.longitude]);
  mapInstance.fitBounds(bounds, { padding: [40, 40] });
}

function getFilteredLocations() {
  const searchTerm = (document.getElementById('mapSearchInput')?.value || '').toLowerCase();

  return allSpatialLocations.filter(loc => {
    // Search keyword
    const matchesSearch = loc.name.toLowerCase().includes(searchTerm) || 
                          (loc.address || '').toLowerCase().includes(searchTerm);
    if (!matchesSearch) return false;

    // Zone & violation filter
    if (currentFilter === 'ALL') return true;
    if (currentFilter === 'VIOLATIONS') {
      return (loc.latestComplianceStatus || '').toUpperCase() !== 'SAFE';
    }
    return loc.zoneType.toLowerCase() === currentFilter.toLowerCase();
  });
}

function setupFilterControls() {
  const buttons = document.querySelectorAll('[data-zone]');
  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      buttons.forEach(b => {
        b.classList.remove('btn-cyber-primary', 'active');
        b.classList.add('btn-cyber-secondary');
      });
      btn.classList.remove('btn-cyber-secondary');
      btn.classList.add('btn-cyber-primary', 'active');

      currentFilter = btn.dataset.zone;
      renderMarkers();
      renderLocationList();
    });
  });

  const searchInput = document.getElementById('mapSearchInput');
  searchInput?.addEventListener('input', () => {
    renderMarkers();
    renderLocationList();
  });
}
