import { useEffect, useState } from 'react';
import { leaveApi } from '../../api/leaveApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function LeaveApprovals() {
  const [requests, setRequests] = useState([]);
  const [comments, setComments] = useState({});
  const [error, setError] = useState(null);

  const load = () => leaveApi.approvals().then(setRequests).catch(setError);

  useEffect(() => { load(); }, []);

  const decide = async (id, approve) => {
    setError(null);
    try {
      await leaveApi.decide({ leaveRequestId: id, approve, comments: comments[id] || '' });
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-clipboard-check"></i> Leave Approvals</h3>

      <ErrorAlert error={error} />

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover align-middle">
          <thead>
            <tr><th>Employee</th><th>Type</th><th>Dates</th><th>Days</th><th>Reason</th><th>Applied</th><th style={{ width: 300 }}>Decision</th></tr>
          </thead>
          <tbody>
            {requests.map((r) => (
              <tr key={r.id}>
                <td>{r.employee.firstName} {r.employee.lastName}</td>
                <td>{r.leaveType.name}</td>
                <td>{r.startDate} to {r.endDate}</td>
                <td>{r.numberOfDays}</td>
                <td>{r.reason}</td>
                <td>{new Date(r.appliedDate).toLocaleString()}</td>
                <td>
                  <div className="d-flex gap-2">
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      placeholder="Comments (optional)"
                      value={comments[r.id] || ''}
                      onChange={(e) => setComments({ ...comments, [r.id]: e.target.value })}
                    />
                    <button className="btn btn-sm btn-success" onClick={() => decide(r.id, true)}>Approve</button>
                    <button className="btn btn-sm btn-danger" onClick={() => decide(r.id, false)}>Reject</button>
                  </div>
                </td>
              </tr>
            ))}
            {requests.length === 0 && <tr><td colSpan={7} className="text-muted text-center py-4">No pending leave requests.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
