from io import BytesIO
from typing import Any, Dict

from fastapi import FastAPI, Response
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

app = FastAPI()


@app.post("/export", response_class=Response)
async def export_pdf(payload: Dict[str, Any]) -> Response:
    buffer = BytesIO()
    c = canvas.Canvas(buffer, pagesize=letter)
    text = c.beginText(50, 750)
    text.setFont("Helvetica", 12)

    for key, value in payload.items():
        text.textLine(f"{key}: {value}")

    c.drawText(text)
    c.showPage()
    c.save()
    buffer.seek(0)
    return Response(
        buffer.read(),
        media_type="application/pdf",
        headers={"Content-Disposition": "attachment; filename=export.pdf"},
    )
