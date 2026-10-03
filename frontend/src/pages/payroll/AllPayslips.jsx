import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { payrollApi } from '../../api/payrollApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function AllPayslips() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [payslips, setPayslips] = useState([]);
  const [error, setError] = useState(null);

  const load = (m = month, y = year) => payrollApi.all({ month: m, year: y }).then(setPayslips).catch(setError);

  useEffect(() => { load(); }, []);

  const handleFilter = (e) => {
    e.preventDefault();
    load();
  };

  const handleRunAll = async () => {
    if (!window.confirm('Generate payslips for ALL active employees for this period?')) return;
    setError(null);
    try {
      await payrollApi.generateForAll(month, year);
      load();
    } catch (err) {
      setError(err);
    }
  };

  const handleDownload = async (id) => {
    try {
      const { url, filename } = await payrollApi.downloadPdf(id);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-money-check-dollar"></i> All Payslips</h3>
        <Link to="/payroll/generate" className="btn btn-primary">Generate Payslip</Link>
      </div>

      <ErrorAlert error={error} />

      <div className="card card-stat mb-3"><div className="card-body">
        <form onSubmit={handleFilter} className="row g-2 align-items-end">
          <div className="col-md-3">
            <label className="form-label">Month</label>
            <input type="number" className="form-control" min={1} max={12} value={month} onChange={(e) => setMonth(e.target.value)} />
          </div>
          <div className="col-md-3">
            <label className="form-label">Year</label>
            <input type="number" className="form-control" value={year} onChange={(e) => setYear(e.target.value)} />
          </div>
          <div className="col-md-2">
            <button type="submit" className="btn btn-outline-primary w-100">Filter</button>
          </div>
          <div className="col-md-4 text-end">
            <button type="button" className="btn btn-success" onClick={handleRunAll}>
              Run Payroll for All (Active Employees)
            </button>
          </div>
        </form>
      </div></div>

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover">
          <thead><tr><th>Employee</th><th>Period</th><th>Net Salary</th><th>Status</th><th></th></tr></thead>
          <tbody>
            {payslips.map((p) => (
              <tr key={p.id}>
                <td>{p.employee.firstName} {p.employee.lastName} ({p.employee.employeeCode})</td>
                <td>{p.payMonth}/{p.payYear}</td>
                <td>{p.netSalary}</td>
                <td><span className="badge bg-success">{p.status}</span></td>
                <td>
                  <Link to={`/payroll/${p.id}`} className="btn btn-sm btn-outline-primary me-1">View</Link>
                  <button className="btn btn-sm btn-outline-secondary" onClick={() => handleDownload(p.id)}>PDF</button>
                </td>
              </tr>
            ))}
            {payslips.length === 0 && <tr><td colSpan={5} className="text-muted text-center py-4">No payslips generated for this period yet.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
