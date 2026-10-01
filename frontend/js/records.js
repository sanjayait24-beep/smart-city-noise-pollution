/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Dashboard
 * Records Management Controller
 */

let allLoadedRecords = [];
let pendingDeleteId = null;
let editModalInstance = null;
let deleteModalInstance = null;

document.addEventListener('DOMContentLoaded', () => {
  editModalInstance = new bootstrap.Modal(document.getElementById('editModal'));
  deleteModalInstance = new bootstrap.Modal(document.getElementById('deleteModal'));

  // Check URL query parameters (e.g. ?search=Hospital)
  const urlParams = new URLSearchParams(window.location.search);
  const searchParam = urlParams.get('search');
  if (searchParam) {
    document.getElementById('searchInput').value = searchParam;
  }

  fetchAndRenderRecords();
  setupFilterListeners();
  setupEditForm();
  setupDeleteHandler();
  setupCsvExport();
});

async function fetchAndRenderRecords() {
  const tbody = document.getElementById('recordsTableBody');
  tbody.innerHTML = '<tr><td colspan="10" class="text-center py-5 text-muted">Loading records...</td></tr>';

  const filters = {
    search: document.getElementById('searchInput').value.trim(),
    zoneType: document.getElementById('zoneFilter').value,
    complianceStatus: document.getElementById('complianceFilter').value,
    startDate: document.getElementById('startDateFilter').value,
    endDate: document.getElementById('endDateFilter').value
  };

  try {
    const resp = await API.getRecords(filters);
    if (resp.success && resp.data) {
      allLoadedRecords = resp.data;
      renderTable(allLoadedRecords);
    } else {
      tbody.innerHTML = `<tr><td colspan="10" class="text-center py-5 text-danger">${resp.message || 'Error loading records.'}</td></tr>`;
    }
  } catch (err) {
    tbody.innerHTML = '<tr><td colspan="10" class="text-center py-5 text-danger">Failed to connect to server.</td></tr>';
  }
}

function renderTable(records) {
  const tbody = document.getElementById('recordsTableBody');
  const countDisplay = document.getElementById('recordCountDisplay');
  countDisplay.textContent = `Showing ${records.length} records`;

  if (records.length === 0) {
    tbody.innerHTML = '<tr><td colspan="10" class="text-center py-5 text-muted">No records match the selected filters.</td></tr>';
    return;
  }

  tbody.innerHTML = '';
  records.forEach(r => {
    const isSafe = (r.complianceStatus || '').toUpperCase() === 'SAFE';
    const statusClass = isSafe ? 'safe' : 'non-compliant';
    const statusIcon = isSafe ? 'bi-shield-check' : 'bi-exclamation-triangle';

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td class="text-muted small">#${r.id}</td>
      <td>
        <strong class="text-light">${escapeHtml(r.locationName)}</strong>
      </td>
      <td><span class="badge-zone">${escapeHtml(r.zoneType)}</span></td>
      <td>
        <span class="fw-bold ${isSafe ? 'text-safe' : 'text-danger'}">${r.noiseLevel} dB</span>
      </td>
      <td class="text-muted">${r.limitApplied} dB</td>
      <td>
        ${r.exceedanceDb > 0 
          ? `<span class="badge bg-danger">+${r.exceedanceDb} dB</span>` 
          : `<span class="badge bg-dark text-muted">0.0 dB</span>`}
      </td>
      <td class="text-muted small">
        ${r.recordDate} <br/>
        <span class="text-dim">${r.recordTime}</span>
      </td>
      <td>
        <span class="badge-status ${statusClass}">
          <i class="bi ${statusIcon}"></i> ${r.complianceStatus}
        </span>
      </td>
      <td class="text-muted small" style="max-width: 200px;">
        <div class="text-truncate" title="${escapeHtml(r.description || '')}">
          ${escapeHtml(r.description || 'N/A')}
        </div>
      </td>
      <td style="text-align: right; white-space: nowrap;">
        <button class="btn btn-sm btn-cyber-secondary py-1 px-2 me-1" onclick="openEditModal(${r.id})" title="Edit Record">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-sm btn-cyber-danger py-1 px-2" onclick="openDeleteModal(${r.id}, '${escapeHtml(r.locationName)}')" title="Delete Record">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function setupFilterListeners() {
  const searchInput = document.getElementById('searchInput');
  const zoneFilter = document.getElementById('zoneFilter');
  const compFilter = document.getElementById('complianceFilter');
  const startFilter = document.getElementById('startDateFilter');
  const endFilter = document.getElementById('endDateFilter');
  const resetBtn = document.getElementById('resetFiltersBtn');

  let debounceTimer;
  searchInput?.addEventListener('input', () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(fetchAndRenderRecords, 350);
  });

  [zoneFilter, compFilter, startFilter, endFilter].forEach(el => {
    el?.addEventListener('change', fetchAndRenderRecords);
  });

  resetBtn?.addEventListener('click', () => {
    searchInput.value = '';
    zoneFilter.value = '';
    compFilter.value = '';
    startFilter.value = '';
    endFilter.value = '';
    fetchAndRenderRecords();
  });
}

window.openEditModal = function(id) {
  const record = allLoadedRecords.find(r => r.id === id);
  if (!record) return;

  document.getElementById('editRecordId').value = record.id;
  document.getElementById('editLocationName').value = record.locationName;
  document.getElementById('editZoneType').value = record.zoneType;
  document.getElementById('editNoiseLevel').value = record.noiseLevel;
  document.getElementById('editDate').value = record.recordDate;
  document.getElementById('editTime').value = record.recordTime ? record.recordTime.substring(0, 5) : '';
  document.getElementById('editDescription').value = record.description || '';

  editModalInstance.show();
};

function setupEditForm() {
  const form = document.getElementById('editRecordForm');
  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('editRecordId').value;
    const payload = {
      locationName: document.getElementById('editLocationName').value.trim(),
      zoneType: document.getElementById('editZoneType').value,
      noiseLevel: parseFloat(document.getElementById('editNoiseLevel').value),
      recordDate: document.getElementById('editDate').value,
      recordTime: document.getElementById('editTime').value,
      description: document.getElementById('editDescription').value.trim()
    };

    try {
      const resp = await API.updateRecord(id, payload);
      if (resp.success) {
        editModalInstance.hide();
        showAlert(`Record #${id} updated successfully and compliance re-evaluated!`, 'success');
        fetchAndRenderRecords();
      } else {
        alert(resp.message || 'Failed to update record.');
      }
    } catch (err) {
      alert('Error updating record on server.');
    }
  });
}

window.openDeleteModal = function(id, name) {
  pendingDeleteId = id;
  document.getElementById('deleteRecordTitle').textContent = `#${id} (${name})`;
  deleteModalInstance.show();
};

function setupDeleteHandler() {
  document.getElementById('confirmDeleteBtn')?.addEventListener('click', async () => {
    if (!pendingDeleteId) return;

    try {
      const resp = await API.deleteRecord(pendingDeleteId);
      if (resp.success) {
        deleteModalInstance.hide();
        showAlert(`Record #${pendingDeleteId} has been deleted.`, 'info');
        pendingDeleteId = null;
        fetchAndRenderRecords();
      } else {
        alert(resp.message || 'Failed to delete record.');
      }
    } catch (err) {
      alert('Error communicating with server.');
    }
  });
}

function setupCsvExport() {
  document.getElementById('exportCsvBtn')?.addEventListener('click', () => {
    if (allLoadedRecords.length === 0) {
      alert('No records available to export.');
      return;
    }

    const headers = ['ID', 'Location', 'Zone', 'Noise_Level_dB', 'Limit_dB', 'Exceedance_dB', 'Compliance_Status', 'Date', 'Time', 'Description'];
    const rows = allLoadedRecords.map(r => [
      r.id,
      `"${(r.locationName || '').replace(/"/g, '""')}"`,
      `"${r.zoneType}"`,
      r.noiseLevel,
      r.limitApplied,
      r.exceedanceDb,
      r.complianceStatus,
      r.recordDate,
      r.recordTime,
      `"${(r.description || '').replace(/"/g, '""')}"`
    ]);

    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `smart_city_noise_records_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  });
}

function showAlert(msg, type = 'info') {
  const alertEl = document.getElementById('recordsAlert');
  if (!alertEl) return;
  alertEl.className = `alert alert-${type} py-2 px-3 mb-4`;
  alertEl.innerHTML = msg;
  alertEl.classList.remove('d-none');
  setTimeout(() => alertEl.classList.add('d-none'), 5000);
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
}
