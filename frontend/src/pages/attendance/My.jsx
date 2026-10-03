import { useEffect, useState } from 'react';
import { attendanceApi } from '../../api/attendanceApi';
import ErrorAlert from '../../components/common/ErrorAlert';

function formatTime(dt) {
  return dt ? new Date(dt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '-';
}

export default function MyAttendance() {
  const [today, setToday] = useState(null);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState(null);

  const load = () => attendanceApi.my().then((data) => {
    setToday(data.today);
    setHistory(data.history);
  }).catch(setError);

  useEffect(() => { load(); }, []);

  const handleClockIn = async () => {
    setError(null);
    try { await attendanceApi.clockIn(); load(); } catch (err) { setError(err); }
  };

  const handleClockOut = async () => {
    setError(null);
    try { await attendanceApi.clockOut(); load(); } catch (err) { setError(err); }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-clock"></i> My Attendance</h3>

      <ErrorAlert error={error} />

      <div className="card card-stat mb-4">
        <div className="card-body">
          <h5>Today</h5>
          {!today && (
            <div>
              <p className="text-muted">You have not clocked in today.</p>
              <button className="btn btn-success" onClick={handleClockIn}>
                <i className="fa-solid fa-right-to-bracket"></i> Clock In
              </button>
            </div>
          )}
          {today && (
            <div>
              <p>
                Clocked in at <strong>{formatTime(today.clockIn)}</strong>
                <span className="badge bg-info ms-2">{today.status}</span>
              </p>
              {today.clockOut && (
                <p>Clocked out at <strong>{formatTime(today.clockOut)}</strong> &mdash; Hours worked: <strong>{today.hoursWorked}</strong></p>
              )}
              {!today.clockOut && (
                <button className="btn btn-warning" onClick={handleClockOut}>
                  <i className="fa-solid fa-right-from-bracket"></i> Clock Out
                </button>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="card card-stat">
        <div className="card-body">
          <h5>History</h5>
          <table className="table table-hover">
            <thead><tr><th>Date</th><th>Clock In</th><th>Clock Out</th><th>Hours</th><th>Status</th></tr></thead>
            <tbody>
              {history.map((a) => (
                <tr key={a.id}>
                  <td>{a.attendanceDate}</td>
                  <td>{formatTime(a.clockIn)}</td>
                  <td>{formatTime(a.clockOut)}</td>
                  <td>{a.hoursWorked ?? '-'}</td>
                  <td>
                    <span className={`badge ${a.status === 'PRESENT' ? 'bg-success' : a.status === 'LATE' ? 'bg-warning' : 'bg-secondary'}`}>
                      {a.status}
                    </span>
                  </td>
                </tr>
              ))}
              {history.length === 0 && (
                <tr><td colSpan={5} className="text-muted text-center py-3">No attendance records yet.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
