import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { leaveApi } from '../../api/leaveApi';
import ErrorAlert from '../../components/common/ErrorAlert';

const statusBadge = {
  APPROVED: 'bg-success',
  REJECTED: 'bg-danger',
  CANCELLED: 'bg-secondary',
  PENDING: 'bg-warning',
};

export default function MyLeaveRequests() {
  const [requests, setRequests] = useState([]);
  const [balances, setBalances] = useState([]);
  const [error, setError] = useState(null);

  const load = () => leaveApi.my().then((data) => {
    setRequests(data.requests);
    setBalances(data.balances);
  }).catch(setError);

  useEffect(() => { load(); }, []);

  const handleCancel = async (id) => {
    if (!window.confirm('Cancel this request?')) return;
    try {
      await leaveApi.cancel(id);
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-calendar-days"></i> My Leave</h3>
        <Link to="/leave/apply" className="btn btn-primary"><i className="fa-solid fa-plus"></i> Apply for Leave</Link>
      </div>

      <ErrorAlert error={error} />

      <div className="card card-stat mb-4"><div className="card-body">
        <h5>Balances</h5>
        <table className="table table-sm mb-0">
          <thead><tr><th>Type</th><th>Allocated</th><th>Used</th><th>Remaining</th></tr></thead>
          <tbody>
            {balances.map((b) => (
              <tr key={b.id}>
                <td>{b.leaveType.name}</td>
                <td>{b.allocatedDays}</td>
                <td>{b.usedDays}</td>
                <td>{b.remainingDays}</td>
              </tr>
            ))}
            {balances.length === 0 && <tr><td colSpan={4} className="text-muted">No balance records for this year yet.</td></tr>}
          </tbody>
        </table>
      </div></div>

      <div className="card card-stat"><div className="card-body">
        <h5>My Requests</h5>
        <table className="table table-hover">
          <thead><tr><th>Type</th><th>Dates</th><th>Days</th><th>Status</th><th>Comments</th><th></th></tr></thead>
          <tbody>
            {requests.map((r) => (
              <tr key={r.id}>
                <td>{r.leaveType.name}</td>
                <td>{r.startDate} to {r.endDate}</td>
                <td>{r.numberOfDays}</td>
                <td><span className={`badge ${statusBadge[r.status]}`}>{r.status}</span></td>
                <td>{r.comments}</td>
                <td>
                  {r.status === 'PENDING' && (
                    <button className="btn btn-sm btn-outline-danger" onClick={() => handleCancel(r.id)}>Cancel</button>
                  )}
                </td>
              </tr>
            ))}
            {requests.length === 0 && <tr><td colSpan={6} className="text-muted text-center py-3">No leave requests yet.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
