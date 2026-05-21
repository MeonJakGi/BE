import os
from fastapi import FastAPI

from app.routers import dashboard, notifications, tasks

app = FastAPI(
    title=os.getenv("APP_NAME", "BeShow API"),
    version=os.getenv("APP_VERSION", "0.1.0"),
)


@app.get("/health")
def health():
    return {"status": "ok"}


app.include_router(dashboard.router, prefix="/api/v1")
app.include_router(tasks.router, prefix="/api/v1")
app.include_router(notifications.router, prefix="/api/v1")
