import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { notificationApi } from '../../api/notificationApi';
import Avatar from '../common/Avatar';

export default function Topbar({ onToggleSidebar }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    let mounted = true;
    const fetchCount = () => {
      notificationApi
        .unreadCount()
        .then((count) => mounted && setUnreadCount(count))
        .catch(() => {});
    };
    fetchCount();
    const interval = setInterval(fetchCount, 30000);
    return () => {
      mounted = false;
      clearInterval(interval);
    };
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    setMenuOpen(false);
    logout();
    navigate('/login');
  };

  return (
    <header className="sms-topbar">
      <button className="sms-sidebar-toggle" onClick={onToggleSidebar} aria-label="Toggle navigation">
        <i className="fa-solid fa-bars"></i>
      </button>

      <div className="sms-topbar-spacer"></div>

      <Link to="/notifications" className="sms-icon-btn" aria-label="Notifications">
        <i className="fa-solid fa-bell"></i>
        {unreadCount > 0 && <span className="sms-badge-dot">{unreadCount}</span>}
      </Link>

      <div className="sms-user-menu" ref={menuRef}>
        <button className="sms-user-trigger" onClick={() => setMenuOpen((v) => !v)}>
          <Avatar photoUrl={user.photoUrl} name={user.fullName || user.username} size={32} />
          <span className="sms-user-info">
            <span className="sms-user-name">{user.username}</span>
            <span className="sms-user-role">{user.role.replace('_', ' ')}</span>
          </span>
          <i className="fa-solid fa-chevron-down sms-chevron"></i>
        </button>

        {menuOpen && (
          <div className="sms-user-dropdown">
            <Link to="/profile" className="sms-user-dropdown-item" onClick={() => setMenuOpen(false)}>
              <i className="fa-solid fa-user-gear"></i> Profile &amp; Password
            </Link>
            <div className="sms-user-dropdown-divider"></div>
            <button className="sms-user-dropdown-item" onClick={handleLogout}>
              <i className="fa-solid fa-right-from-bracket"></i> Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
