/**
 * Smart City Noise Pollution Spatial Mapping & Compliance Dashboard
 * Add Noise Data Form Logic & CSV Batch Upload
 */

let cachedLocations = [];
let cachedLimits = {
  'Silence Zone': { day: 50.0, night: 40.0 },
  'Residential': { day: 55.0, night: 45.0 },
  'Commercial': { day: 65.0, night: 55.0 },
  'Industrial': { day: 75.0, night: 70.0 }
};

document.addEventListener('DOMContentLoaded', async () => {
  initDefaults();
  await loadLocations();
  await loadLimits();
  setupLivePreview();
  setupCsvUpload();
});

function initDefaults() {
  const now = new Date();
  const dateInput = document.getElementById('recordDate');
  const timeInput = document.getElementById('recordTime');

  if (dateInput) {
    dateInput.value = now.toISOString().split('T')[0];
  }
  if (timeInput) {
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    timeInput.value = `${hours}:${minutes}`;
  }
}

async function loadLocations() {
  try {
    const resp = await API.getLocations();
    if (resp.success && resp.data) {
      cachedLocations = resp.data;
      const datalist = document.getElementById('locationDatalist');
      if (datalist) {
        datalist.innerHTML = '';
        cachedLocations.forEach(loc => {
          const opt = document.createElement('option');
          opt.value = loc.name;
          opt.textContent = `${loc.name} (${loc.zoneType})`;
          datalist.appendChild(opt);
        });
      }
    }
  } catch (err) {
    console.error('Failed to fetch locations:', err);
  }
}

async function loadLimits() {
  try {
    const resp = await API.getZoneLimits();
    if (resp.success && resp.data) {
      resp.data.forEach(zl => {
        cachedLimits[zl.zoneType] = { day: zl.dayLimitDb, night: zl.nightLimitDb };
      });
    }
  } catch (err) {
    console.error('Failed to fetch zone limits:', err);
  }
}

function setupLivePreview() {
  const locInput = document.getElementById('locationName');
  const zoneSelect = document.getElementById('zoneType');
  const noiseInput = document.getElementById('noiseLevel');
  const timeInput = document.getElementById('recordTime');
  const latInput = document.getElementById('latitude');
  const lngInput = document.getElementById('longitude');

  // When user selects a known location, auto-fill coordinates and zone
  locInput?.addEventListener('change', () => {
    const entered = locInput.value.trim().toLowerCase();
    const match = cachedLocations.find(l => l.name.toLowerCase() === entered);
    if (match) {
      if (match.zoneType) zoneSelect.value = match.zoneType;
      if (match.latitude) latInput.value = match.latitude;
      if (match.longitude) lngInput.value = match.longitude;
      updateGauge();
    }
  });

  zoneSelect?.addEventListener('change', updateGauge);
  noiseInput?.addEventListener('input', updateGauge);
  timeInput?.addEventListener('change', updateGauge);

  function updateGauge() {
    const zone = zoneSelect.value;
    const noise = parseFloat(noiseInput.value) || 0;
    const timeStr = timeInput.value || '12:00';
    const hours = parseInt(timeStr.split(':')[0], 10);
    const isDay = hours >= 6 && hours < 22;

    const limits = cachedLimits[zone] || { day: 60.0, night: 50.0 };
    const activeLimit = isDay ? limits.day : limits.night;

    document.getElementById('previewZoneName').textContent = `${zone} (${isDay ? 'Day Mode' : 'Night Mode'})`;
    document.getElementById('previewDayLimit').textContent = `${limits.day} dB`;
    document.getElementById('previewNightLimit').textContent = `${limits.night} dB`;

    const gaugeDb = document.getElementById('previewGaugeDb');
    const badgeContainer = document.getElementById('previewStatusBadge');
    const exceedanceText = document.getElementById('previewExceedance');

    gaugeDb.innerHTML = `${noise.toFixed(1)} <span style="font-size: 1.5rem;">dB</span>`;

    if (noise <= 0) {
      gaugeDb.className = 'noise-gauge-number';
      badgeContainer.innerHTML = '<span class="badge bg-secondary">Awaiting Input</span>';
      exceedanceText.textContent = '0.0 dB';
      exceedanceText.className = 'text-safe';
      return;
    }

    const isSafe = noise <= activeLimit;
    const exceedance = Math.max(0, noise - activeLimit);

    if (isSafe) {
      gaugeDb.className = 'noise-gauge-number safe';
      badgeContainer.innerHTML = `
        <span class="badge-status safe">
          <i class="bi bi-shield-check"></i> SAFE / COMPLIANT (Limit: ${activeLimit} dB)
        </span>
      `;
      exceedanceText.textContent = `0.0 dB (Within Limit by ${(activeLimit - noise).toFixed(1)} dB)`;
      exceedanceText.className = 'text-safe';
    } else {
      gaugeDb.className = 'noise-gauge-number danger';
      badgeContainer.innerHTML = `
        <span class="badge-status non-compliant">
          <i class="bi bi-exclamation-triangle"></i> EXCEEDED / NON-COMPLIANT
        </span>
      `;
      exceedanceText.textContent = `+${exceedance.toFixed(1)} dB EXCEEDANCE (Limit: ${activeLimit} dB)`;
      exceedanceText.className = 'text-danger';
    }
  }

  // Handle Form Submission
  const form = document.getElementById('noiseEntryForm');
  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = document.getElementById('saveRecordBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Evaluating & Saving...';

    const payload = {
      locationName: locInput.value.trim(),
      zoneType: zoneSelect.value,
      noiseLevel: parseFloat(noiseInput.value),
      recordDate: document.getElementById('recordDate').value,
      recordTime: timeInput.value,
      latitude: latInput.value ? parseFloat(latInput.value) : null,
      longitude: lngInput.value ? parseFloat(lngInput.value) : null,
      description: document.getElementById('description').value.trim()
    };

    try {
      const resp = await API.createRecord(payload);
      if (resp.success && resp.data) {
        const isSafe = resp.data.complianceStatus === 'SAFE';
        showAlert(
          `Record saved successfully! Location: <strong>${resp.data.locationName}</strong> evaluated as 
           <span class="badge ${isSafe ? 'bg-success' : 'bg-danger'}">${resp.data.complianceStatus}</span> 
           (Noise: ${resp.data.noiseLevel} dB, Limit: ${resp.data.limitApplied} dB).`,
          isSafe ? 'success' : 'warning'
        );
        form.reset();
        initDefaults();
        updateGauge();
        await loadLocations();
      } else {
        showAlert(resp.message || 'Error saving noise record.', 'danger');
      }
    } catch (err) {
      showAlert('Failed to connect to backend server. Make sure Spring Boot is running.', 'danger');
    } finally {
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<i class="bi bi-check2-circle"></i> Save &amp; Check Compliance';
    }
  });
}

function showAlert(msg, type = 'info') {
  const alertEl = document.getElementById('statusAlert');
  if (!alertEl) return;
  alertEl.className = `alert alert-${type} py-2 px-3 mb-4`;
  alertEl.innerHTML = msg;
  alertEl.classList.remove('d-none');
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function setupCsvUpload() {
  const dropzone = document.getElementById('csvDropzone');
  const fileInput = document.getElementById('csvFileInput');
  const uploadBtn = document.getElementById('uploadCsvBtn');
  const fileInfo = document.getElementById('selectedFileInfo');
  const fileNameSpan = document.getElementById('fileNameSpan');
  const resultBox = document.getElementById('csvResultBox');
  const resultContent = document.getElementById('csvResultContent');

  let selectedFile = null;

  dropzone?.addEventListener('click', () => fileInput.click());

  ['dragenter', 'dragover'].forEach(eventName => {
    dropzone?.addEventListener(eventName, (e) => {
      e.preventDefault();
      dropzone.classList.add('dragover');
    });
  });

  ['dragleave', 'drop'].forEach(eventName => {
    dropzone?.addEventListener(eventName, (e) => {
      e.preventDefault();
      dropzone.classList.remove('dragover');
    });
  });

  dropzone?.addEventListener('drop', (e) => {
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      handleFile(e.dataTransfer.files[0]);
    }
  });

  fileInput?.addEventListener('change', () => {
    if (fileInput.files && fileInput.files.length > 0) {
      handleFile(fileInput.files[0]);
    }
  });

  function handleFile(file) {
    if (!file.name.endsWith('.csv')) {
      alert('Please select a valid .csv file.');
      return;
    }
    selectedFile = file;
    fileNameSpan.textContent = `${file.name} (${(file.size / 1024).toFixed(1)} KB)`;
    fileInfo.classList.remove('d-none');
    uploadBtn.disabled = false;
  }

  uploadBtn?.addEventListener('click', async () => {
    if (!selectedFile) return;

    uploadBtn.disabled = true;
    uploadBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing Dataset...';
    resultBox.classList.add('d-none');

    try {
      const resp = await API.importCsv(selectedFile);
      if (resp.success && resp.data) {
        const r = resp.data;
        resultContent.innerHTML = `
          <div class="text-safe mb-1">✓ <strong>${r.importedCount}</strong> records imported successfully.</div>
          ${r.skippedCount > 0 ? `<div class="text-danger mb-1">⚠ <strong>${r.skippedCount}</strong> rows skipped.</div>` : ''}
          ${r.errors && r.errors.length > 0 ? `
            <div class="mt-2 text-warning small border-top border-secondary pt-2">
              <strong>Errors / Warnings:</strong>
              <ul class="mb-0 ps-3">
                ${r.errors.slice(0, 5).map(e => `<li>${e}</li>`).join('')}
              </ul>
            </div>
          ` : ''}
          <div class="mt-3">
            <a href="records.html" class="btn btn-sm btn-cyber-primary py-1 px-3">View Imported Records</a>
          </div>
        `;
        resultBox.classList.remove('d-none');
        await loadLocations();
      } else {
        resultContent.innerHTML = `<div class="text-danger">Failed: ${resp.message}</div>`;
        resultBox.classList.remove('d-none');
      }
    } catch (err) {
      resultContent.innerHTML = '<div class="text-danger">Server error during CSV processing.</div>';
      resultBox.classList.remove('d-none');
    } finally {
      uploadBtn.disabled = false;
      uploadBtn.innerHTML = '<i class="bi bi-upload"></i> Process &amp; Import Dataset';
    }
  });
}
