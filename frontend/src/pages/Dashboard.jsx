import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { dashboardApi } from '../api/dashboardApi';
import StatCard from '../components/common/StatCard';
import ErrorAlert from '../components/common/ErrorAlert';

export default function Dashboard() {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    dashboardApi.get().then(setData).catch(setError);
  }, []);

  if (error) return <ErrorAlert error={error} />;
  if (!data) return <p className="text-muted">Loading...</p>;

  const name = data.employee ? `${data.employee.firstName} ${data.employee.lastName}` : user.username;

  return (
    <div>
      <h3 className="mb-1">Welcome, {name}</h3>
      <p className="text-muted mb-4">
        Here's what's happening today &mdash;{' '}
        {new Date().toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
      </p>

      {user.role === 'ADMIN' && (
        <div className="row g-3">
          <div className="col-md-4"><StatCard value={data.totalEmployees} label="Total Employees" /></div>
          <div className="col-md-4"><StatCard value={data.totalDepartments} label="Departments" /></div>
          <div className="col-md-4"><StatCard value={data.pendingLeaveCount} label="Pending Leave Requests" /></div>
          <div className="col-12 mt-3">
            <Link to="/employees" className="btn btn-outline-primary me-2">Manage Employees</Link>
            <Link to="/departments" className="btn btn-outline-secondary">Manage Departments</Link>
          </div>
        </div>
      )}

      {user.role === 'HR_MANAGER' && (
        <div className="row g-3">
          <div className="col-md-4"><StatCard value={data.totalEmployees} label="Total Employees" /></div>
          <div className="col-md-4"><StatCard value={data.activeEmployees} label="Active Employees" /></div>
          <div className="col-md-4"><StatCard value={data.pendingLeaveCount} label="Pending Leave (Org-wide)" /></div>
          <div className="col-12 mt-3">
            <Link to="/leave/approvals" className="btn btn-outline-primary me-2">Review Leave Requests</Link>
            <Link to="/reports" className="btn btn-outline-secondary">Reports</Link>
          </div>
        </div>
      )}

      {user.role === 'SUPERVISOR' && (
        <div className="row g-3">
          <div className="col-md-4"><StatCard value={data.teamSize} label="Team Members" /></div>
          <div className="col-md-4"><StatCard value={data.pendingLeaveForTeam} label="Pending Leave Requests" /></div>
          <div className="col-md-4"><StatCard value={data.pendingSwaps} label="Pending Shift Swaps" /></div>
          <div className="col-12 mt-3">
            <Link to="/leave/approvals" className="btn btn-outline-primary me-2">Approve Leave</Link>
            <Link to="/schedule/manage" className="btn btn-outline-secondary me-2">Manage Roster</Link>
            <Link to="/performance/goals" className="btn btn-outline-success">Performance</Link>
          </div>
        </div>
      )}

      {user.role === 'PAYROLL_OFFICER' && (
        <div className="row g-3">
          <div className="col-md-6"><StatCard value={data.payslipsThisMonth} label="Payslips Generated This Month" /></div>
          <div className="col-md-6"><StatCard value={data.activeEmployees} label="Active Employees" /></div>
          <div className="col-12 mt-3">
            <Link to="/payroll/generate" className="btn btn-outline-primary me-2">Generate Payslip</Link>
            <Link to="/payroll/all" className="btn btn-outline-secondary">View All Payslips</Link>
          </div>
        </div>
      )}

      {user.role === 'EMPLOYEE' && (
        <div>
          <div className="row g-3 mb-3">
            <div className="col-md-4">
              <div className="card card-stat"><div className="card-body">
                <h6 className="text-muted">Today's Attendance</h6>
                {!data.todayAttendance && <span className="text-danger">Not clocked in yet</span>}
                {data.todayAttendance && (
                  <div>
                    <div>In: <strong>{new Date(data.todayAttendance.clockIn).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</strong></div>
                    {data.todayAttendance.clockOut && (
                      <div>Out: <strong>{new Date(data.todayAttendance.clockOut).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</strong></div>
                    )}
                    <span className="badge bg-info">{data.todayAttendance.status}</span>
                  </div>
                )}
                <Link to="/attendance/my" className="btn btn-sm btn-outline-primary mt-2 d-block">Go to Attendance</Link>
              </div></div>
            </div>
            <div className="col-md-8">
              <div className="card card-stat"><div className="card-body">
                <h6 className="text-muted">Leave Balances ({new Date().getFullYear()})</h6>
                <table className="table table-sm mb-0">
                  <thead><tr><th>Type</th><th>Allocated</th><th>Used</th><th>Remaining</th></tr></thead>
                  <tbody>
                    {(data.leaveBalances || []).map((b) => (
                      <tr key={b.id}>
                        <td>{b.leaveType.name}</td>
                        <td>{b.allocatedDays}</td>
                        <td>{b.usedDays}</td>
                        <td>{b.remainingDays}</td>
                      </tr>
                    ))}
                    {(!data.leaveBalances || data.leaveBalances.length === 0) && (
                      <tr><td colSpan={4} className="text-muted">No leave balance records yet.</td></tr>
                    )}
                  </tbody>
                </table>
              </div></div>
            </div>
          </div>

          <div className="row g-3">
            <div className="col-md-6">
              <div className="card card-stat"><div className="card-body">
                <h6 className="text-muted">Recent Leave Requests</h6>
                <table className="table table-sm mb-0">
                  <tbody>
                    {(data.myRecentLeave || []).map((r) => (
                      <tr key={r.id}>
                        <td>{r.leaveType.name}</td>
                        <td>{r.startDate} to {r.endDate}</td>
                        <td><span className="badge bg-secondary">{r.status}</span></td>
                      </tr>
                    ))}
                    {(!data.myRecentLeave || data.myRecentLeave.length === 0) && (
                      <tr><td className="text-muted">No leave requests yet.</td></tr>
                    )}
                  </tbody>
                </table>
                <Link to="/leave/apply" className="btn btn-sm btn-outline-primary mt-2">Apply for Leave</Link>
              </div></div>
            </div>
            <div className="col-md-6">
              <div className="card card-stat"><div className="card-body">
                <h6 className="text-muted">Upcoming Shifts (next 14 days)</h6>
                <table className="table table-sm mb-0">
                  <tbody>
                    {(data.myUpcomingShifts || []).map((s) => (
                      <tr key={s.id}>
                        <td>{s.rosterDate}</td>
                        <td>{s.shift.name}</td>
                        <td>{s.shift.startTime} - {s.shift.endTime}</td>
                      </tr>
                    ))}
                    {(!data.myUpcomingShifts || data.myUpcomingShifts.length === 0) && (
                      <tr><td className="text-muted">No shifts scheduled.</td></tr>
                    )}
                  </tbody>
                </table>
                <Link to="/schedule/my" className="btn btn-sm btn-outline-primary mt-2">View Schedule</Link>
              </div></div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
