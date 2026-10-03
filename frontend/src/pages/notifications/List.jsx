import { useEffect, useState } from 'react';
import { notificationApi } from '../../api/notificationApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function NotificationList() {
  const [notifications, setNotifications] = useState([]);
  const [error, setError] = useState(null);

  const load = () => notificationApi.list().then(setNotifications).catch(setError);

  useEffect(() => { load(); }, []);

  const markRead = async (id) => {
    await notificationApi.markRead(id);
    load();
  };

  const markAllRead = async () => {
    await notificationApi.markAllRead();
    load();
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-bell"></i> Notifications</h3>
        <button className="btn btn-outline-secondary btn-sm" onClick={markAllRead}>Mark all as read</button>
      </div>

      <ErrorAlert error={error} />

      <div className="list-group">
        {notifications.map((n) => (
          <div
            key={n.id}
            className={`list-group-item d-flex justify-content-between align-items-start ${!n.read ? 'list-group-item-light border-start border-3 border-primary' : ''}`}
          >
            <div>
              <div className="fw-bold">{n.title}</div>
              <div>{n.message}</div>
              <small className="text-muted">{new Date(n.createdDate).toLocaleString()}</small>
            </div>
            {!n.read && (
              <button className="btn btn-sm btn-outline-primary" onClick={() => markRead(n.id)}>Mark read</button>
            )}
          </div>
        ))}
        {notifications.length === 0 && <div className="text-muted text-center py-4">No notifications yet.</div>}
      </div>
    </div>
  );
}
