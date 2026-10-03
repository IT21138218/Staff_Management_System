import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { employeeApi } from '../../api/employeeApi';
import { departmentApi } from '../../api/departmentApi';
import Avatar from '../../components/common/Avatar';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [departmentId, setDepartmentId] = useState('');
  const [status, setStatus] = useState('');
  const [error, setError] = useState(null);

  const load = (params = {}) => {
    employeeApi.list(params).then(setEmployees).catch(setError);
  };

  useEffect(() => {
    load();
    departmentApi.list().then(setDepartments).catch(() => {});
  }, []);

  const handleFilter = (e) => {
    e.preventDefault();
    load({
      keyword: keyword || undefined,
      departmentId: departmentId || undefined,
      status: status || undefined,
    });
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-users"></i> Employees</h3>
        <Link to="/employees/new" className="btn btn-primary"><i className="fa-solid fa-plus"></i> Add Employee</Link>
      </div>

      <ErrorAlert error={error} />

      <div className="card card-stat mb-3">
        <div className="card-body">
          <form onSubmit={handleFilter} className="row g-2 align-items-end">
            <div className="col-md-4">
              <label className="form-label">Search (name or code)</label>
              <input type="text" className="form-control" value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="Search by name or employee code" />
            </div>
            <div className="col-md-3">
              <label className="form-label">Department</label>
              <select className="form-select" value={departmentId} onChange={(e) => setDepartmentId(e.target.value)}>
                <option value="">All</option>
                {departments.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">Status</label>
              <select className="form-select" value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="">All</option>
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
            <div className="col-md-2">
              <button type="submit" className="btn btn-outline-primary w-100"><i className="fa-solid fa-filter"></i> Filter</button>
            </div>
          </form>
        </div>
      </div>

      <div className="card card-stat">
        <div className="card-body">
          <table className="table table-hover align-middle">
            <thead>
              <tr><th></th><th>Code</th><th>Name</th><th>Department</th><th>Designation</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {employees.map((e) => (
                <tr key={e.id}>
                  <td><Avatar photoUrl={e.photoUrl} name={`${e.firstName} ${e.lastName}`} size={32} /></td>
                  <td>{e.employeeCode}</td>
                  <td>{e.firstName} {e.lastName}</td>
                  <td>{e.department ? e.department.name : '-'}</td>
                  <td>{e.designation}</td>
                  <td><span className={`badge ${e.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>{e.status}</span></td>
                  <td><Link to={`/employees/${e.id}`} className="btn btn-sm btn-outline-primary">View</Link></td>
                </tr>
              ))}
              {employees.length === 0 && (
                <tr><td colSpan={7} className="text-muted text-center py-4">No employees found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
