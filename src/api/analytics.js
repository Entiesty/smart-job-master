import request from '@/utils/request'

export function getAnalyticsData() {
  return request({
    url: '/analytics/data',
    method: 'get'
  })
}

export function getUserStats() {
  return request({
    url: '/analytics/user-stats',
    method: 'get'
  })
}

export function getJobStats() {
  return request({
    url: '/analytics/job-stats',
    method: 'get'
  })
}