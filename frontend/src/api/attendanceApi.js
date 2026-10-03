import axiosClient from './axiosClient';

export const attendanceApi = {
  my: () => axiosClient.get('/attendance/my').then((res) => res.data),
  clockIn: () => axiosClient.post('/attendance/clock-in').then((res) => res.data),
  clockOut: () => axiosClient.post('/attendance/clock-out').then((res) => res.data),
  list: (params) => axiosClient.get('/attendance/list', { params }).then((res) => res.data),
};
