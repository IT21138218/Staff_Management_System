import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { performanceApi } from '../../api/performanceApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function Goals() {
  const { user } = useAuth();
  const [searchParams] = useSearchParams();
  const employeeId = searchParams.get('employeeId') || undefined;
  const [goals, setGoals] = useState([]);
  const [error, setError] = useState(null);

  const loadGoals = () => performanceApi.goals(employeeId).then(setGoals).catch(setError);

  useEffect(() => {
    loadGoals();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [employeeId]);

  const canManage = ['SUPERVISOR', 'HR_MANAGER'].includes(user.role);
  const targetEmployeeId = employeeId || user.employeeId;

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this goal? This cannot be undone.')) return;
    setError(null);
    try {
      await performanceApi.deleteGoal(id);
      loadGoals();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-bullseye"></i> Goals</h3>
        {canManage && (
          <Link to={`/performance/goal/new?employeeId=${targetEmployeeId}`} className="btn btn-primary">Set New Goal</Link>
        )}
      </div>

      <ErrorAlert error={error} />

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover">
          <thead><tr><th>Title</th><th>Description</th><th>Target Date</th><th>Status</th><th></th></tr></thead>
          <tbody>
            {goals.map((g) => (
              <tr key={g.id}>
                <td>{g.title}</td>
                <td>{g.description}</td>
                <td>{g.targetDate}</td>
                <td><span className="badge bg-info">{g.status}</span></td>
                <td>
                  {canManage && (
                    <>
                      <Link to={`/performance/goal/${g.id}/edit`} className="btn btn-sm btn-outline-primary me-2">Edit</Link>
                      <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(g.id)}>Delete</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
            {goals.length === 0 && <tr><td colSpan={5} className="text-muted text-center py-3">No goals set yet.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
