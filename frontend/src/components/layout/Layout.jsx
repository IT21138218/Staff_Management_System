import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="sms-app-shell">
      <Sidebar open={sidebarOpen} onNavigate={() => setSidebarOpen(false)} />
      {sidebarOpen && <div className="sms-sidebar-backdrop" onClick={() => setSidebarOpen(false)}></div>}

      <div className="sms-main">
        <Topbar onToggleSidebar={() => setSidebarOpen((v) => !v)} />
        <main className="sms-content">
          <Outlet />
        </main>
        <footer className="app-footer text-center py-3">
          Staff Management System &middot; SE2030 Software Engineering Group Project &middot; SLIIT Faculty of Computing
        </footer>
      </div>
    </div>
  );
}
