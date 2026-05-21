import { useEffect, useMemo, useState } from 'react';
import DetailModal from '@/components/DetailModal';
import StatusBadge from '@/components/StatusBadge';
import { completeOrder, fetchOrderItems, fetchOrderSummary } from '@/api/dashboard';
import { OrderItem } from '@/types/dashboard';

interface Props {
  storeId?: number;
}

export default function OrderPage({ storeId = 1 }: Props) {
  const [selected, setSelected] = useState<OrderItem | null>(null);
  const [items, setItems] = useState<OrderItem[]>([]);
  const [summary, setSummary] = useState({ order_required_sku_count: 0, sold_out_sku_count: 0, category_distribution: [] as Array<{ category: string; count: number }> });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setLoading(true);
      setError(null);
      const [summaryData, itemsData] = await Promise.all([fetchOrderSummary(storeId), fetchOrderItems(storeId)]);
      setSummary(summaryData);
      setItems(itemsData);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'unknown error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [storeId]);

  const categoryText = useMemo(
    () => summary.category_distribution.map((x) => `${x.category} ${x.count}`).join(' / '),
    [summary.category_distribution],
  );

  const onComplete = async () => {
    if (!selected) return;
    await completeOrder(selected.skuCode, storeId);
    setSelected(null);
    await load();
  };

  return (
    <div className="page">
      <div className="card-row" style={{ gridTemplateColumns: '1fr 1fr 2fr' }}>
        <div className="card"><div>발주 필요 SKU</div><h2>{summary.order_required_sku_count} 건</h2></div>
        <div className="card"><div>전량 소진 SKU</div><h2>{summary.sold_out_sku_count} 건</h2></div>
        <div className="card"><div>카테고리 분포</div><p>{categoryText}</p></div>
      </div>

      {loading && <div style={{ marginTop: 12 }}>로딩 중...</div>}
      {error && <div style={{ marginTop: 12, color: '#c24141' }}>오류: {error}</div>}

      <div className="table-wrap" style={{ marginTop: 16 }}>
        <div className="table-scroll">
          <table className="table">
            <thead>
              <tr>
                <th>SKU ID</th><th>상품명</th><th>상태 전환 시각</th><th>창고 재고</th><th>진열 위치</th><th>발주 완료 처리</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.skuCode} onClick={() => setSelected(item)} style={{ cursor: 'pointer' }}>
                  <td>{item.skuCode}</td>
                  <td>{item.productName}</td>
                  <td>{item.statusChangedAt}</td>
                  <td>{item.warehouseQty}</td>
                  <td>{item.shelfLabel} {item.slotLabel}</td>
                  <td><input type="checkbox" checked={item.isOrderCompleted} readOnly /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <DetailModal open={!!selected} title="SKU Detail" onClose={() => setSelected(null)}>
        {selected && (
          <div>
            <p>{selected.productName}</p>
            <p>SKU ID: {selected.skuCode}</p>
            <p>현재 상태: <StatusBadge status={selected.status} /></p>
            <p>상태 전환 시각: {selected.statusChangedAt}</p>
            <p>현재 창고 재고: {selected.warehouseQty}</p>
            <p>진열 위치: {selected.shelfLabel} {selected.slotLabel}</p>
            <div style={{ marginTop: 12 }}>
              <button onClick={() => void onComplete()}>발주 완료 처리</button>
            </div>
          </div>
        )}
      </DetailModal>
    </div>
  );
}
