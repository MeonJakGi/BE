import { ReactNode } from 'react';

interface Props {
  open: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
}

export default function DetailModal({ open, title, onClose, children }: Props) {
  if (!open) return null;
  return (
    <div className="modal-scrim" onClick={onClose}>
      <section className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        {children}
        <div style={{ marginTop: 16 }}>
          <button onClick={onClose}>닫기</button>
        </div>
      </section>
    </div>
  );
}
