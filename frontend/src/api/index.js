import request from '../utils/request'

export const shelterApi = {
  findAll: () => request.get('/shelters'),
  findById: (id) => request.get(`/shelters/${id}`),
  save: (data) => request.post('/shelters', data),
  update: (id, data) => request.put(`/shelters/${id}`, data),
  delete: (id) => request.delete(`/shelters/${id}`)
}

export const socketApi = {
  findAll: () => request.get('/sockets'),
  findById: (id) => request.get(`/sockets/${id}`),
  findByShelterId: (shelterId) => request.get(`/sockets/shelter/${shelterId}`),
  save: (data) => request.post('/sockets', data),
  updateStatus: (id, status) => request.put(`/sockets/${id}/status/${status}`)
}

export const reservationApi = {
  findAll: () => request.get('/reservations'),
  findById: (id) => request.get(`/reservations/${id}`),
  findByUserId: (userId) => request.get(`/reservations/user/${userId}`),
  create: (data) => request.post('/reservations', data),
  checkIn: (id) => request.put(`/reservations/${id}/checkin`),
  cancel: (id) => request.put(`/reservations/${id}/cancel`),
  complete: (id) => request.put(`/reservations/${id}/complete`)
}

export const orderApi = {
  findAll: () => request.get('/orders'),
  findById: (id) => request.get(`/orders/${id}`),
  findByUserId: (userId) => request.get(`/orders/user/${userId}`),
  startCharging: (data) => request.post('/orders/start', data),
  stopCharging: (id) => request.put(`/orders/${id}/stop`),
  pay: (id) => request.put(`/orders/${id}/pay`)
}

export const alarmApi = {
  findAll: () => request.get('/alarms'),
  findById: (id) => request.get(`/alarms/${id}`),
  findByStatus: (status) => request.get(`/alarms/status/${status}`),
  create: (data) => request.post('/alarms', data),
  process: (id, data) => request.put(`/alarms/${id}/process`, data),
  resolve: (id, data) => request.put(`/alarms/${id}/resolve`, data),
  dismiss: (id, data) => request.put(`/alarms/${id}/dismiss`, data)
}

export const inspectionApi = {
  findAll: () => request.get('/inspections'),
  findById: (id) => request.get(`/inspections/${id}`),
  findByOfficerId: (officerId) => request.get(`/inspections/officer/${officerId}`),
  findHasIssues: () => request.get('/inspections/issues'),
  save: (data) => request.post('/inspections', data),
  resolve: (id, data) => request.put(`/inspections/${id}/resolve`, data)
}

export const complaintApi = {
  findAll: () => request.get('/complaints'),
  findById: (id) => request.get(`/complaints/${id}`),
  findByUserId: (userId) => request.get(`/complaints/user/${userId}`),
  create: (data) => request.post('/complaints', data),
  handle: (id, data) => request.put(`/complaints/${id}/handle`, data)
}

export const maintenanceApi = {
  findAll: () => request.get('/maintenance'),
  findById: (id) => request.get(`/maintenance/${id}`),
  findPending: () => request.get('/maintenance/pending'),
  create: (data) => request.post('/maintenance', data),
  complete: (id, data) => request.put(`/maintenance/${id}/complete`, data)
}

export const userApi = {
  findAll: () => request.get('/users'),
  findById: (id) => request.get(`/users/${id}`),
  findByRole: (role) => request.get(`/users/role/${role}`),
  findBlacklisted: () => request.get('/users/blacklist'),
  save: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  setBlacklist: (id, data) => request.put(`/users/${id}/blacklist`, data),
  login: (data) => request.post('/users/login', data)
}

export const feeRuleApi = {
  findAll: () => request.get('/fee-rules'),
  findActive: () => request.get('/fee-rules/active'),
  save: (data) => request.post('/fee-rules', data),
  update: (id, data) => request.put(`/fee-rules/${id}`, data),
  delete: (id) => request.delete(`/fee-rules/${id}`)
}

export const meterReadingApi = {
  findAll: () => request.get('/meter-readings'),
  findBySocketId: (socketId) => request.get(`/meter-readings/socket/${socketId}`),
  save: (data) => request.post('/meter-readings', data)
}
