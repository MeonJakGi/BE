from datetime import datetime, timezone


def ok(data):
    return {
        "success": True,
        "data": data,
        "meta": {
            "request_id": "local",
            "timestamp": datetime.now(timezone.utc).isoformat(),
        },
    }
