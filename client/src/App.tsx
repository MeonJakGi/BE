import { useEffect, useMemo, useState } from 'react';
import NotificationDrawer from '@/components/NotificationDrawer';
import OrderPage from '@/pages/OrderPage';
import ReplenishmentPage from '@/pages/ReplenishmentPage';
import { fetchNotifications, readNotification } from '@/api/dashboard';
import { NotificationItem } from '@/types/dashboard';

export default function App() {
  const [tab, setTab] = useState<'SCR-1' | 'SCR-2'>('SCR-1');
  const [openNotify, setOpenNotify] = useState(false);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const loadNotifications = async () => {
    try {
      const data = await fetchNotifications(1);
      setNotifications(data.items);
      setUnreadCount(data.unreadCount);
    } catch {
      setNotifications([]);
      setUnreadCount(0);
    }
  };

  useEffect(() => {
    void loadNotifications();
  }, []);

  const bellLabel = useMemo(() => (unreadCount > 0 ? `🔔 ${unreadCount}` : '🔔'), [unreadCount]);

  const onReadNotification = async (notificationId: number) => {
    await readNotification(notificationId);
    await loadNotifications();
  };

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">Be:SHOW</div>
        <nav className="tabs">
          <button className={`tab ${tab === 'SCR-1' ? 'active' : ''}`} onClick={() => setTab('SCR-1')}>보충 필요 리스트</button>
          <button className={`tab ${tab === 'SCR-2' ? 'active' : ''}`} onClick={() => setTab('SCR-2')}>발주 필요 리스트</button>
        </nav>
        <div>
          <button onClick={() => setOpenNotify(true)}>{bellLabel}</button>
        </div>
      </header>

      {tab === 'SCR-1' ? <ReplenishmentPage storeId={1} /> : <OrderPage storeId={1} />}
      <NotificationDrawer
        open={openNotify}
        items={notifications}
        onClose={() => setOpenNotify(false)}
        onRead={onReadNotification}
      />
    </div>
  );
}
