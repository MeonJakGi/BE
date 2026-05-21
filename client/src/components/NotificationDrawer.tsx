import { NotificationItem } from '@/types/dashboard';

interface Props {
  open: boolean;
  items: NotificationItem[];
  onClose: () => void;
  onRead: (notificationId: number) => void;
}

export default function NotificationDrawer({ open, items, onClose, onRead }: Props) {
  if (!open) return null;
  return (
    <aside className="notify-drawer">
      <h3>알림</h3>
      <button onClick={onClose}>닫기</button>
      <ul>
        {items.map((item) => (
          <li key={item.notificationId} style={{ marginBottom: 12 }}>
            <strong>{item.title}</strong>
            <div style={{ color: '#6b7280', fontSize: 13 }}>{item.message}</div>
            <button onClick={() => onRead(item.notificationId)} style={{ marginTop: 4 }}>
              읽음 처리
            </button>
          </li>
        ))}
      </ul>
    </aside>
  );
}
