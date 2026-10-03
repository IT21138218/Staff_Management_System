import { useState } from 'react';
import { Link } from 'react-router-dom';
import { reportApi } from '../api/reportApi';
import ErrorAlert from '../components/common/ErrorAlert';

function downloadFile(url, filename) {
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  window.URL.revokeObjectURL(url);
}

export default function Reports() {
  const today = new Date().toISOString().slice(0, 10);
  const [start, setStart] = useState(today);
  const [end, setEnd] = useState(today);
  const [error, setError] = useState(null);

  const handleExportEmployees = async () => {
    try {
      const { url, filename } = await reportApi.exportEmployees();
      downloadFile(url, filename);
    } catch (err) {
      setError(err);
    }
  };

  const handleExportAttendance = async (e) => {
    e.preventDefault();
    try {
      const { url, filename } = await reportApi.exportAttendance(start, end);
      downloadFile(url, filename);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-chart-column"></i> Reports &amp; Exports</h3>

      <ErrorAlert error={error} />

      <div className="row g-4">
        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <h5><i className="fa-solid fa-users"></i> Employee Directory</h5>
            <p className="text-muted">Export the full employee list (code, name, department, designation, status, salary) as an Excel workbook.</p>
            <button className="btn btn-primary" onClick={handleExportEmployees}>
              <i className="fa-solid fa-file-excel"></i> Export to Excel
            </button>
          </div></div>
        </div>

        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <h5><i className="fa-solid fa-clock"></i> Attendance Report</h5>
            <p className="text-muted">Export attendance records for a date range as an Excel workbook.</p>
            <form onSubmit={handleExportAttendance} className="row g-2 align-items-end">
              <div className="col-6">
                <label className="form-label">From</label>
                <input type="date" className="form-control" value={start} onChange={(e) => setStart(e.target.value)} />
              </div>
              <div className="col-6">
                <label className="form-label">To</label>
                <input type="date" className="form-control" value={end} onChange={(e) => setEnd(e.target.value)} />
              </div>
              <div className="col-12 mt-2">
                <button type="submit" className="btn btn-primary"><i className="fa-solid fa-file-excel"></i> Export to Excel</button>
              </div>
            </form>
          </div></div>
        </div>

        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <h5><i className="fa-solid fa-file-invoice-dollar"></i> Payslips</h5>
            <p className="text-muted">
              Individual payslip PDFs are available from each payslip's detail page
              (<em>Payroll &rarr; All Payslips &rarr; View &rarr; Download PDF</em>).
            </p>
            <Link to="/payroll/all" className="btn btn-outline-primary">Go to Payslips</Link>
          </div></div>
        </div>
      </div>
    </div>
  );
}
