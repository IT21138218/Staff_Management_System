import axiosClient from './axiosClient';

export const leaveApi = {
  my: () => axiosClient.get('/leave/my').then((res) => res.data),
  types: () => axiosClient.get('/leave/types').then((res) => res.data),
  apply: (form) => axiosClient.post('/leave/apply', form).then((res) => res.data),
  cancel: (id) => axiosClient.post(`/leave/${id}/cancel`).then((res) => res.data),
  approvals: () => axiosClient.get('/leave/approvals').then((res) => res.data),
  decide: (form) => axiosClient.post('/leave/decide', form).then((res) => res.data),
};
