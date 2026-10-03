import axiosClient from './axiosClient';

export const performanceApi = {
  employeeSummary: (id) => axiosClient.get(`/performance/employee/${id}`).then((res) => res.data),
  goals: (employeeId) => axiosClient.get('/performance/goals', { params: { employeeId } }).then((res) => res.data),
  createGoal: (form) => axiosClient.post('/performance/goal/new', form).then((res) => res.data),
  updateGoal: (id, form) => axiosClient.put(`/performance/goal/${id}`, form).then((res) => res.data),
  deleteGoal: (id) => axiosClient.delete(`/performance/goal/${id}`).then((res) => res.data),
  getGoal: (id) => axiosClient.get(`/performance/goal/${id}`).then((res) => res.data),
  reviews: (employeeId) => axiosClient.get('/performance/reviews', { params: { employeeId } }).then((res) => res.data),
  getReview: (id) => axiosClient.get(`/performance/review/${id}`).then((res) => res.data),
  createReview: (form) => axiosClient.post('/performance/review/new', form).then((res) => res.data),
  updateReview: (id, form) => axiosClient.put(`/performance/review/${id}`, form).then((res) => res.data),
  deleteReview: (id) => axiosClient.delete(`/performance/review/${id}`).then((res) => res.data),
};
