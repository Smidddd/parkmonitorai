
import io
import os
from pathlib import Path
from PIL import Image
from aiohttp import web
import random

async def hello(request):
    return web.Response(text="Hello, world")

def get_random_file(ext, top=os.getcwd()):
    file_list = list(Path(top).glob(f"**/*.{ext}"))
    if not len(file_list):
        return f"No files matched that extension: {ext}"
    rand = random.randint(0, len(file_list) - 1)
    return file_list[rand]

async def image(req: web.Request):
    camera = req.rel_url.query['camera']

    #im = Image.open("apto/kamera-fpv17/20190413110001.jpg")
    im = Image.open(get_random_file("jpg", os.path.join("apto", camera)))

    stream = io.BytesIO()
    im.save(stream, "JPEG")

    return web.Response(body=stream.getvalue(), content_type='image/jpeg')

app = web.Application()
app.add_routes([web.get('/', image)])

web.run_app(app, port=8081)

# pouzitie:
#   http://localhost:8080/?camera=fpv17
#   http://localhost:8080/?camera=fpv18

