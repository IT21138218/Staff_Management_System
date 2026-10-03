import axiosClient from './axiosClient';

export const departmentApi = {
  list: () => axiosClient.get('/departments').then((res) => res.data),
  create: (form) => axiosClient.post('/departments', form).then((res) => res.data),
  update: (id, form) => axiosClient.put(`/departments/${id}`, form).then((res) => res.data),
  remove: (id) => axiosClient.delete(`/departments/${id}`).then((res) => res.data),
};
