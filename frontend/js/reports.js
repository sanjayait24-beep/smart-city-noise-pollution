/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Dashboard
 * Reports & Compliance Audit Controller
 */

let reportDonutInstance = null;
let reportZoneChartInstance = null;
let reportTrendChartInstance = null;
let editLimitModalInstance = null;
let cachedLimitsList = [];

document.addEventListener('DOMContentLoaded', () => {
  editLimitModalInstance = new bootstrap.Modal(document.getElementById('editLimitModal'));

  loadAllReportsData();

  document.getElementById('refreshReportBtn')?.addEventListener('click', loadAllReportsData);
  document.getElementById('printReportBtn')?.addEventListener('click', () => window.print());

  setupEditLimitForm();
});

async function loadAllReportsData() {
  try {
    await Promise.all([
      renderComplianceRatio(),
      renderZoneComparison(),
      renderDailyTrends(),
      renderTopViolations(),
      renderZoneLimits()
    ]);
  } catch (err) {
    console.error('Error loading reports:', err);
  }
}

async function renderComplianceRatio() {
  const resp = await API.getDashboardStats();
  if (resp.success && resp.data) {
    const s = resp.data;
    document.getElementById('repSafeCount').textContent = `${s.safeLocations} Sites`;
    document.getElementById('repViolationCount').textContent = `${s.nonCompliantLocations} Sites`;
    document.getElementById('repAdherencePercent').textContent = `${s.complianceRatePercent}%`;

    const ctx = document.getElementById('reportComplianceDonut')?.getContext('2d');
    if (ctx) {
      if (reportDonutInstance) reportDonutInstance.destroy();

      reportDonutInstance = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['Compliant / Safe', 'Statutory Exceeded / Non-Compliant'],
          datasets: [{
            data: [s.safeLocations, s.nonCompliantLocations],
            backgroundColor: ['#10b981', '#f43f5e'],
            borderColor: '#111827',
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: { color: '#94a3b8', font: { size: 12 } }
            }
          }
        }
      });
    }
  }
}

async function renderZoneComparison() {
  const resp = await API.getZoneSummaries();
  if (resp.success && resp.data) {
    const zones = resp.data;
    const ctx = document.getElementById('reportZoneChart')?.getContext('2d');
    if (ctx) {
      if (reportZoneChartInstance) reportZoneChartInstance.destroy();

      const labels = zones.map(z => z.zoneType);
      const avgNoise = zones.map(z => z.averageNoise);
      const dayLimits = zones.map(z => z.dayLimit);
      const nightLimits = zones.map(z => z.nightLimit);

      reportZoneChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: 'Average Recorded (dB)',
              data: avgNoise,
              backgroundColor: '#6366f1',
              borderRadius: 6
            },
            {
              label: 'Statutory Day Limit (dB)',
              data: dayLimits,
              backgroundColor: '#10b981',
              borderRadius: 6
            },
            {
              label: 'Statutory Night Limit (dB)',
              data: nightLimits,
              backgroundColor: '#f59e0b',
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
              max: 95,
              grid: { color: 'rgba(255,255,255,0.06)' },
              ticks: { color: '#94a3b8' }
            },
            x: {
              grid: { display: false },
              ticks: { color: '#94a3b8' }
            }
          },
          plugins: {
            legend: {
              position: 'top',
              labels: { color: '#94a3b8', font: { size: 11 } }
            }
          }
        }
      });
    }
  }
}

async function renderDailyTrends() {
  const resp = await API.getDailyTrends();
  if (resp.success && resp.data) {
    const trends = resp.data;
    const ctx = document.getElementById('reportDailyTrendChart')?.getContext('2d');
    if (ctx) {
      if (reportTrendChartInstance) reportTrendChartInstance.destroy();

      reportTrendChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
          labels: trends.map(t => t.date),
          datasets: [
            {
              label: 'Average Noise (dB)',
              data: trends.map(t => t.averageNoise),
              borderColor: '#06b6d4',
              backgroundColor: 'rgba(6, 182, 212, 0.1)',
              fill: true,
              tension: 0.35,
              pointRadius: 4
            },
            {
              label: 'Max Peak Noise (dB)',
              data: trends.map(t => t.maxNoise),
              borderColor: '#f43f5e',
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
              ticks: { color: '#94a3b8' }
            }
          },
          plugins: {
            legend: {
              position: 'top',
              labels: { color: '#94a3b8', font: { size: 11 } }
            }
          }
        }
      });
    }
  }
}

async function renderTopViolations() {
  const tbody = document.getElementById('topViolationsTableBody');
  if (!tbody) return;

  const resp = await API.getTopViolations();
  if (resp.success && resp.data && resp.data.length > 0) {
    tbody.innerHTML = '';
    resp.data.forEach((v, index) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><span class="badge bg-danger rounded-circle">${index + 1}</span></td>
        <td><strong class="text-light">${v.locationName}</strong></td>
        <td><span class="badge-zone">${v.zoneType}</span></td>
        <td><span class="fw-bold text-danger">${v.noiseLevel} dB</span></td>
        <td class="text-muted">${v.limitApplied} dB</td>
        <td><span class="badge bg-danger">+${v.exceedanceDb} dB</span></td>
      `;
      tbody.appendChild(tr);
    });
  } else {
    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No violations recorded.</td></tr>';
  }
}

async function renderZoneLimits() {
  const tbody = document.getElementById('zoneLimitsTableBody');
  if (!tbody) return;

  const resp = await API.getZoneLimits();
  if (resp.success && resp.data) {
    cachedLimitsList = resp.data;
    tbody.innerHTML = '';
    cachedLimitsList.forEach(zl => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong class="text-light">${zl.zoneType}</strong></td>
        <td><span class="text-warning">${zl.dayLimitDb} dB</span></td>
        <td><span class="text-warning">${zl.nightLimitDb} dB</span></td>
        <td><span class="text-muted">${zl.standardLimitDb} dB</span></td>
        <td class="no-print" style="text-align: right;">
          <button class="btn btn-sm btn-cyber-secondary py-0 px-2" onclick="openEditLimitModal(${zl.id})" title="Edit Limits">
            <i class="bi bi-pencil"></i> Edit
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } else {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-muted">No limits found.</td></tr>';
  }
}

window.openEditLimitModal = function(id) {
  const zl = cachedLimitsList.find(item => item.id === id);
  if (!zl) return;

  document.getElementById('editLimitId').value = zl.id;
  document.getElementById('editLimitZoneType').value = zl.zoneType;
  document.getElementById('editDayLimit').value = zl.dayLimitDb;
  document.getElementById('editNightLimit').value = zl.nightLimitDb;
  document.getElementById('editStandardLimit').value = zl.standardLimitDb;
  document.getElementById('editLimitDesc').value = zl.description || '';

  editLimitModalInstance.show();
};

function setupEditLimitForm() {
  const form = document.getElementById('editLimitForm');
  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('editLimitId').value;
    const payload = {
      zoneType: document.getElementById('editLimitZoneType').value,
      dayLimitDb: parseFloat(document.getElementById('editDayLimit').value),
      nightLimitDb: parseFloat(document.getElementById('editNightLimit').value),
      standardLimitDb: parseFloat(document.getElementById('editStandardLimit').value),
      description: document.getElementById('editLimitDesc').value.trim()
    };

    try {
      const resp = await API.updateZoneLimit(id, payload);
      if (resp.success) {
        editLimitModalInstance.hide();
        showAlert(`Thresholds for ${payload.zoneType} updated successfully!`, 'success');
        renderZoneLimits();
        renderZoneComparison();
      } else {
        alert(resp.message || 'Error updating limit.');
      }
    } catch (err) {
      alert('Failed to update limit.');
    }
  });
}

function showAlert(msg, type = 'info') {
  const alertEl = document.getElementById('reportAlert');
  if (!alertEl) return;
  alertEl.className = `alert alert-${type} py-2 px-3 mb-4`;
  alertEl.innerHTML = msg;
  alertEl.classList.remove('d-none');
  setTimeout(() => alertEl.classList.add('d-none'), 5000);
}
