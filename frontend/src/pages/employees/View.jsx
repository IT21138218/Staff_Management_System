import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { employeeApi } from '../../api/employeeApi';
import Avatar from '../../components/common/Avatar';
import ErrorAlert, { extractErrorMessage } from '../../components/common/ErrorAlert';

export default function EmployeeView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [employee, setEmployee] = useState(null);
  const [error, setError] = useState(null);
  const [photoError, setPhotoError] = useState(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  const load = () => employeeApi.get(id).then(setEmployee).catch(setError);

  useEffect(() => { load(); }, [id]);

  const toggleStatus = async () => {
    if (employee.status === 'ACTIVE') {
      if (!window.confirm('Deactivate this employee?')) return;
      await employeeApi.deactivate(id);
    } else {
      await employeeApi.activate(id);
    }
    load();
  };

  const handlePhotoChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setPhotoError(null);
    setUploading(true);
    try {
      const updated = await employeeApi.uploadPhoto(id, file);
      setEmployee(updated);
    } catch (err) {
      setPhotoError(extractErrorMessage(err));
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  if (error) return <ErrorAlert error={error} />;
  if (!employee) return <p className="text-muted">Loading...</p>;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div className="d-flex align-items-center gap-3">
          <Avatar photoUrl={employee.photoUrl} name={`${employee.firstName} ${employee.lastName}`} size={56} />
          <h3 className="mb-0"><i className="fa-solid fa-id-badge"></i> {employee.firstName} {employee.lastName}</h3>
        </div>
        <div>
          <button className="btn btn-outline-secondary me-2" onClick={() => fileInputRef.current?.click()} disabled={uploading}>
            <i className="fa-solid fa-camera"></i> {uploading ? 'Uploading...' : 'Photo'}
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/webp"
            className="d-none"
            onChange={handlePhotoChange}
          />
          <button className="btn btn-outline-primary me-2" onClick={() => navigate(`/employees/${id}/edit`)}>Edit</button>
          <button
            className={`btn ${employee.status === 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}`}
            onClick={toggleStatus}
          >
            {employee.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
          </button>
        </div>
      </div>

      {photoError && <div className="alert alert-danger py-2">{photoError}</div>}

      <div className="row g-3">
        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <table className="table table-borderless mb-0">
              <tbody>
                <tr><th style={{ width: '40%' }}>Employee Code</th><td>{employee.employeeCode}</td></tr>
                <tr><th>Email</th><td>{employee.email}</td></tr>
                <tr><th>Phone</th><td>{employee.phone || '-'}</td></tr>
                <tr><th>Address</th><td>{employee.address || '-'}</td></tr>
                <tr><th>Date of Birth</th><td>{employee.dateOfBirth || '-'}</td></tr>
              </tbody>
            </table>
          </div></div>
        </div>
        <div className="col-md-6">
          <div className="card card-stat"><div className="card-body">
            <table className="table table-borderless mb-0">
              <tbody>
                <tr><th style={{ width: '40%' }}>Designation</th><td>{employee.designation || '-'}</td></tr>
                <tr><th>Department</th><td>{employee.department ? employee.department.name : '-'}</td></tr>
                <tr><th>Date Joined</th><td>{employee.dateJoined}</td></tr>
                <tr><th>Supervisor</th><td>{employee.supervisor ? `${employee.supervisor.firstName} ${employee.supervisor.lastName}` : '-'}</td></tr>
                <tr><th>Basic Salary</th><td>{employee.basicSalary}</td></tr>
                <tr><th>Status</th><td><span className={`badge ${employee.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>{employee.status}</span></td></tr>
                <tr><th>Portal Login</th><td>{employee.user ? employee.user.username : 'No login created'}</td></tr>
              </tbody>
            </table>
          </div></div>
        </div>
      </div>

      <Link to="/employees" className="btn btn-link mt-3">&laquo; Back to Employees</Link>
    </div>
  );
}
