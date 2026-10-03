import { useEffect, useState } from 'react';
import { departmentApi } from '../../api/departmentApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function DepartmentList() {
  const [departments, setDepartments] = useState([]);
  const [form, setForm] = useState({ name: '', description: '' });
  const [error, setError] = useState(null);

  const load = () => departmentApi.list().then(setDepartments).catch(setError);

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await departmentApi.create(form);
      setForm({ name: '', description: '' });
      load();
    } catch (err) {
      setError(err);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this department? Employees assigned to it will need to be reassigned.')) return;
    try {
      await departmentApi.remove(id);
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-sitemap"></i> Departments</h3>

      <ErrorAlert error={error} />

      <div className="row g-4">
        <div className="col-md-5">
          <div className="card card-stat"><div className="card-body">
            <h5>Add Department</h5>
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Name</label>
                <input type="text" className="form-control" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Description</label>
                <textarea className="form-control" rows={2} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
              </div>
              <button type="submit" className="btn btn-primary">Add</button>
            </form>
          </div></div>
        </div>

        <div className="col-md-7">
          <div className="card card-stat"><div className="card-body">
            <table className="table table-hover">
              <thead><tr><th>Name</th><th>Description</th><th></th></tr></thead>
              <tbody>
                {departments.map((d) => (
                  <tr key={d.id}>
                    <td>{d.name}</td>
                    <td>{d.description}</td>
                    <td>
                      <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(d.id)}>Delete</button>
                    </td>
                  </tr>
                ))}
                {departments.length === 0 && (
                  <tr><td colSpan={3} className="text-muted text-center py-3">No departments yet.</td></tr>
                )}
              </tbody>
            </table>
          </div></div>
        </div>
      </div>
    </div>
  );
}
