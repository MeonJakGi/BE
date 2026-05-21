from fastapi import APIRouter, Depends
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.db import get_db
from app.schemas import CompleteTaskRequest
from app.services.response import ok

router = APIRouter(prefix="/tasks", tags=["tasks"])


@router.post("/replenishment/{sku_code}/complete")
def complete_replenishment(sku_code: int, payload: CompleteTaskRequest, db: Session = Depends(get_db)):
    row = db.execute(
        text(
            """
            UPDATE stock st
            JOIN shelf sh ON sh.shelf_id = st.shelf_id
            SET st.status = 'NORMAL', st.reason = 'MANUAL_REPLENISHED', st.checked_at = NOW()
            WHERE st.product_id = :sku_code
              AND sh.store_id = :store_id
              AND st.status IN ('REPLENISH_REQUIRED','NEEDS_CHECK')
            """
        ),
        {"sku_code": sku_code, "store_id": payload.store_id},
    )
    db.commit()
    return ok(
        {
            "sku_code": sku_code,
            "task_status": "COMPLETED",
            "updated_rows": row.rowcount,
        }
    )


@router.post("/order/{sku_code}/complete")
def complete_order(sku_code: int, payload: CompleteTaskRequest, db: Session = Depends(get_db)):
    db.execute(
        text(
            """
            UPDATE alarm a
            JOIN stock st ON st.stock_id = a.stock_id
            JOIN shelf sh ON sh.shelf_id = st.shelf_id
            SET a.alarm_status = 'RESOLVED', a.resolved_at = NOW()
            WHERE st.product_id = :sku_code
              AND sh.store_id = :store_id
              AND st.status = 'ORDER_REQUIRED'
            """
        ),
        {"sku_code": sku_code, "store_id": payload.store_id},
    )
    db.commit()
    return ok({"sku_code": sku_code, "task_status": "COMPLETED"})
