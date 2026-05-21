import { getJson, postJson } from './client';
import type { NotificationItem, OrderItem, ReplenishmentItem } from '@/types/dashboard';

interface ReplenishmentItemsResponse {
  items: Array<{
    sku_code: number;
    product_name: string;
    status: 'NORMAL' | 'REPLENISH_REQUIRED' | 'NEEDS_CHECK';
    priority: number;
    warehouse_qty: number;
    shelf_label: string;
    slot_label: string;
    detected_at: string;
  }>;
}

interface ReplenishmentSummaryResponse {
  normal_count: number;
  replenish_required_count: number;
  needs_check_count: number;
}

interface OrderItemsResponse {
  items: Array<{
    sku_code: number;
    product_name: string;
    status: 'ORDER_REQUIRED';
    status_changed_at: string;
    warehouse_qty: number;
    shelf_label: string;
    slot_label: string;
    is_order_completed: boolean;
  }>;
}

interface OrderSummaryResponse {
  order_required_sku_count: number;
  sold_out_sku_count: number;
  category_distribution: Array<{ category: string; count: number }>;
}

interface NotificationsResponse {
  unread_count: number;
  items: Array<{
    notification_id: number;
    type: 'REPLENISH_REQUIRED' | 'NEEDS_CHECK' | 'ORDER_REQUIRED';
    title: string;
    message: string;
    sku_code: number;
    target_screen: 'SCR-1' | 'SCR-2';
    created_at: string;
    is_read: boolean;
  }>;
}

const mapReplenishmentItem = (x: ReplenishmentItemsResponse['items'][number]): ReplenishmentItem => ({
  skuCode: x.sku_code,
  productName: x.product_name,
  status: x.status,
  priority: x.priority,
  warehouseQty: x.warehouse_qty,
  shelfLabel: x.shelf_label,
  slotLabel: x.slot_label,
  detectedAt: x.detected_at,
});

const mapOrderItem = (x: OrderItemsResponse['items'][number]): OrderItem => ({
  skuCode: x.sku_code,
  productName: x.product_name,
  status: x.status,
  statusChangedAt: x.status_changed_at,
  warehouseQty: x.warehouse_qty,
  shelfLabel: x.shelf_label,
  slotLabel: x.slot_label,
  isOrderCompleted: x.is_order_completed,
});

const mapNotificationItem = (x: NotificationsResponse['items'][number]): NotificationItem => ({
  notificationId: x.notification_id,
  type: x.type,
  title: x.title,
  message: x.message,
  skuCode: x.sku_code,
  targetScreen: x.target_screen,
  createdAt: x.created_at,
  isRead: x.is_read,
});

export async function fetchReplenishmentSummary(storeId = 1): Promise<ReplenishmentSummaryResponse> {
  return getJson<ReplenishmentSummaryResponse>(`/dashboard/replenishment/summary?store_id=${storeId}`);
}

export async function fetchReplenishmentItems(storeId = 1): Promise<ReplenishmentItem[]> {
  const data = await getJson<ReplenishmentItemsResponse>(`/dashboard/replenishment/items?store_id=${storeId}`);
  return data.items.map(mapReplenishmentItem);
}

export async function completeReplenishment(skuCode: number, storeId = 1, operatorId = 'staff_001') {
  return postJson(`/tasks/replenishment/${skuCode}/complete`, { store_id: storeId, operator_id: operatorId });
}

export async function fetchOrderSummary(storeId = 1): Promise<OrderSummaryResponse> {
  return getJson<OrderSummaryResponse>(`/dashboard/order/summary?store_id=${storeId}`);
}

export async function fetchOrderItems(storeId = 1): Promise<OrderItem[]> {
  const data = await getJson<OrderItemsResponse>(`/dashboard/order/items?store_id=${storeId}`);
  return data.items.map(mapOrderItem);
}

export async function completeOrder(skuCode: number, storeId = 1, operatorId = 'owner_001') {
  return postJson(`/tasks/order/${skuCode}/complete`, { store_id: storeId, operator_id: operatorId });
}

export async function fetchNotifications(storeId = 1): Promise<{ unreadCount: number; items: NotificationItem[] }> {
  const data = await getJson<NotificationsResponse>(`/notifications?store_id=${storeId}`);
  return {
    unreadCount: data.unread_count,
    items: data.items.map(mapNotificationItem),
  };
}

export async function readNotification(notificationId: number) {
  return postJson(`/notifications/${notificationId}/read`, {});
}

export async function readAllNotifications(storeId = 1) {
  return postJson('/notifications/read-all', { store_id: storeId });
}
