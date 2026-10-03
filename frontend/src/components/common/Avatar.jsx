import { resolveImageUrl } from '../../api/imageUrl';

export default function Avatar({ photoUrl, name, size = 32 }) {
  const initial = (name || '?').trim().slice(0, 1).toUpperCase();
  const src = resolveImageUrl(photoUrl);

  if (src) {
    return (
      <img
        src={src}
        alt=""
        width={size}
        height={size}
        className="sms-avatar-img"
        style={{ width: size, height: size }}
      />
    );
  }

  return (
    <span className="sms-avatar" style={{ width: size, height: size, fontSize: size * 0.42 }}>
      {initial}
    </span>
  );
}
