import { useEffect, useRef, useState } from 'react';
import { profileApi } from '../../api/profileApi';
import { useAuth } from '../../context/AuthContext';
import Avatar from '../../components/common/Avatar';
import ErrorAlert, { extractErrorMessage } from '../../components/common/ErrorAlert';

const emptyPasswordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };

export default function Profile() {
  const { updatePhotoUrl } = useAuth();
  const [profile, setProfile] = useState(null);
  const [passwordForm, setPasswordForm] = useState(emptyPasswordForm);
  const [passwordError, setPasswordError] = useState(null);
  const [passwordChanged, setPasswordChanged] = useState(false);
  const [error, setError] = useState(null);
  const [photoError, setPhotoError] = useState(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  useEffect(() => {
    profileApi.get().then(setProfile).catch(setError);
  }, []);

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordChanged(false);
    try {
      await profileApi.changePassword(passwordForm);
      setPasswordChanged(true);
      setPasswordForm(emptyPasswordForm);
    } catch (err) {
      setPasswordError(extractErrorMessage(err));
    }
  };

  const handlePhotoChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setPhotoError(null);
    setUploading(true);
    try {
      const updatedEmployee = await profileApi.uploadPhoto(file);
      setProfile((prev) => ({ ...prev, employee: updatedEmployee }));
      updatePhotoUrl(updatedEmployee.photoUrl);
    } catch (err) {
      setPhotoError(extractErrorMessage(err));
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  if (error) return <ErrorAlert error={error} />;
  if (!profile) return <p className="text-muted">Loading...</p>;

  const { user, employee } = profile;

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-id-card"></i> My Profile</h3>

      <div className="row g-4">
        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <div className="d-flex align-items-center gap-3 mb-3">
              <Avatar photoUrl={employee?.photoUrl} name={employee ? `${employee.firstName} ${employee.lastName}` : user.username} size={64} />
              <div>
                <button
                  type="button"
                  className="btn btn-outline-primary btn-sm"
                  onClick={() => fileInputRef.current?.click()}
                  disabled={uploading}
                >
                  <i className="fa-solid fa-camera"></i> {uploading ? 'Uploading...' : 'Change Photo'}
                </button>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/jpeg,image/png,image/webp"
                  className="d-none"
                  onChange={handlePhotoChange}
                />
                {photoError && <div className="text-danger small mt-1">{photoError}</div>}
              </div>
            </div>

            <h5 className="card-title">Account</h5>
            <table className="table table-borderless mb-0">
              <tbody>
                <tr><th>Username</th><td>{user.username}</td></tr>
                <tr><th>Email</th><td>{user.email}</td></tr>
                <tr><th>Role</th><td><span className="badge badge-role">{user.role}</span></td></tr>
              </tbody>
            </table>

            {employee && (
              <>
                <hr />
                <h5 className="card-title">Employee Record</h5>
                <table className="table table-borderless mb-0">
                  <tbody>
                    <tr><th>Employee Code</th><td>{employee.employeeCode}</td></tr>
                    <tr><th>Full Name</th><td>{employee.firstName} {employee.lastName}</td></tr>
                    <tr><th>Department</th><td>{employee.department ? employee.department.name : '-'}</td></tr>
                    <tr><th>Designation</th><td>{employee.designation}</td></tr>
                    <tr><th>Date Joined</th><td>{employee.dateJoined}</td></tr>
                  </tbody>
                </table>
              </>
            )}
          </div></div>
        </div>

        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <h5 className="card-title"><i className="fa-solid fa-key"></i> Change Password</h5>

            {passwordChanged && <div className="alert alert-success py-2">Password updated successfully.</div>}
            {passwordError && <div className="alert alert-danger py-2">{passwordError}</div>}

            <form onSubmit={handleChangePassword}>
              <div className="mb-3">
                <label className="form-label">Current Password</label>
                <input type="password" className="form-control" value={passwordForm.currentPassword} onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })} required />
              </div>
              <div className="mb-3">
                <label className="form-label">New Password</label>
                <input type="password" className="form-control" value={passwordForm.newPassword} onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })} required minLength={6} />
              </div>
              <div className="mb-3">
                <label className="form-label">Confirm New Password</label>
                <input type="password" className="form-control" value={passwordForm.confirmPassword} onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })} required />
              </div>
              <button type="submit" className="btn btn-primary">Update Password</button>
            </form>
          </div></div>
        </div>
      </div>
    </div>
  );
}
