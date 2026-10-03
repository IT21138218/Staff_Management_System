import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { performanceApi } from '../../api/performanceApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function Reviews() {
  const { user } = useAuth();
  const [searchParams] = useSearchParams();
  const employeeId = searchParams.get('employeeId') || undefined;
  const [reviews, setReviews] = useState([]);
  const [error, setError] = useState(null);

  const loadReviews = () => performanceApi.reviews(employeeId).then(setReviews).catch(setError);

  useEffect(() => {
    loadReviews();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [employeeId]);

  const canManage = ['SUPERVISOR', 'HR_MANAGER'].includes(user.role);
  const targetEmployeeId = employeeId || user.employeeId;

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this performance review? This cannot be undone.')) return;
    setError(null);
    try {
      await performanceApi.deleteReview(id);
      loadReviews();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-star"></i> Performance Reviews</h3>
        {canManage && (
          <Link to={`/performance/review/new?employeeId=${targetEmployeeId}`} className="btn btn-primary">New Review</Link>
        )}
      </div>

      <ErrorAlert error={error} />

      <div className="row g-3">
        {reviews.map((r) => (
          <div className="col-md-6" key={r.id}>
            <div className="card card-stat"><div className="card-body">
              <h5>{r.reviewPeriod}</h5>
              <p>Score: <strong>{r.score} / 5</strong></p>
              <p><strong>Strengths:</strong> {r.strengths}</p>
              <p><strong>Areas for Improvement:</strong> {r.areasForImprovement}</p>
              <p><strong>Feedback:</strong> {r.feedback}</p>
              <p className="text-muted small">Reviewed on {r.reviewDate}</p>
              {canManage && (
                <div>
                  <Link to={`/performance/review/${r.id}/edit?employeeId=${targetEmployeeId}`} className="btn btn-sm btn-outline-primary me-2">Edit</Link>
                  <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(r.id)}>Delete</button>
                </div>
              )}
            </div></div>
          </div>
        ))}
        {reviews.length === 0 && (
          <div className="col-12"><p className="text-muted">No performance reviews yet.</p></div>
        )}
      </div>
    </div>
  );
}
