import { useEffect, useState } from 'react';
import { scheduleApi } from '../../api/scheduleApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function SwapRequests() {
  const [swaps, setSwaps] = useState([]);
  const [error, setError] = useState(null);

  const load = () => scheduleApi.pendingSwaps().then(setSwaps).catch(setError);

  useEffect(() => { load(); }, []);

  const decide = async (id, approve) => {
    setError(null);
    try {
      await scheduleApi.decideSwap(id, approve);
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-arrows-rotate"></i> Pending Swap Requests</h3>

      <ErrorAlert error={error} />

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover">
          <thead><tr><th>Date</th><th>Shift</th><th>Current Employee</th><th>Requested Swap With</th><th>Decision</th></tr></thead>
          <tbody>
            {swaps.map((s) => (
              <tr key={s.id}>
                <td>{s.rosterDate}</td>
                <td>{s.shift.name}</td>
                <td>{s.employee.firstName} {s.employee.lastName}</td>
                <td>{s.swapWithEmployee.firstName} {s.swapWithEmployee.lastName}</td>
                <td>
                  <button className="btn btn-sm btn-success me-1" onClick={() => decide(s.id, true)}>Approve</button>
                  <button className="btn btn-sm btn-danger" onClick={() => decide(s.id, false)}>Reject</button>
                </td>
              </tr>
            ))}
            {swaps.length === 0 && <tr><td colSpan={5} className="text-muted text-center py-3">No pending swap requests.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
