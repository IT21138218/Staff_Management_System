import axiosClient from './axiosClient';

export const notificationApi = {
  list: () => axiosClient.get('/notifications').then((res) => res.data),
  unreadCount: () => axiosClient.get('/notifications/unread-count').then((res) => res.data.count),
  markRead: (id) => axiosClient.post(`/notifications/${id}/read`).then((res) => res.data),
  markAllRead: () => axiosClient.post('/notifications/read-all').then((res) => res.data),
};
