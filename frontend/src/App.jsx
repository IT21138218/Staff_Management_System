import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/layout/Layout';
import ProtectedRoute from './components/layout/ProtectedRoute';

import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Reports from './pages/Reports';

import EmployeeList from './pages/employees/List';
import EmployeeForm from './pages/employees/Form';
import EmployeeView from './pages/employees/View';

import DepartmentList from './pages/departments/List';

import MyAttendance from './pages/attendance/My';
import TeamAttendance from './pages/attendance/TeamList';

import ApplyLeave from './pages/leave/Apply';
import MyLeaveRequests from './pages/leave/MyRequests';
import LeaveApprovals from './pages/leave/Approvals';

import MyPayslips from './pages/payroll/My';
import GeneratePayroll from './pages/payroll/Generate';
import AllPayslips from './pages/payroll/AllPayslips';
import PayslipView from './pages/payroll/PayslipView';

import Goals from './pages/performance/Goals';
import GoalForm from './pages/performance/GoalForm';
import Reviews from './pages/performance/Reviews';
import ReviewForm from './pages/performance/ReviewForm';

import MyShifts from './pages/schedule/MyShifts';
import ManageRoster from './pages/schedule/ManageRoster';
import SwapRequests from './pages/schedule/SwapRequests';

import NotificationList from './pages/notifications/List';
import Profile from './pages/profile/Profile';

const ADMIN_HR = ['ADMIN', 'HR_MANAGER'];
const SUPERVISOR_HR = ['SUPERVISOR', 'HR_MANAGER'];
const SUPERVISOR_ADMIN = ['SUPERVISOR', 'ADMIN'];
const REPORT_ROLES = ['ADMIN', 'HR_MANAGER', 'PAYROLL_OFFICER'];

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />

          <Route element={<ProtectedRoute allowedRoles={ADMIN_HR} />}>
            <Route path="/employees" element={<EmployeeList />} />
            <Route path="/employees/new" element={<EmployeeForm />} />
            <Route path="/employees/:id/edit" element={<EmployeeForm />} />
            <Route path="/employees/:id" element={<EmployeeView />} />
            <Route path="/departments" element={<DepartmentList />} />
          </Route>

          <Route path="/attendance/my" element={<MyAttendance />} />
          <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'HR_MANAGER', 'SUPERVISOR']} />}>
            <Route path="/attendance/team" element={<TeamAttendance />} />
          </Route>

          <Route path="/leave/my" element={<MyLeaveRequests />} />
          <Route path="/leave/apply" element={<ApplyLeave />} />
          <Route element={<ProtectedRoute allowedRoles={SUPERVISOR_HR} />}>
            <Route path="/leave/approvals" element={<LeaveApprovals />} />
          </Route>

          <Route path="/payroll/my" element={<MyPayslips />} />
          <Route element={<ProtectedRoute allowedRoles={['PAYROLL_OFFICER']} />}>
            <Route path="/payroll/generate" element={<GeneratePayroll />} />
            <Route path="/payroll/all" element={<AllPayslips />} />
          </Route>
          <Route path="/payroll/:id" element={<PayslipView />} />

          <Route path="/performance/goals" element={<Goals />} />
          <Route path="/performance/reviews" element={<Reviews />} />
          <Route element={<ProtectedRoute allowedRoles={SUPERVISOR_HR} />}>
            <Route path="/performance/goal/new" element={<GoalForm />} />
            <Route path="/performance/goal/:id/edit" element={<GoalForm />} />
            <Route path="/performance/review/new" element={<ReviewForm />} />
            <Route path="/performance/review/:id/edit" element={<ReviewForm />} />
          </Route>

          <Route path="/schedule/my" element={<MyShifts />} />
          <Route element={<ProtectedRoute allowedRoles={SUPERVISOR_ADMIN} />}>
            <Route path="/schedule/manage" element={<ManageRoster />} />
            <Route path="/schedule/swaps" element={<SwapRequests />} />
          </Route>

          <Route element={<ProtectedRoute allowedRoles={REPORT_ROLES} />}>
            <Route path="/reports" element={<Reports />} />
          </Route>

          <Route path="/notifications" element={<NotificationList />} />
          <Route path="/profile" element={<Profile />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
