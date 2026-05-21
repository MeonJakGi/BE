# BeShow Project Workspace

## 구조
- `server/`: FastAPI 백엔드 API
- `client/`: 프론트엔드 앱(React 예정)
- `db/`: MySQL 도커 및 초기 SQL
- `docs/`: 제품/화면/기능/API 명세

## DB 실행
```bash
cd db
docker compose up -d
```

## DB 초기화 SQL
- `db/init/schema.sql`
- `db/init/seed.sql`

## 서버 실행
```bash
cd server
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

## API 문서
- `http://127.0.0.1:8000/docs`
