import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { leaveApi } from '../../api/leaveApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function ApplyLeave() {
  const navigate = useNavigate();
  const [leaveTypes, setLeaveTypes] = useState([]);
  const [form, setForm] = useState({ leaveTypeId: '', startDate: '', endDate: '', reason: '' });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    leaveApi.types().then((types) => {
      setLeaveTypes(types);
      if (types.length > 0) {
        setForm((prev) => ({ ...prev, leaveTypeId: types[0].id }));
      }
    }).catch(setError);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await leaveApi.apply(form);
      navigate('/leave/my');
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-calendar-plus"></i> Apply for Leave</h3>

      <div className="card card-stat" style={{ maxWidth: 600 }}>
        <div className="card-body">
          <ErrorAlert error={error} />
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Leave Type</label>
              <select className="form-select" value={form.leaveTypeId} onChange={(e) => setForm({ ...form, leaveTypeId: e.target.value })} required>
                {leaveTypes.map((lt) => (
                  <option key={lt.id} value={lt.id}>{lt.name} ({lt.defaultDaysPerYear} days/yr)</option>
                ))}
              </select>
            </div>
            <div className="row g-3">
              <div className="col-md-6">
                <label className="form-label">Start Date</label>
                <input type="date" className="form-control" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">End Date</label>
                <input type="date" className="form-control" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} required />
              </div>
            </div>
            <div className="mb-3 mt-3">
              <label className="form-label">Reason</label>
              <textarea className="form-control" rows={3} value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} />
            </div>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              <i className="fa-solid fa-paper-plane"></i> Submit Request
            </button>
            <button type="button" className="btn btn-outline-secondary ms-2" onClick={() => navigate('/leave/my')}>Cancel</button>
          </form>
        </div>
      </div>
    </div>
  );
}
