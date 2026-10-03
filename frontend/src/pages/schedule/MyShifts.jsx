import { useEffect, useState } from 'react';
import { scheduleApi } from '../../api/scheduleApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function MyShifts() {
  const [shifts, setShifts] = useState([]);
  const [colleagues, setColleagues] = useState([]);
  const [swapChoice, setSwapChoice] = useState({});
  const [error, setError] = useState(null);

  const load = () => scheduleApi.my().then(setShifts).catch(setError);

  useEffect(() => {
    load();
    scheduleApi.colleagues().then(setColleagues).catch(() => {});
  }, []);

  const handleRequestSwap = async (rosterAssignmentId) => {
    const swapWithEmployeeId = swapChoice[rosterAssignmentId];
    if (!swapWithEmployeeId) return;
    setError(null);
    try {
      await scheduleApi.requestSwap(rosterAssignmentId, swapWithEmployeeId);
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-calendar-week"></i> My Shifts (next 30 days)</h3>

      <ErrorAlert error={error} />

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover">
          <thead><tr><th>Date</th><th>Shift</th><th>Time</th><th>Swap Status</th><th>Request Swap</th></tr></thead>
          <tbody>
            {shifts.map((s) => (
              <tr key={s.id}>
                <td>{s.rosterDate}</td>
                <td>{s.shift.name}</td>
                <td>{s.shift.startTime} - {s.shift.endTime}</td>
                <td><span className="badge bg-secondary">{s.swapStatus}</span></td>
                <td>
                  {s.swapStatus === 'NONE' && (
                    <div className="d-flex gap-2">
                      <select
                        className="form-select form-select-sm"
                        value={swapChoice[s.id] || ''}
                        onChange={(e) => setSwapChoice({ ...swapChoice, [s.id]: e.target.value })}
                      >
                        <option value="">Select colleague...</option>
                        {colleagues.map((c) => <option key={c.id} value={c.id}>{c.firstName} {c.lastName}</option>)}
                      </select>
                      <button className="btn btn-sm btn-outline-primary" onClick={() => handleRequestSwap(s.id)}>Request Swap</button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
            {shifts.length === 0 && <tr><td colSpan={5} className="text-muted text-center py-3">No shifts scheduled.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
