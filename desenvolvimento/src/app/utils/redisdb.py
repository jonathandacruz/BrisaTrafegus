import redis
from flask import current_app

def get_redis_connection():
    return current_app.r

def init_redis_connection(app):
    app.r = redis.Redis(host='redis', port=6379)
