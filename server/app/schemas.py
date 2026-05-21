from datetime import datetime
from typing import Any
from pydantic import BaseModel


class Meta(BaseModel):
    request_id: str = "local"
    timestamp: datetime


class SuccessResponse(BaseModel):
    success: bool = True
    data: Any
    meta: Meta


class CompleteTaskRequest(BaseModel):
    store_id: int
    operator_id: str


class ReadAllNotificationsRequest(BaseModel):
    store_id: int
