import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { payrollApi } from '../../api/payrollApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function GeneratePayroll() {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const now = new Date();
  const [employeeId, setEmployeeId] = useState('');
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    payrollApi.eligibleEmployees().then((list) => {
      setEmployees(list);
      if (list.length > 0) setEmployeeId(list[0].id);
    }).catch(setError);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await payrollApi.generate(employeeId, month, year);
      navigate('/payroll/all');
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-calculator"></i> Generate Payslip</h3>

      <div className="card card-stat" style={{ maxWidth: 600 }}>
        <div className="card-body">
          <ErrorAlert error={error} />
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Employee</label>
              <select className="form-select" value={employeeId} onChange={(e) => setEmployeeId(e.target.value)} required>
                {employees.map((e) => (
                  <option key={e.id} value={e.id}>{e.firstName} {e.lastName} ({e.employeeCode})</option>
                ))}
              </select>
            </div>
            <div className="row g-3">
              <div className="col-md-6">
                <label className="form-label">Month</label>
                <input type="number" className="form-control" min={1} max={12} value={month} onChange={(e) => setMonth(e.target.value)} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Year</label>
                <input type="number" className="form-control" value={year} onChange={(e) => setYear(e.target.value)} required />
              </div>
            </div>
            <p className="text-muted small mt-3">
              Salary is calculated from the employee's attendance for the selected period
              (pro-rated basic pay + overtime, minus tax and EPF), using the STRATEGY
              pattern to pick the calculation rule for the employee's department.
            </p>
            <button type="submit" className="btn btn-primary" disabled={submitting}>Generate</button>
            <button type="button" className="btn btn-outline-secondary ms-2" onClick={() => navigate('/payroll/all')}>Cancel</button>
          </form>
        </div>
      </div>
    </div>
  );
}
