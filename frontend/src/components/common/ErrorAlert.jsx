export function extractErrorMessage(error) {
  return error?.response?.data?.message || 'Something went wrong. Please try again.';
}

export default function ErrorAlert({ error }) {
  if (!error) return null;
  return <div className="alert alert-danger py-2">{extractErrorMessage(error)}</div>;
}
