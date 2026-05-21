# BeShow Backend Handoff (2026-05-21)

## 1) 현재 구조
- Backend root: `/project/server`
- DB root: `/project/db`
- API prefix: `/api/v1`
- 상태값 기준:
  - `NORMAL`
  - `REPLENISH_REQUIRED`
  - `NEEDS_CHECK`
  - `ORDER_REQUIRED`

## 2) 백엔드 구현 현황
### 서버 엔트리
- `server/app/main.py`
  - `GET /health`
  - router 등록
    - `/api/v1/dashboard`
    - `/api/v1/tasks`
    - `/api/v1/notifications`

### DB 연결
- `server/app/db.py`
  - default `DATABASE_URL`
  - `mysql+pymysql://root:root1234@127.0.0.1:3307/beshow`

### 라우터
- `server/app/routers/dashboard.py`
  - `GET /dashboard/replenishment/summary`
  - `GET /dashboard/replenishment/items`
  - `GET /dashboard/replenishment/items/{sku_code}`
  - `GET /dashboard/order/summary`
  - `GET /dashboard/order/items`
  - `GET /dashboard/order/items/{sku_code}`
- `server/app/routers/tasks.py`
  - `POST /tasks/replenishment/{sku_code}/complete`
  - `POST /tasks/order/{sku_code}/complete`
- `server/app/routers/notifications.py`
  - `GET /notifications`
  - `POST /notifications/{notification_id}/read`
  - `POST /notifications/read-all`

## 3) DB 스키마/시드 현황
### 스키마
- `db/init/schema.sql`
- 테이블 12개:
  - `store`
  - `product`
  - `shelf`
  - `camera`
  - `slot`
  - `planogram`
  - `inventory`
  - `shelf_image`
  - `image_log`
  - `detection_result`
  - `stock`
  - `alarm`

### 시드
- `db/init/seed.sql`
- 제품 수: 61 SKU
- 슬롯 구조:
  - 선반 A: `4x5` (20 slots)
  - 선반 B: `3x4` (12 slots)

## 4) 로컬 실행 방법
### DB 실행
```bash
cd project/db
docker compose up -d
```

### 스키마/시드 주입
```bash
MYSQL_PWD='root1234' mysql -h 127.0.0.1 -P 3307 -u root beshow < init/schema.sql
MYSQL_PWD='root1234' mysql -h 127.0.0.1 -P 3307 -u root beshow < init/seed.sql
```

### 백엔드 실행
```bash
cd project/server
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 확인
- Swagger: `http://127.0.0.1:8000/docs`
- Health: `http://127.0.0.1:8000/health`

## 5) 다음 작업 우선순위
1. `dashboard.py` 필터 파라미터 정합성 개선
   - `shelf_id`/`status` 파싱 안전화
2. 작업 완료 API에 감사 로그(누가/언제) 남기기
3. SQL 분리
   - raw SQL을 service 계층으로 분리
4. 응답 스키마 엄격화
   - `schemas.py`에 endpoint별 response model 추가
5. 에러 처리 표준화
   - not found / invalid query / db error 핸들링

## 6) 프론트 연동 포인트
- 프론트 호출 파일: `client/src/api/dashboard.ts`
- 현재 연결되는 endpoint는 위 라우터와 1:1 매칭됨
- 필드명 snake_case -> camelCase 매핑은 프론트에서 수행 중

## 7) 참고
- 디자인/기능/화면 명세:
  - `docs/beshow_product_spec.md`
  - `docs/beshow_function_spec.md`
  - `docs/beshow_screen_spec.md`
- API 명세:
  - `docs/beshow_api_spec_mvp.md`
