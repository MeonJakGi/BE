import { useEffect, useMemo, useState } from 'react';
import DetailModal from '@/components/DetailModal';
import StatusBadge from '@/components/StatusBadge';
import { completeReplenishment, fetchReplenishmentItems, fetchReplenishmentSummary } from '@/api/dashboard';
import { ReplenishmentItem } from '@/types/dashboard';

interface Props {
  storeId?: number;
}

export default function ReplenishmentPage({ storeId = 1 }: Props) {
  const [selected, setSelected] = useState<ReplenishmentItem | null>(null);
  const [shelf, setShelf] = useState<'A' | 'B'>('A');
  const [items, setItems] = useState<ReplenishmentItem[]>([]);
  const [summary, setSummary] = useState({ normal_count: 0, replenish_required_count: 0, needs_check_count: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setLoading(true);
      setError(null);
      const [summaryData, itemsData] = await Promise.all([
        fetchReplenishmentSummary(storeId),
        fetchReplenishmentItems(storeId),
      ]);
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

  const filteredItems = useMemo(
    () => items.filter((item) => item.shelfLabel.includes(`선반 ${shelf}`)),
    [items, shelf],
  );

  const onComplete = async () => {
    if (!selected) return;
    await completeReplenishment(selected.skuCode, storeId);
    setSelected(null);
    await load();
  };

  return (
    <div className="page">
      <div className="card-row">
        <div className="card"><div>정상 항목 수</div><h2>{summary.normal_count}</h2></div>
        <div className="card"><div>보충 필요 항목 수</div><h2>{summary.replenish_required_count}</h2></div>
        <div className="card"><div>확인 필요 항목 수</div><h2>{summary.needs_check_count}</h2></div>
      </div>

      <div className="card" style={{ marginTop: 12 }}>
        <button onClick={() => setShelf('A')}>선반 A</button>
        <button onClick={() => setShelf('B')} style={{ marginLeft: 8 }}>선반 B</button>
        <button onClick={() => void load()} style={{ marginLeft: 8 }}>새로고침</button>
        <div style={{ marginTop: 12 }}>선택 선반: {shelf}</div>
      </div>

      {loading && <div style={{ marginTop: 12 }}>로딩 중...</div>}
      {error && <div style={{ marginTop: 12, color: '#c24141' }}>오류: {error}</div>}

      <div className="table-wrap">
        <div className="table-scroll">
          <table className="table">
            <thead>
              <tr>
                <th>우선순위</th><th>상품명</th><th>상품상태</th><th>창고 재고</th><th>위치</th><th>탐지 시각</th>
              </tr>
            </thead>
            <tbody>
              {filteredItems.map((item) => (
                <tr key={item.skuCode} onClick={() => setSelected(item)} style={{ cursor: 'pointer' }}>
                  <td>{item.priority}</td>
                  <td>{item.productName}</td>
                  <td><StatusBadge status={item.status} /></td>
                  <td>{item.warehouseQty}</td>
                  <td>{item.shelfLabel} {item.slotLabel}</td>
                  <td>{item.detectedAt}</td>
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
            <p>현재 창고 재고: {selected.warehouseQty}</p>
            <p>진열 위치: {selected.shelfLabel} {selected.slotLabel}</p>
            <div style={{ marginTop: 12 }}>
              <button onClick={() => void onComplete()}>보충 완료 처리</button>
            </div>
          </div>
        )}
      </DetailModal>
    </div>
  );
}
