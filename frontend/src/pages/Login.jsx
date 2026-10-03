import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ErrorAlert from '../components/common/ErrorAlert';
import logo from '../assets/logo.svg';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(username, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page d-flex align-items-center">
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-5">
            <div className="card shadow-lg border-0">
              <div className="card-body p-4">
                <div className="text-center mb-4">
                  <img src={logo} alt="" width={48} height={48} />
                  <h4 className="mt-2 mb-0">Staff Management System</h4>
                  <small className="text-muted">Sign in to continue</small>
                </div>

                <ErrorAlert error={error} />

                <form onSubmit={handleSubmit}>
                  <div className="mb-3">
                    <label className="form-label">Username</label>
                    <input
                      type="text"
                      className="form-control"
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                      required
                      autoFocus
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Password</label>
                    <input
                      type="password"
                      className="form-control"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                    />
                  </div>
                  <button type="submit" className="btn btn-primary w-100" disabled={submitting}>
                    <i className="fa-solid fa-right-to-bracket"></i> {submitting ? 'Signing in...' : 'Login'}
                  </button>
                </form>

                <p className="text-center small text-muted mt-3 mb-0">
                  Don't have an account? <Link to="/register">Create one</Link>
                </p>

                <hr />
                <p className="small text-muted mb-1"><strong>Demo logins:</strong></p>
                <ul className="small text-muted mb-0">
                  <li>admin / Admin@123 &mdash; System Administrator</li>
                  <li>hr.manager / Hr@12345 &mdash; HR Manager</li>
                  <li>supervisor / Super@123 &mdash; Supervisor</li>
                  <li>payroll.officer / Payroll@123 &mdash; Payroll Officer</li>
                  <li>employee / Employee@123 &mdash; Employee</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
