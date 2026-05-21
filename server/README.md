## BeShow Backend (MVP)

### 1) Setup
```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
```

### 2) Run
```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 3) Docs
- Swagger: http://127.0.0.1:8000/docs
- Health: http://127.0.0.1:8000/health
