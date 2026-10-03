import { useEffect, useState } from 'react';
import { scheduleApi } from '../../api/scheduleApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function ManageRoster() {
  const [assignments, setAssignments] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [shifts, setShifts] = useState([]);
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [rosterForm, setRosterForm] = useState({ employeeId: '', shiftId: '', rosterDate: '' });
  const [shiftForm, setShiftForm] = useState({ name: '', startTime: '', endTime: '' });
  const [error, setError] = useState(null);

  const loadAssignments = (params = {}) => scheduleApi.manage(params).then(setAssignments).catch(setError);
  const loadShifts = () => scheduleApi.shifts().then(setShifts).catch(setError);

  useEffect(() => {
    loadAssignments();
    loadShifts();
    scheduleApi.manageableEmployees().then(setEmployees).catch(() => {});
  }, []);

  const handleFilter = (e) => {
    e.preventDefault();
    loadAssignments({ start: start || undefined, end: end || undefined });
  };

  const handleAssign = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await scheduleApi.assign(rosterForm);
      loadAssignments({ start: start || undefined, end: end || undefined });
    } catch (err) {
      setError(err);
    }
  };

  const handleNewShift = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await scheduleApi.newShift(shiftForm);
      setShiftForm({ name: '', startTime: '', endTime: '' });
      loadShifts();
    } catch (err) {
      setError(err);
    }
  };

  const handleDeleteShift = async (id) => {
    if (!window.confirm('Delete this shift template? This cannot be undone.')) return;
    setError(null);
    try {
      await scheduleApi.deleteShift(id);
      loadShifts();
    } catch (err) {
      setError(err);
    }
  };

  const handleUnassign = async (id) => {
    if (!window.confirm('Remove this roster assignment?')) return;
    setError(null);
    try {
      await scheduleApi.unassign(id);
      loadAssignments({ start: start || undefined, end: end || undefined });
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-calendar-days"></i> Manage Roster</h3>

      <ErrorAlert error={error} />

      <div className="row g-4 mb-4">
        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <h5>Assign Shift</h5>
            <form onSubmit={handleAssign}>
              <div className="mb-2">
                <label className="form-label">Employee</label>
                <select className="form-select" value={rosterForm.employeeId} onChange={(e) => setRosterForm({ ...rosterForm, employeeId: e.target.value })} required>
                  <option value="">-- Select --</option>
                  {employees.map((e) => <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>)}
                </select>
              </div>
              <div className="mb-2">
                <label className="form-label">Shift</label>
                <select className="form-select" value={rosterForm.shiftId} onChange={(e) => setRosterForm({ ...rosterForm, shiftId: e.target.value })} required>
                  <option value="">-- Select --</option>
                  {shifts.map((s) => <option key={s.id} value={s.id}>{s.name} ({s.startTime}-{s.endTime})</option>)}
                </select>
              </div>
              <div className="mb-2">
                <label className="form-label">Date</label>
                <input type="date" className="form-control" value={rosterForm.rosterDate} onChange={(e) => setRosterForm({ ...rosterForm, rosterDate: e.target.value })} required />
              </div>
              <button type="submit" className="btn btn-primary">Assign</button>
            </form>
          </div></div>
        </div>

        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <h5>Add Shift Template</h5>
            <form onSubmit={handleNewShift}>
              <div className="mb-2">
                <label className="form-label">Name</label>
                <input type="text" className="form-control" placeholder="e.g. Weekend" value={shiftForm.name} onChange={(e) => setShiftForm({ ...shiftForm, name: e.target.value })} required />
              </div>
              <div className="row g-2">
                <div className="col-6">
                  <label className="form-label">Start Time</label>
                  <input type="time" className="form-control" value={shiftForm.startTime} onChange={(e) => setShiftForm({ ...shiftForm, startTime: e.target.value })} required />
                </div>
                <div className="col-6">
                  <label className="form-label">End Time</label>
                  <input type="time" className="form-control" value={shiftForm.endTime} onChange={(e) => setShiftForm({ ...shiftForm, endTime: e.target.value })} required />
                </div>
              </div>
              <button type="submit" className="btn btn-outline-primary mt-2">Add Shift</button>
            </form>
            {shifts.length > 0 && (
              <ul className="list-group mt-3">
                {shifts.map((s) => (
                  <li key={s.id} className="list-group-item d-flex justify-content-between align-items-center">
                    <span>{s.name} ({s.startTime}-{s.endTime})</span>
                    <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleDeleteShift(s.id)}>Delete</button>
                  </li>
                ))}
              </ul>
            )}
          </div></div>
        </div>
      </div>

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
          <thead><tr><th>Date</th><th>Employee</th><th>Shift</th><th>Swap Status</th><th></th></tr></thead>
          <tbody>
            {assignments.map((a) => (
              <tr key={a.id}>
                <td>{a.rosterDate}</td>
                <td>{a.employee.firstName} {a.employee.lastName}</td>
                <td>{a.shift.name}</td>
                <td><span className="badge bg-secondary">{a.swapStatus}</span></td>
                <td><button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleUnassign(a.id)}>Remove</button></td>
              </tr>
            ))}
            {assignments.length === 0 && <tr><td colSpan={5} className="text-muted text-center py-3">No assignments for this period.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
