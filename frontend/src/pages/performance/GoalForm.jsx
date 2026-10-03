import { useEffect, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { performanceApi } from '../../api/performanceApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function GoalForm() {
  const { id } = useParams();
  const isEditing = !!id;
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [employee, setEmployee] = useState(null);
  const [form, setForm] = useState({ employeeId: searchParams.get('employeeId') || '', title: '', description: '', targetDate: '', status: 'NOT_STARTED' });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (isEditing) {
      performanceApi.getGoal(id).then((g) => {
        setForm({
          employeeId: g.employee.id,
          title: g.title,
          description: g.description || '',
          targetDate: g.targetDate || '',
          status: g.status,
        });
        setEmployee(g.employee);
      }).catch(setError);
    } else if (form.employeeId) {
      performanceApi.employeeSummary(form.employeeId).then(setEmployee).catch(setError);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      if (isEditing) {
        await performanceApi.updateGoal(id, form);
      } else {
        await performanceApi.createGoal(form);
      }
      navigate(`/performance/goals?employeeId=${form.employeeId}`);
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4">
        <i className="fa-solid fa-bullseye"></i> {isEditing ? 'Edit Goal' : 'Set Goal'}
        {employee && <> for {employee.firstName} {employee.lastName}</>}
      </h3>

      <div className="card card-stat" style={{ maxWidth: 600 }}>
        <div className="card-body">
          <ErrorAlert error={error} />
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Title</label>
              <input type="text" className="form-control" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
            </div>
            <div className="mb-3">
              <label className="form-label">Description</label>
              <textarea className="form-control" rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Target Date</label>
              <input type="date" className="form-control" value={form.targetDate} onChange={(e) => setForm({ ...form, targetDate: e.target.value })} />
            </div>
            {isEditing && (
              <div className="mb-3">
                <label className="form-label">Status</label>
                <select className="form-select" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                  <option value="NOT_STARTED">Not Started</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </div>
            )}
            <button type="submit" className="btn btn-primary" disabled={submitting}>Save Goal</button>
            <button type="button" className="btn btn-outline-secondary ms-2" onClick={() => navigate(`/performance/goals?employeeId=${form.employeeId}`)}>Cancel</button>
          </form>
        </div>
      </div>
    </div>
  );
}
