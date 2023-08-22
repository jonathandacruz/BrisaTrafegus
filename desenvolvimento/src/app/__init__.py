from flask import Flask
from .utils.mongodb import init_mongo_db
from .utils.redisdb import init_redis_connection
from .routes.routes import main_bp

def create_app():
    app = Flask(__name__)

    init_mongo_db(app)
    init_redis_connection(app)

    app.register_blueprint(main_bp)

    return app
