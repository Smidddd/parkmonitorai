#!/usr/bin/python3

import io
import os
import random
import time
from pathlib import Path
from PIL import Image
from aiohttp import web

camera_images = {}  # Dictionary to store image paths and update times per camera
image_duration = 30  # Duration in seconds (3 minutes)

async def hello(request):
    return web.Response(text="Hello, world")

def get_random_file(ext, top=os.getcwd()):
    file_list = list(Path(top).glob(f"**/*.{ext}"))
    if not len(file_list):
        return f"No files matched that extension: {ext}"
    rand = random.randint(0, len(file_list) - 1)
    return file_list[rand]

def get_camera_image(camera):
    # Check if camera is in the dictionary and if the current image is still valid
    current_time = time.time()
    if camera in camera_images:
        image_path, last_updated = camera_images[camera]
        if (current_time - last_updated) < image_duration:
            return image_path  # Return current image if still valid

    # Update image since no valid image exists or time expired
    new_image_path = get_random_file("jpg", os.path.join("apto", camera))
    camera_images[camera] = (new_image_path, current_time)
    return new_image_path

async def image(req: web.Request):
    camera = req.rel_url.query['camera']
    image_path = get_camera_image(camera)

    with open(image_path, 'rb') as file:
        im = Image.open(file)
        stream = io.BytesIO()
        im.save(stream, "JPEG")
        return web.Response(body=stream.getvalue(), content_type='image/jpeg')

app = web.Application()
app.add_routes([web.get('/', image)])

web.run_app(app, port=8081)

# pouzitie:
#   http://localhost:8081/?camera=fpv17
#   http://localhost:8081/?camera=fpv18