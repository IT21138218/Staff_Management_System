import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ErrorAlert from '../components/common/ErrorAlert';
import logo from '../assets/logo.svg';

const emptyForm = { username: '', firstName: '', lastName: '', email: '', password: '', confirmPassword: '' };

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (form.password !== form.confirmPassword) {
      setError({ response: { data: { message: 'Password and confirmation do not match.' } } });
      return;
    }
    setSubmitting(true);
    try {
      await register(form);
      navigate('/dashboard');
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page d-flex align-items-center py-5">
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-6 col-lg-5">
            <div className="card shadow-lg border-0">
              <div className="card-body p-4">
                <div className="text-center mb-4">
                  <img src={logo} alt="" width={48} height={48} />
                  <h4 className="mt-2 mb-0">Create your account</h4>
                  <small className="text-muted">Sign up as an employee</small>
                </div>

                <ErrorAlert error={error} />

                <form onSubmit={handleSubmit}>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">First Name</label>
                      <input type="text" className="form-control" value={form.firstName} onChange={handleChange('firstName')} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Last Name</label>
                      <input type="text" className="form-control" value={form.lastName} onChange={handleChange('lastName')} required />
                    </div>
                    <div className="col-12">
                      <label className="form-label">Username</label>
                      <input type="text" className="form-control" value={form.username} onChange={handleChange('username')} required minLength={3} autoComplete="username" />
                    </div>
                    <div className="col-12">
                      <label className="form-label">Email</label>
                      <input type="email" className="form-control" value={form.email} onChange={handleChange('email')} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Password</label>
                      <input type="password" className="form-control" value={form.password} onChange={handleChange('password')} required minLength={6} autoComplete="new-password" />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Confirm Password</label>
                      <input type="password" className="form-control" value={form.confirmPassword} onChange={handleChange('confirmPassword')} required autoComplete="new-password" />
                    </div>
                  </div>

                  <button type="submit" className="btn btn-primary w-100 mt-4" disabled={submitting}>
                    <i className="fa-solid fa-user-plus"></i> {submitting ? 'Creating account...' : 'Create Account'}
                  </button>
                </form>

                <p className="text-center small text-muted mt-3 mb-0">
                  Already have an account? <Link to="/login">Sign in</Link>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
