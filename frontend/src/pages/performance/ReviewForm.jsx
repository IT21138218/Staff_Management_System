import { useEffect, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { performanceApi } from '../../api/performanceApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function ReviewForm() {
  const { id } = useParams();
  const isEditing = !!id;
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const employeeId = searchParams.get('employeeId');

  const [employee, setEmployee] = useState(null);
  const [form, setForm] = useState({ employeeId, reviewPeriod: '', score: 5, strengths: '', areasForImprovement: '', feedback: '' });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (isEditing) {
      performanceApi.getReview(id).then((r) => {
        setForm({
          employeeId: r.employee.id,
          reviewPeriod: r.reviewPeriod,
          score: r.score,
          strengths: r.strengths || '',
          areasForImprovement: r.areasForImprovement || '',
          feedback: r.feedback || '',
        });
        setEmployee(r.employee);
      }).catch(setError);
    } else if (employeeId) {
      performanceApi.employeeSummary(employeeId).then(setEmployee).catch(setError);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const payload = { ...form, score: Number(form.score) };
      if (isEditing) {
        await performanceApi.updateReview(id, payload);
      } else {
        await performanceApi.createReview(payload);
      }
      navigate(`/performance/reviews?employeeId=${form.employeeId}`);
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4">
        <i className="fa-solid fa-star"></i> {isEditing ? 'Edit Review' : 'New Review'}{employee && <> for {employee.firstName} {employee.lastName}</>}
      </h3>

      <div className="card card-stat" style={{ maxWidth: 650 }}>
        <div className="card-body">
          <ErrorAlert error={error} />
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Review Period</label>
              <input type="text" className="form-control" placeholder="e.g. 2026-H1" value={form.reviewPeriod} onChange={(e) => setForm({ ...form, reviewPeriod: e.target.value })} required />
            </div>
            <div className="mb-3">
              <label className="form-label">Score (1-5)</label>
              <input type="number" min={1} max={5} className="form-control" value={form.score} onChange={(e) => setForm({ ...form, score: e.target.value })} required />
            </div>
            <div className="mb-3">
              <label className="form-label">Strengths</label>
              <textarea className="form-control" rows={2} value={form.strengths} onChange={(e) => setForm({ ...form, strengths: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Areas for Improvement</label>
              <textarea className="form-control" rows={2} value={form.areasForImprovement} onChange={(e) => setForm({ ...form, areasForImprovement: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Feedback</label>
              <textarea className="form-control" rows={3} value={form.feedback} onChange={(e) => setForm({ ...form, feedback: e.target.value })} />
            </div>
            <button type="submit" className="btn btn-primary" disabled={submitting}>{isEditing ? 'Save Review' : 'Submit Review'}</button>
            <button type="button" className="btn btn-outline-secondary ms-2" onClick={() => navigate(`/performance/reviews?employeeId=${form.employeeId}`)}>Cancel</button>
          </form>
        </div>
      </div>
    </div>
  );
}
