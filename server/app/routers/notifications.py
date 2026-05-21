from fastapi import APIRouter, Depends, Query
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.db import get_db
from app.schemas import ReadAllNotificationsRequest
from app.services.response import ok

router = APIRouter(prefix="/notifications", tags=["notifications"])


@router.get("")
def list_notifications(
    store_id: int = Query(...),
    is_read: bool | None = Query(default=None),
    page: int = Query(default=1, ge=1),
    size: int = Query(default=20, ge=1, le=100),
    db: Session = Depends(get_db),
):
    offset = (page - 1) * size
    params = {"store_id": store_id, "limit": size, "offset": offset}
    read_filter = ""
    if is_read is not None:
        read_filter = " AND a.alarm_status IN ('READ','RESOLVED')" if is_read else " AND a.alarm_status='UNREAD'"

    rows = db.execute(
        text(
            f"""
            SELECT
              a.alarm_id AS notification_id,
              a.alarm_type AS type,
              a.message AS title,
              CONCAT(sh.shelf_name, ' ', sl.slot_name, ' ', p.product_name) AS message,
              p.product_id AS sku_code,
              CASE WHEN a.alarm_type='ORDER_REQUIRED' THEN 'SCR-2' ELSE 'SCR-1' END AS target_screen,
              a.created_at,
              (a.alarm_status IN ('READ','RESOLVED')) AS is_read
            FROM alarm a
            JOIN shelf sh ON sh.shelf_id = a.shelf_id
            JOIN product p ON p.product_id = a.product_id
            LEFT JOIN stock st ON st.stock_id = a.stock_id
            LEFT JOIN slot sl ON sl.slot_id = st.slot_id
            WHERE a.store_id = :store_id
            {read_filter}
            ORDER BY a.created_at DESC
            LIMIT :limit OFFSET :offset
            """
        ),
        params,
    ).mappings().all()

    unread_count = db.execute(
        text("SELECT COUNT(*) AS cnt FROM alarm WHERE store_id=:store_id AND alarm_status='UNREAD'"),
        {"store_id": store_id},
    ).mappings().first()["cnt"]

    return ok({"unread_count": unread_count, "items": [dict(r) for r in rows]})


@router.post("/{notification_id}/read")
def read_notification(notification_id: int, db: Session = Depends(get_db)):
    db.execute(
        text(
            """
            UPDATE alarm
            SET alarm_status='READ', read_at=NOW()
            WHERE alarm_id=:notification_id
            """
        ),
        {"notification_id": notification_id},
    )
    db.commit()
    return ok({"notification_id": notification_id, "is_read": True})


@router.post("/read-all")
def read_all_notifications(payload: ReadAllNotificationsRequest, db: Session = Depends(get_db)):
    db.execute(
        text(
            """
            UPDATE alarm
            SET alarm_status='READ', read_at=NOW()
            WHERE store_id=:store_id AND alarm_status='UNREAD'
            """
        ),
        {"store_id": payload.store_id},
    )
    db.commit()
    return ok({"store_id": payload.store_id, "result": "ok"})
