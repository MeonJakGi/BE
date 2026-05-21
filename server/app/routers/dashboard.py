from fastapi import APIRouter, Depends, Query
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.db import get_db
from app.services.response import ok

router = APIRouter(prefix="/dashboard", tags=["dashboard"])


@router.get("/replenishment/summary")
def replenishment_summary(
    store_id: int = Query(...),
    shelf_id: str | None = Query(default="all"),
    db: Session = Depends(get_db),
):
    params = {"store_id": store_id}
    shelf_filter = ""
    if shelf_id and shelf_id != "all":
        shelf_filter = "AND sh.shelf_name = :shelf_name"
        params["shelf_name"] = shelf_id.replace("shelf", "선반 ").replace("A", "A").replace("B", "B")

    q = text(
        f"""
        SELECT
          SUM(CASE WHEN st.status='NORMAL' THEN 1 ELSE 0 END) AS normal_count,
          SUM(CASE WHEN st.status='REPLENISH_REQUIRED' THEN 1 ELSE 0 END) AS replenish_required_count,
          SUM(CASE WHEN st.status='NEEDS_CHECK' THEN 1 ELSE 0 END) AS needs_check_count,
          MAX(st.checked_at) AS last_updated_at
        FROM stock st
        JOIN shelf sh ON sh.shelf_id = st.shelf_id
        WHERE sh.store_id = :store_id
        {shelf_filter}
          AND st.status IN ('NORMAL','REPLENISH_REQUIRED','NEEDS_CHECK')
        """
    )
    row = db.execute(q, params).mappings().first()
    return ok(dict(row) if row else {})


@router.get("/replenishment/items")
def replenishment_items(
    store_id: int = Query(...),
    shelf_id: str = Query(default="all"),
    q: str | None = Query(default=None),
    status: str | None = Query(default=None),
    page: int = Query(default=1, ge=1),
    size: int = Query(default=20, ge=1, le=100),
    db: Session = Depends(get_db),
):
    offset = (page - 1) * size
    params = {"store_id": store_id, "limit": size, "offset": offset}

    shelf_filter = ""
    if shelf_id != "all":
        shelf_filter = " AND sh.shelf_name = :shelf_name"
        params["shelf_name"] = shelf_id.replace("shelf", "선반 ")

    name_filter = ""
    if q:
        name_filter = " AND p.product_name LIKE :name"
        params["name"] = f"%{q}%"

    status_filter = ""
    if status:
        requested = [s.strip() for s in status.split(",") if s.strip()]
        if requested:
            status_filter = " AND st.status IN :statuses"
            params["statuses"] = tuple(requested)

    base = f"""
      FROM stock st
      JOIN shelf sh ON sh.shelf_id = st.shelf_id
      JOIN product p ON p.product_id = st.product_id
      LEFT JOIN slot sl ON sl.slot_id = st.slot_id
      LEFT JOIN inventory inv ON inv.product_id = st.product_id AND inv.store_id = sh.store_id
      WHERE sh.store_id = :store_id
        AND st.status IN ('NORMAL','REPLENISH_REQUIRED','NEEDS_CHECK')
        {shelf_filter}
        {name_filter}
    """

    if status_filter:
        # fallback because IN tuple binding differs by driver
        safe = ",".join([f"'{s}'" for s in requested])
        base += f" AND st.status IN ({safe})"

    rows = db.execute(
        text(
            f"""
            SELECT
              p.product_id AS sku_code,
              p.product_name,
              st.status,
              ROW_NUMBER() OVER (ORDER BY FIELD(st.status,'REPLENISH_REQUIRED','NEEDS_CHECK','NORMAL'), st.checked_at DESC) AS priority,
              COALESCE(inv.current_qty,0) AS warehouse_qty,
              sh.shelf_name AS shelf_label,
              sl.slot_name AS slot_label,
              st.checked_at AS detected_at
            {base}
            ORDER BY FIELD(st.status,'REPLENISH_REQUIRED','NEEDS_CHECK','NORMAL'), st.checked_at DESC
            LIMIT :limit OFFSET :offset
            """
        ),
        params,
    ).mappings().all()

    total = db.execute(text(f"SELECT COUNT(*) AS cnt {base}"), params).mappings().first()["cnt"]
    return ok({"items": [dict(r) for r in rows], "pagination": {"page": page, "size": size, "total": total}})


@router.get("/replenishment/items/{sku_code}")
def replenishment_item_detail(sku_code: int, store_id: int = Query(...), db: Session = Depends(get_db)):
    row = db.execute(
        text(
            """
            SELECT
              p.product_id AS sku_code,
              p.product_name,
              st.status,
              NULL AS product_image_url,
              COALESCE(inv.current_qty,0) AS warehouse_qty,
              st.detected_qty,
              (SELECT AVG(dr.confidence) FROM detection_result dr WHERE dr.product_id = p.product_id) AS confidence,
              st.checked_at AS detected_at,
              sh.shelf_name AS shelf_label,
              sl.slot_name AS slot_label,
              st.reason
            FROM stock st
            JOIN product p ON p.product_id = st.product_id
            JOIN shelf sh ON sh.shelf_id = st.shelf_id
            LEFT JOIN slot sl ON sl.slot_id = st.slot_id
            LEFT JOIN inventory inv ON inv.product_id = p.product_id AND inv.store_id = :store_id
            WHERE p.product_id = :sku_code
              AND sh.store_id = :store_id
              AND st.status IN ('NORMAL','REPLENISH_REQUIRED','NEEDS_CHECK')
            ORDER BY st.checked_at DESC
            LIMIT 1
            """
        ),
        {"sku_code": sku_code, "store_id": store_id},
    ).mappings().first()
    return ok(dict(row) if row else {})


@router.get("/order/summary")
def order_summary(store_id: int = Query(...), db: Session = Depends(get_db)):
    row = db.execute(
        text(
            """
            SELECT
              SUM(CASE WHEN st.status='ORDER_REQUIRED' THEN 1 ELSE 0 END) AS order_required_sku_count,
              SUM(CASE WHEN st.status='ORDER_REQUIRED' AND st.detected_qty=0 THEN 1 ELSE 0 END) AS sold_out_sku_count,
              MAX(st.checked_at) AS last_updated_at
            FROM stock st
            JOIN shelf sh ON sh.shelf_id = st.shelf_id
            WHERE sh.store_id = :store_id
            """
        ),
        {"store_id": store_id},
    ).mappings().first()

    dist = db.execute(
        text(
            """
            SELECT p.category, COUNT(*) AS count
            FROM stock st
            JOIN shelf sh ON sh.shelf_id = st.shelf_id
            JOIN product p ON p.product_id = st.product_id
            WHERE sh.store_id = :store_id AND st.status='ORDER_REQUIRED'
            GROUP BY p.category
            ORDER BY count DESC
            """
        ),
        {"store_id": store_id},
    ).mappings().all()

    data = dict(row) if row else {}
    data["category_distribution"] = [dict(r) for r in dist]
    return ok(data)


@router.get("/order/items")
def order_items(
    store_id: int = Query(...),
    q: str | None = Query(default=None),
    category: str | None = Query(default=None),
    is_completed: bool | None = Query(default=None),
    page: int = Query(default=1, ge=1),
    size: int = Query(default=20, ge=1, le=100),
    db: Session = Depends(get_db),
):
    offset = (page - 1) * size
    params = {"store_id": store_id, "limit": size, "offset": offset}
    filters = ""
    if q:
        filters += " AND p.product_name LIKE :name"
        params["name"] = f"%{q}%"
    if category:
        filters += " AND p.category = :category"
        params["category"] = category
    if is_completed is not None:
        filters += " AND COALESCE(a.alarm_status,'UNREAD') = :alarm_status"
        params["alarm_status"] = "RESOLVED" if is_completed else "UNREAD"

    base = f"""
      FROM stock st
      JOIN shelf sh ON sh.shelf_id = st.shelf_id
      JOIN product p ON p.product_id = st.product_id
      LEFT JOIN slot sl ON sl.slot_id = st.slot_id
      LEFT JOIN inventory inv ON inv.product_id = st.product_id AND inv.store_id = sh.store_id
      LEFT JOIN alarm a ON a.stock_id = st.stock_id
      WHERE sh.store_id = :store_id
        AND st.status = 'ORDER_REQUIRED'
        {filters}
    """

    rows = db.execute(
        text(
            f"""
            SELECT
              p.product_id AS sku_code,
              p.product_name,
              st.status,
              st.checked_at AS status_changed_at,
              COALESCE(inv.current_qty,0) AS warehouse_qty,
              sh.shelf_name AS shelf_label,
              sl.slot_name AS slot_label,
              (COALESCE(a.alarm_status,'UNREAD')='RESOLVED') AS is_order_completed
            {base}
            ORDER BY st.checked_at DESC
            LIMIT :limit OFFSET :offset
            """
        ),
        params,
    ).mappings().all()

    total = db.execute(text(f"SELECT COUNT(*) AS cnt {base}"), params).mappings().first()["cnt"]
    return ok({"items": [dict(r) for r in rows], "pagination": {"page": page, "size": size, "total": total}})


@router.get("/order/items/{sku_code}")
def order_item_detail(sku_code: int, store_id: int = Query(...), db: Session = Depends(get_db)):
    row = db.execute(
        text(
            """
            SELECT
              p.product_id AS sku_code,
              p.product_name,
              st.status,
              NULL AS product_image_url,
              st.checked_at AS status_changed_at,
              COALESCE(inv.current_qty,0) AS warehouse_qty,
              (SELECT AVG(dr.confidence) FROM detection_result dr WHERE dr.product_id = p.product_id) AS confidence,
              sh.shelf_name AS shelf_label,
              sl.slot_name AS slot_label,
              2 AS lead_time_days,
              st.reason AS order_reason
            FROM stock st
            JOIN product p ON p.product_id = st.product_id
            JOIN shelf sh ON sh.shelf_id = st.shelf_id
            LEFT JOIN slot sl ON sl.slot_id = st.slot_id
            LEFT JOIN inventory inv ON inv.product_id = p.product_id AND inv.store_id = :store_id
            WHERE p.product_id = :sku_code
              AND sh.store_id = :store_id
              AND st.status = 'ORDER_REQUIRED'
            ORDER BY st.checked_at DESC
            LIMIT 1
            """
        ),
        {"sku_code": sku_code, "store_id": store_id},
    ).mappings().first()
    return ok(dict(row) if row else {})
