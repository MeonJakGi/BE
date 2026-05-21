# 비었쇼 MVP API 명세서 (개발용)

## 1. 공통 규칙
- Base URL: `/api/v1`
- Content-Type: `application/json`
- 시간 포맷: `YYYY-MM-DDTHH:mm:ssZ` (ISO-8601)
- 상태값(enum)
  - `NORMAL`
  - `REPLENISH_REQUIRED`
  - `NEEDS_CHECK`
  - `ORDER_REQUIRED`
- 화면 노출 정책
  - SCR-1: `NORMAL`, `REPLENISH_REQUIRED`, `NEEDS_CHECK`
  - SCR-2: `ORDER_REQUIRED` only

---

## 2. 공통 응답 형식

### 2.1 성공
```json
{
  "success": true,
  "data": {},
  "meta": {
    "request_id": "req_123",
    "timestamp": "2026-05-20T01:00:00Z"
  }
}
```

### 2.2 실패
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "invalid query parameter"
  },
  "meta": {
    "request_id": "req_123",
    "timestamp": "2026-05-20T01:00:00Z"
  }
}
```

---

## 3. SCR-1 보충 필요 리스트

## 3.1 Overview 카드
`GET /dashboard/replenishment/summary`

### Query
- `store_id` (required)
- `shelf_id` (optional, `all | shelfA | shelfB`)

### Response
```json
{
  "success": true,
  "data": {
    "normal_count": 45,
    "replenish_required_count": 12,
    "needs_check_count": 2,
    "last_updated_at": "2026-05-20T01:24:00Z"
  }
}
```

## 3.2 선반 이미지 + 탐지 오버레이
`GET /dashboard/replenishment/shelf-image`

### Query
- `store_id` (required)
- `shelf_id` (required: `shelfA | shelfB`)

### Response
```json
{
  "success": true,
  "data": {
    "image_id": 991,
    "image_url": "https://...presigned-url",
    "captured_at": "2026-05-20T01:24:00Z",
    "overlays": [
      {
        "sku_code": "22013",
        "status": "NEEDS_CHECK",
        "confidence": 0.95,
        "bbox": {"x_min": 0.62, "y_min": 0.43, "x_max": 0.70, "y_max": 0.58}
      }
    ]
  }
}
```

## 3.3 작업 필요 리스트 (한 SKU 1행)
`GET /dashboard/replenishment/items`

### Query
- `store_id` (required)
- `shelf_id` (optional, default `all`)
- `q` (optional, SKU명 검색)
- `status` (optional: `REPLENISH_REQUIRED,NEEDS_CHECK,NORMAL`)
- `page` (optional, default 1)
- `size` (optional, default 20)

### Response
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "sku_code": "22013",
        "product_name": "농심 짜파게티 큰사발 123G",
        "status": "NEEDS_CHECK",
        "priority": 1,
        "warehouse_qty": 25,
        "shelf_label": "선반 A",
        "slot_label": "3-3",
        "detected_at": "2026-05-20T01:24:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 12
    }
  }
}
```

## 3.4 SCR-1.1 상세 모달
`GET /dashboard/replenishment/items/{sku_code}`

### Path
- `sku_code` (required)

### Query
- `store_id` (required)

### Response
```json
{
  "success": true,
  "data": {
    "sku_code": "22013",
    "product_name": "농심 짜파게티 큰사발 123G",
    "status": "NEEDS_CHECK",
    "product_image_url": "https://...",
    "warehouse_qty": 25,
    "detected_qty": 1,
    "confidence": 0.95,
    "detected_at": "2026-05-20T01:24:00Z",
    "shelf_label": "선반 A",
    "slot_label": "3-3",
    "reason": "LOW_CONFIDENCE"
  }
}
```

## 3.5 보충 완료 처리
`POST /tasks/replenishment/{sku_code}/complete`

### Path
- `sku_code` (required)

### Body
```json
{
  "store_id": 1,
  "operator_id": "staff_001"
}
```

### Response
```json
{
  "success": true,
  "data": {
    "sku_code": "22013",
    "task_status": "COMPLETED",
    "completed_at": "2026-05-20T01:30:00Z"
  }
}
```

---

## 4. SCR-2 발주 필요 리스트

## 4.1 요약 카드
`GET /dashboard/order/summary`

### Query
- `store_id` (required)

### Response
```json
{
  "success": true,
  "data": {
    "order_required_sku_count": 25,
    "sold_out_sku_count": 5,
    "category_distribution": [
      {"category": "과자", "count": 9},
      {"category": "면류", "count": 6},
      {"category": "상온", "count": 5},
      {"category": "통조림_안주", "count": 3},
      {"category": "기타", "count": 2}
    ],
    "last_updated_at": "2026-05-20T01:24:00Z"
  }
}
```

## 4.2 발주 필요 리스트 (한 SKU 1행)
`GET /dashboard/order/items`

### Query
- `store_id` (required)
- `q` (optional)
- `category` (optional)
- `is_completed` (optional: `true | false`)
- `page` (optional)
- `size` (optional)

### Response
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "sku_code": "22013",
        "product_name": "농심 짜파게티 큰사발 123G",
        "status": "ORDER_REQUIRED",
        "status_changed_at": "2026-05-20T01:24:00Z",
        "warehouse_qty": 3,
        "shelf_label": "선반 A",
        "slot_label": "3-3",
        "is_order_completed": false
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 25
    }
  }
}
```

## 4.3 SCR-2.1 상세 모달
`GET /dashboard/order/items/{sku_code}`

### Query
- `store_id` (required)

### Response
```json
{
  "success": true,
  "data": {
    "sku_code": "22013",
    "product_name": "농심 짜파게티 큰사발 123G",
    "status": "ORDER_REQUIRED",
    "product_image_url": "https://...",
    "status_changed_at": "2026-05-20T01:24:00Z",
    "warehouse_qty": 3,
    "confidence": 0.95,
    "shelf_label": "선반 A",
    "slot_label": "3-3",
    "lead_time_days": 2,
    "order_reason": "BELOW_ROP"
  }
}
```

## 4.4 발주 완료 처리
`POST /tasks/order/{sku_code}/complete`

### Body
```json
{
  "store_id": 1,
  "operator_id": "owner_001"
}
```

### Response
```json
{
  "success": true,
  "data": {
    "sku_code": "22013",
    "task_status": "COMPLETED",
    "completed_at": "2026-05-20T01:32:00Z"
  }
}
```

---

## 5. SCR-opt 알림 패널

## 5.1 알림 목록 조회
`GET /notifications`

### Query
- `store_id` (required)
- `is_read` (optional)
- `page` (optional)
- `size` (optional)

### Response
```json
{
  "success": true,
  "data": {
    "unread_count": 4,
    "items": [
      {
        "notification_id": 501,
        "type": "ORDER_REQUIRED",
        "title": "매대상 재고 전부 소진",
        "message": "선반 B 3-2 오뚜기3분쇠고기짜장200G",
        "sku_code": "50138",
        "target_screen": "SCR-2",
        "created_at": "2026-05-20T01:20:00Z",
        "is_read": false
      }
    ]
  }
}
```

## 5.2 알림 읽음 처리
`POST /notifications/{notification_id}/read`

### Response
```json
{
  "success": true,
  "data": {
    "notification_id": 501,
    "is_read": true,
    "read_at": "2026-05-20T01:25:00Z"
  }
}
```

## 5.3 알림 전체 읽음 처리
`POST /notifications/read-all`

### Body
```json
{
  "store_id": 1
}
```

---

## 6. 참조 코드(에러)
- `VALIDATION_ERROR` (400)
- `UNAUTHORIZED` (401)
- `FORBIDDEN` (403)
- `NOT_FOUND` (404)
- `CONFLICT` (409)
- `INTERNAL_ERROR` (500)

---

## 7. 백엔드 구현 필수 조건
1. 리스트 API는 반드시 SKU 중복 제거 후 반환한다.
2. SCR-1/SCR-2 상태 노출 정책을 API 레벨에서 보장한다.
3. 상세 API는 모달 렌더링에 필요한 필드를 누락 없이 반환한다.
4. 알림 API는 `target_screen`을 함께 내려 프론트 라우팅을 단순화한다.
