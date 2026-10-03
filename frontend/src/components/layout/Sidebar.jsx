import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import logo from '../../assets/logo.svg';

function buildSections(user) {
  const canManageEmployees = ['ADMIN', 'HR_MANAGER'].includes(user.role);
  const canApproveLeave = ['SUPERVISOR', 'HR_MANAGER'].includes(user.role);
  const isPayrollOfficer = user.role === 'PAYROLL_OFFICER';
  const canManageRoster = ['SUPERVISOR', 'ADMIN'].includes(user.role);
  const canSeeReports = ['ADMIN', 'HR_MANAGER', 'PAYROLL_OFFICER'].includes(user.role);
  const canMonitorAttendance = ['ADMIN', 'HR_MANAGER', 'SUPERVISOR'].includes(user.role);

  const sections = [
    {
      items: [{ to: '/dashboard', icon: 'fa-gauge-high', label: 'Dashboard' }],
    },
  ];

  if (canManageEmployees) {
    sections.push({
      label: 'People',
      items: [
        { to: '/employees', icon: 'fa-users', label: 'Employees' },
        { to: '/departments', icon: 'fa-sitemap', label: 'Departments' },
      ],
    });
  }

  sections.push({
    label: 'Attendance',
    items: [
      { to: '/attendance/my', icon: 'fa-clock', label: 'My Attendance' },
      ...(canMonitorAttendance ? [{ to: '/attendance/team', icon: 'fa-users-rectangle', label: 'Team Attendance' }] : []),
    ],
  });

  sections.push({
    label: 'Leave',
    items: [
      { to: '/leave/my', icon: 'fa-calendar-days', label: 'My Leave' },
      { to: '/leave/apply', icon: 'fa-calendar-plus', label: 'Apply for Leave' },
      ...(canApproveLeave ? [{ to: '/leave/approvals', icon: 'fa-clipboard-check', label: 'Approvals' }] : []),
    ],
  });

  sections.push({
    label: 'Payroll',
    items: [
      { to: '/payroll/my', icon: 'fa-file-invoice-dollar', label: 'My Payslips' },
      ...(isPayrollOfficer
        ? [
            { to: '/payroll/generate', icon: 'fa-calculator', label: 'Generate Payslip' },
            { to: '/payroll/all', icon: 'fa-money-check-dollar', label: 'All Payslips' },
          ]
        : []),
    ],
  });

  sections.push({
    label: 'Performance',
    items: [
      { to: '/performance/goals', icon: 'fa-bullseye', label: 'Goals' },
      { to: '/performance/reviews', icon: 'fa-star', label: 'Reviews' },
    ],
  });

  sections.push({
    label: 'Schedule',
    items: [
      { to: '/schedule/my', icon: 'fa-calendar-week', label: 'My Shifts' },
      ...(canManageRoster
        ? [
            { to: '/schedule/manage', icon: 'fa-calendar-days', label: 'Manage Roster' },
            { to: '/schedule/swaps', icon: 'fa-arrows-rotate', label: 'Swap Requests' },
          ]
        : []),
    ],
  });

  if (canSeeReports) {
    sections.push({
      items: [{ to: '/reports', icon: 'fa-chart-column', label: 'Reports' }],
    });
  }

  return sections;
}

export default function Sidebar({ open, onNavigate }) {
  const { user } = useAuth();
  const sections = buildSections(user);

  return (
    <aside className={`sms-sidebar ${open ? 'sms-sidebar-open' : ''}`}>
      <div className="sms-sidebar-brand">
        <img src={logo} alt="" width={30} height={30} />
        <span>Staff Management</span>
      </div>

      <nav className="sms-sidebar-nav">
        {sections.map((section, idx) => (
          <div className="sms-nav-section" key={idx}>
            {section.label && <div className="sms-nav-section-label">{section.label}</div>}
            {section.items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={onNavigate}
                className={({ isActive }) => `sms-nav-item${isActive ? ' active' : ''}`}
              >
                <i className={`fa-solid ${item.icon}`}></i>
                <span>{item.label}</span>
              </NavLink>
            ))}
          </div>
        ))}
      </nav>
    </aside>
  );
}
