import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { employeeApi } from '../../api/employeeApi';
import { departmentApi } from '../../api/departmentApi';
import ErrorAlert from '../../components/common/ErrorAlert';

const emptyForm = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  address: '',
  dateOfBirth: '',
  dateJoined: '',
  designation: '',
  departmentId: '',
  basicSalary: '',
  supervisorId: '',
  createLogin: false,
  username: '',
  role: 'EMPLOYEE',
};

export default function EmployeeForm() {
  const { id } = useParams();
  const isEditing = !!id;
  const navigate = useNavigate();

  const [form, setForm] = useState(emptyForm);
  const [departments, setDepartments] = useState([]);
  const [supervisors, setSupervisors] = useState([]);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    departmentApi.list().then(setDepartments).catch(() => {});
    employeeApi.supervisors().then(setSupervisors).catch(() => {});
    if (isEditing) {
      employeeApi.get(id).then((e) => {
        setForm({
          firstName: e.firstName,
          lastName: e.lastName,
          email: e.email,
          phone: e.phone || '',
          address: e.address || '',
          dateOfBirth: e.dateOfBirth || '',
          dateJoined: e.dateJoined || '',
          designation: e.designation || '',
          departmentId: e.department ? e.department.id : '',
          basicSalary: e.basicSalary,
          supervisorId: e.supervisor ? e.supervisor.id : '',
          createLogin: false,
          username: '',
          role: 'EMPLOYEE',
        });
      }).catch(setError);
    }
  }, [id, isEditing]);

  const handleChange = (field) => (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const payload = {
        ...form,
        departmentId: form.departmentId || null,
        supervisorId: form.supervisorId || null,
        basicSalary: form.basicSalary ? Number(form.basicSalary) : null,
      };
      if (isEditing) {
        await employeeApi.update(id, payload);
        navigate(`/employees/${id}`);
      } else {
        const created = await employeeApi.create(payload);
        navigate(`/employees/${created.id}`);
      }
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-user-pen"></i> {isEditing ? 'Edit Employee' : 'Add Employee'}</h3>

      <div className="card card-stat">
        <div className="card-body">
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
              <div className="col-md-6">
                <label className="form-label">Email</label>
                <input type="email" className="form-control" value={form.email} onChange={handleChange('email')} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Phone</label>
                <input type="text" className="form-control" value={form.phone} onChange={handleChange('phone')} />
              </div>
              <div className="col-12">
                <label className="form-label">Address</label>
                <input type="text" className="form-control" value={form.address} onChange={handleChange('address')} />
              </div>
              <div className="col-md-4">
                <label className="form-label">Date of Birth</label>
                <input type="date" className="form-control" value={form.dateOfBirth} onChange={handleChange('dateOfBirth')} />
              </div>
              <div className="col-md-4">
                <label className="form-label">Date Joined</label>
                <input type="date" className="form-control" value={form.dateJoined} onChange={handleChange('dateJoined')} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Basic Salary</label>
                <input type="number" step="0.01" className="form-control" value={form.basicSalary} onChange={handleChange('basicSalary')} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Designation</label>
                <input type="text" className="form-control" value={form.designation} onChange={handleChange('designation')} placeholder="e.g. Operations Assistant" />
              </div>
              <div className="col-md-6">
                <label className="form-label">Department</label>
                <select className="form-select" value={form.departmentId} onChange={handleChange('departmentId')}>
                  <option value="">-- None --</option>
                  {departments.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
                </select>
              </div>
              <div className="col-md-6">
                <label className="form-label">Supervisor</label>
                <select className="form-select" value={form.supervisorId} onChange={handleChange('supervisorId')}>
                  <option value="">-- None --</option>
                  {supervisors.map((s) => <option key={s.id} value={s.id}>{s.firstName} {s.lastName}</option>)}
                </select>
              </div>

              {!isEditing && (
                <div className="col-12">
                  <hr />
                  <div className="form-check mb-2">
                    <input className="form-check-input" type="checkbox" id="createLogin" checked={form.createLogin} onChange={handleChange('createLogin')} />
                    <label className="form-check-label" htmlFor="createLogin">Also create a portal login for this employee</label>
                  </div>
                  {form.createLogin && (
                    <div className="row g-3">
                      <div className="col-md-6">
                        <label className="form-label">Username</label>
                        <input type="text" className="form-control" value={form.username} onChange={handleChange('username')} />
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Role</label>
                        <select className="form-select" value={form.role} onChange={handleChange('role')}>
                          <option value="EMPLOYEE">Employee</option>
                          <option value="SUPERVISOR">Supervisor</option>
                          <option value="HR_MANAGER">HR Manager</option>
                          <option value="PAYROLL_OFFICER">Payroll Officer</option>
                          <option value="ADMIN">Administrator</option>
                        </select>
                      </div>
                      <div className="col-12">
                        <small className="text-muted">Temporary password will be &lt;EmployeeCode&gt;@123 - the employee should change it on first login.</small>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>

            <div className="mt-4">
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                <i className="fa-solid fa-floppy-disk"></i> Save
              </button>
              <button type="button" className="btn btn-outline-secondary ms-2" onClick={() => navigate('/employees')}>Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
