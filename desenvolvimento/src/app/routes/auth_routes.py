from flask import current_app, Blueprint, request, jsonify
from app.utils.auth import generate_jwt_token, token_required
from flask_bcrypt import Bcrypt
from app.utils.mongodb import get_mongo_db
from app.utils.redisdb import get_redis_connection
from app.models.user_model import UserModel

auth_bp = Blueprint('auth', __name__)
bcrypt = Bcrypt()

@auth_bp.route('/signup', methods=['POST'])
def signup():
    data = request.get_json()

    if 'username' not in data or 'password' not in data:
        return jsonify({"message": "Username and password are required"}), 400

    username = data['username']
    password = data['password']

    user_model = UserModel(current_app.mongo.trafegus)
    existing_user = user_model.find_by_username(username)

    if existing_user:
        return jsonify({"message": "Username already exists"}), 400

    hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')

    user_model.create_user(username, hashed_password)

    access_token = generate_jwt_token(identity=username)
    return jsonify({"message": "User registered successfully", "access_token": access_token}), 201

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()

    if 'username' not in data or 'password' not in data:
        return jsonify({"message": "Username and password are required"}), 400

    username = data['username']
    password = data['password']

    user_model = UserModel(current_app.mongo)
    user = user_model.find_by_username(username)

    if not user or not bcrypt.check_password_hash(user['password'], password):
        return jsonify({"message": "Invalid username or password"}), 401

    access_token = generate_jwt_token(identity=username)
    return jsonify({"message": "Login successful", "access_token": access_token}), 200
