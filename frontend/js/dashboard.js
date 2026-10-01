/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Dashboard
 * Dashboard Logic & Data Visualizations
 */

let miniMapInstance = null;
let donutChartInstance = null;
let zoneBarChartInstance = null;
let trendChartInstance = null;

document.addEventListener('DOMContentLoaded', () => {
  loadDashboardData();

  document.getElementById('refreshDashboardBtn')?.addEventListener('click', () => {
    loadDashboardData();
  });
});

async function loadDashboardData() {
  try {
    await Promise.all([
      fetchStats(),
      initMiniMap(),
      renderCharts(),
      fetchRecentRecords()
    ]);
  } catch (err) {
    console.error('Error loading dashboard:', err);
  }
}

async function fetchStats() {
  const resp = await API.getDashboardStats();
  if (resp.success && resp.data) {
    const s = resp.data;
    document.getElementById('kpiTotalLocations').textContent = s.totalLocations;
    document.getElementById('kpiTotalRecords').textContent = s.totalRecords;
    document.getElementById('kpiSafeLocations').textContent = s.safeLocations;
    document.getElementById('kpiNonCompliantLocations').textContent = s.nonCompliantLocations;
    document.getElementById('kpiAverageNoise').textContent = `${s.averageNoiseLevel} dB`;
    document.getElementById('kpiHighestNoise').textContent = `${s.highestNoiseLevel} dB`;
    document.getElementById('kpiHighestLoc').textContent = s.highestNoiseLocation || 'No data';
    document.getElementById('donutPercentText').textContent = `${s.complianceRatePercent}%`;
  }
}

async function initMiniMap() {
  const mapEl = document.getElementById('miniMap');
  if (!mapEl) return;

  if (miniMapInstance) {
    miniMapInstance.remove();
  }

  // Initialize Leaflet Map (Bangalore Smart City center coordinates default)
  miniMapInstance = L.map('miniMap', {
    zoomControl: false,
    attributionControl: false
  }).setView([12.9716, 77.5946], 11);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 18
  }).addTo(miniMapInstance);

  // Fetch spatial locations and drop pins
  const locResp = await API.getSpatialLocations();
  if (locResp.success && locResp.data && locResp.data.length > 0) {
    const bounds = [];
    locResp.data.forEach(loc => {
      const isCompliant = (loc.latestComplianceStatus || '').toUpperCase() === 'SAFE';
      const color = isCompliant ? '#10b981' : '#f43f5e';

      const circle = L.circleMarker([loc.latitude, loc.longitude], {
        radius: 8,
        fillColor: color,
        color: '#ffffff',
        weight: 2,
        opacity: 1,
        fillOpacity: 0.85
      }).addTo(miniMapInstance);

      circle.bindPopup(`
        <div style="font-family:'Outfit', sans-serif; font-size:12px;">
          <strong style="color:#38bdf8;">${loc.name}</strong><br/>
          <span style="color:#cbd5e1;">Zone: ${loc.zoneType}</span><br/>
          <span style="font-size:13px; font-weight:bold; color:${color};">
            ${loc.latestNoiseLevel} dB (${loc.latestComplianceStatus})
          </span>
        </div>
      `);

      bounds.push([loc.latitude, loc.longitude]);
    });

    if (bounds.length > 0) {
      miniMapInstance.fitBounds(bounds, { padding: [20, 20] });
    }
  }
}

async function renderCharts() {
  // 1. Compliance Donut Chart
  const statsResp = await API.getDashboardStats();
  if (statsResp.success && statsResp.data) {
    const s = statsResp.data;
    const ctxDonut = document.getElementById('complianceDonutChart')?.getContext('2d');
    if (ctxDonut) {
      if (donutChartInstance) donutChartInstance.destroy();

      const safeCount = s.safeLocations;
      const violationCount = s.nonCompliantLocations;

      donutChartInstance = new Chart(ctxDonut, {
        type: 'doughnut',
        data: {
          labels: ['Compliant', 'Non-Compliant'],
          datasets: [{
            data: [safeCount, violationCount],
            backgroundColor: ['#10b981', '#f43f5e'],
            borderColor: '#111827',
            borderWidth: 3,
            hoverOffset: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '72%',
          plugins: {
            legend: {
              position: 'bottom',
              labels: { color: '#94a3b8', font: { size: 11 } }
            },
            tooltip: {
              callbacks: {
                label: (ctx) => ` ${ctx.label}: ${ctx.raw} locations`
              }
            }
          }
        }
      });
    }
  }

  // 2. Zone Bar Chart
  const zoneResp = await API.getZoneSummaries();
  if (zoneResp.success && zoneResp.data) {
    const zones = zoneResp.data;
    const ctxBar = document.getElementById('zoneBarChart')?.getContext('2d');
    if (ctxBar) {
      if (zoneBarChartInstance) zoneBarChartInstance.destroy();

      const labels = zones.map(z => z.zoneType);
      const avgValues = zones.map(z => z.averageNoise);
      const limits = zones.map(z => z.dayLimit);

      zoneBarChartInstance = new Chart(ctxBar, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: 'Average Recorded dB',
              data: avgValues,
              backgroundColor: 'rgba(99, 102, 241, 0.85)',
              borderRadius: 6
            },
            {
              label: 'Permitted Day Limit dB',
              data: limits,
              backgroundColor: 'rgba(245, 158, 11, 0.6)',
              borderRadius: 6
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              grid: { color: 'rgba(255,255,255,0.06)' },
              ticks: { color: '#94a3b8' }
            },
            x: {
              grid: { display: false },
              ticks: { color: '#94a3b8', font: { size: 10 } }
            }
          },
          plugins: {
            legend: {
              position: 'top',
              labels: { color: '#94a3b8', boxWidth: 12, font: { size: 11 } }
            }
          }
        }
      });
    }
  }

  // 3. Daily Trendline Chart
  const trendResp = await API.getDailyTrends();
  if (trendResp.success && trendResp.data) {
    const trends = trendResp.data;
    const ctxLine = document.getElementById('dailyTrendChart')?.getContext('2d');
    if (ctxLine) {
      if (trendChartInstance) trendChartInstance.destroy();

      const labels = trends.map(t => t.date);
      const avgData = trends.map(t => t.averageNoise);
      const maxData = trends.map(t => t.maxNoise);

      trendChartInstance = new Chart(ctxLine, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [
            {
              label: 'Daily Mean dB',
              data: avgData,
              borderColor: '#38bdf8',
              backgroundColor: 'rgba(56, 189, 248, 0.1)',
              tension: 0.35,
              fill: true,
              pointRadius: 4
            },
            {
              label: 'Peak Spike dB',
              data: maxData,
              borderColor: '#f43f5e',
              backgroundColor: 'transparent',
              borderDash: [5, 5],
              tension: 0.35,
              pointRadius: 3
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              min: 30,
              max: 100,
              grid: { color: 'rgba(255,255,255,0.06)' },
              ticks: { color: '#94a3b8' }
            },
            x: {
              grid: { display: false },
              ticks: { color: '#94a3b8', font: { size: 10 } }
            }
          },
          plugins: {
            legend: {
              position: 'top',
              labels: { color: '#94a3b8', boxWidth: 12, font: { size: 11 } }
            }
          }
        }
      });
    }
  }
}

async function fetchRecentRecords() {
  const tbody = document.getElementById('recentRecordsTableBody');
  if (!tbody) return;

  const resp = await API.getRecentRecords();
  if (resp.success && resp.data && resp.data.length > 0) {
    tbody.innerHTML = '';
    resp.data.slice(0, 6).forEach(r => {
      const isSafe = (r.complianceStatus || '').toUpperCase() === 'SAFE';
      const badgeClass = isSafe ? 'safe' : 'non-compliant';
      const statusIcon = isSafe ? 'bi-shield-check' : 'bi-exclamation-triangle';

      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>
          <div class="fw-semibold text-light">${r.locationName}</div>
          <div class="text-dim small">${r.description ? r.description.substring(0, 40) + '...' : 'Monitoring point'}</div>
        </td>
        <td><span class="badge-zone">${r.zoneType}</span></td>
        <td>
          <span class="fw-bold ${isSafe ? 'text-safe' : 'text-danger'}">${r.noiseLevel} dB</span>
          <span class="text-dim small d-block">Limit: ${r.limitApplied} dB</span>
        </td>
        <td class="text-muted small">
          ${r.recordDate} <br/>
          <span class="text-dim">${r.recordTime}</span>
        </td>
        <td>
          <span class="badge-status ${badgeClass}">
            <i class="bi ${statusIcon}"></i> ${r.complianceStatus}
          </span>
        </td>
        <td>
          <a href="records.html?search=${encodeURIComponent(r.locationName)}" class="btn btn-sm btn-cyber-secondary py-1 px-2 text-decoration-none">
            <i class="bi bi-eye"></i>
          </a>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } else {
    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No monitoring records found.</td></tr>';
  }
}
