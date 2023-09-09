from flask import Flask
from .utils.mongodb import init_mongo_db
from .utils.redisdb import init_redis_connection
from .routes.routes import main_bp
from .routes.auth_routes import auth_bp
from .routes.eventos_route import eventos_bp
from .routes.client_config_routes import client_config_bp
from .routes.viagens_routes import viagens_bp

def create_app():
    app = Flask(__name__)

    app.config.from_pyfile('config.py')

    init_mongo_db(app)
    init_redis_connection(app)

    app.register_blueprint(main_bp)
    app.register_blueprint(auth_bp)
    app.register_blueprint(eventos_bp)
    app.register_blueprint(client_config_bp)
    app.register_blueprint(viagens_bp)

    return app
