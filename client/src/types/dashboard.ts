export type StockStatus = 'NORMAL' | 'REPLENISH_REQUIRED' | 'NEEDS_CHECK' | 'ORDER_REQUIRED';

export interface ReplenishmentItem {
  skuCode: number;
  productName: string;
  status: Exclude<StockStatus, 'ORDER_REQUIRED'>;
  priority: number;
  warehouseQty: number;
  shelfLabel: string;
  slotLabel: string;
  detectedAt: string;
}

export interface OrderItem {
  skuCode: number;
  productName: string;
  status: 'ORDER_REQUIRED';
  statusChangedAt: string;
  warehouseQty: number;
  shelfLabel: string;
  slotLabel: string;
  isOrderCompleted: boolean;
}

export interface NotificationItem {
  notificationId: number;
  type: 'REPLENISH_REQUIRED' | 'NEEDS_CHECK' | 'ORDER_REQUIRED';
  title: string;
  message: string;
  skuCode: number;
  targetScreen: 'SCR-1' | 'SCR-2';
  createdAt: string;
  isRead: boolean;
}
