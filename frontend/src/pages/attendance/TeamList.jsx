import { useEffect, useState } from 'react';
import { attendanceApi } from '../../api/attendanceApi';
import ErrorAlert from '../../components/common/ErrorAlert';

function formatTime(dt) {
  return dt ? new Date(dt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '-';
}

export default function TeamAttendance() {
  const [records, setRecords] = useState([]);
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [error, setError] = useState(null);

  const load = (params = {}) => attendanceApi.list(params).then(setRecords).catch(setError);

  useEffect(() => { load(); }, []);

  const handleFilter = (e) => {
    e.preventDefault();
    load({ start: start || undefined, end: end || undefined });
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-users-rectangle"></i> Team / All Attendance</h3>

      <ErrorAlert error={error} />

      <div className="card card-stat mb-3"><div className="card-body">
        <form onSubmit={handleFilter} className="row g-2 align-items-end">
          <div className="col-md-4">
            <label className="form-label">From</label>
            <input type="date" className="form-control" value={start} onChange={(e) => setStart(e.target.value)} />
          </div>
          <div className="col-md-4">
            <label className="form-label">To</label>
            <input type="date" className="form-control" value={end} onChange={(e) => setEnd(e.target.value)} />
          </div>
          <div className="col-md-2">
            <button type="submit" className="btn btn-outline-primary w-100">Filter</button>
          </div>
        </form>
      </div></div>

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover">
          <thead><tr><th>Employee</th><th>Date</th><th>Clock In</th><th>Clock Out</th><th>Hours</th><th>Status</th></tr></thead>
          <tbody>
            {records.map((a) => (
              <tr key={a.id}>
                <td>{a.employee.firstName} {a.employee.lastName} ({a.employee.employeeCode})</td>
                <td>{a.attendanceDate}</td>
                <td>{formatTime(a.clockIn)}</td>
                <td>{formatTime(a.clockOut)}</td>
                <td>{a.hoursWorked ?? '-'}</td>
                <td><span className="badge bg-secondary">{a.status}</span></td>
              </tr>
            ))}
            {records.length === 0 && (
              <tr><td colSpan={6} className="text-muted text-center py-3">No records for this period.</td></tr>
            )}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
