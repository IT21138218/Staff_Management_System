// Uploaded photos are served at the API's origin under /uploads/**, not under
// the /api prefix axiosClient is based at - so build the origin separately.
const API_ORIGIN = import.meta.env.VITE_API_BASE_URL.replace(/\/api\/?$/, '');

export function resolveImageUrl(path) {
  if (!path) return null;
  return `${API_ORIGIN}${path}`;
}
