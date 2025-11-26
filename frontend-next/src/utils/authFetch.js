// Authenticated fetch utility for attaching JWT token
export async function authFetch(url, options = {}) {
  const token = sessionStorage.getItem('jwt_token');
  const headers = {
    ...(options.headers || {}),
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
  return fetch(url, { ...options, headers });
}