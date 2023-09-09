import logging

from bson import json_util, ObjectId
import json
import uuid

from flask import Blueprint, jsonify, current_app, request
from app.utils.mongodb import get_mongo_db
from app.utils.redisdb import get_redis_connection
from app.routes.auth_routes import token_required
from app.models.client_config_model import ClientConfigModel

client_config_bp = Blueprint('client_config', __name__)

@client_config_bp.route('/client-config', methods=['GET'])
@token_required
def get_all_client_config(identity: str):
    current_app.logger.info(f'User {identity} is requesting client config')
    db = get_mongo_db()
    data = list(db.client_config.find({}))

    if not data:
        return jsonify({'message': 'Config not found'}), 404

    serialized_data = json.loads(json_util.dumps(data))

    return jsonify({"config": serialized_data})


@client_config_bp.route('/client-config/<id>', methods=['GET'])
@token_required
def get_client_config(identity: str, id: str):
    current_app.logger.info(f'User {identity} is requesting client config')
    db = get_mongo_db()

    try:
        object_id = ObjectId(id)
    except Exception as e:
        return jsonify({'message': 'Invalid ID format'}), 400

    data = db.client_config.find_one({'_id': object_id})

    if not data:
        return jsonify({'message': 'Config not found'}), 404

    serialized_data = json.loads(json_util.dumps(data))

    return jsonify(serialized_data), 200

@client_config_bp.route('/client-config', methods=['POST'])
@token_required
def create_client_config(identity: str):
    current_app.logger.info(f'User {identity} is creating client config')
    redis = get_redis_connection()

    data = request.get_json()

    if not data:
        return jsonify({'message': 'No input data provided'}), 400

    empresa_id = data.get('empresa_id')
    tipo = data.get('tipo')
    regras = data.get('regras')

    if not empresa_id:
        return jsonify({'message': 'No empresa_id provided'}), 400

    if not tipo:
        return jsonify({'message': 'No tipo provided'}), 400

    if not regras:
        return jsonify({'message': 'No regras provided'}), 400

    config_model = ClientConfigModel(empresa_id, tipo, regras)

    inserted_data = config_model.criar_config()

    response_data = {
        'message': 'Client config created successfully',
        'config_id': str(inserted_data.inserted_id),
        'empresa_id': empresa_id,
        'tipo': tipo,
        'regras': regras
    }

    config_redis = redis.lrange(empresa_id, 0, -1)
    config_redis = [valor.decode('utf-8') for valor in config_redis]

    codigos_config = []

    for regra in regras:
        for codigos in regra.get('codigos'):
            codigos_config.append(str(codigos))

    conjunto_config_redis = set(config_redis)
    conjunto_codigos_config = set(codigos_config)

    diferenca_entre_configs = conjunto_codigos_config - conjunto_config_redis

    lista_diferenca = list(diferenca_entre_configs)

    if lista_diferenca != []:
        redis.lpush(empresa_id, *lista_diferenca)

    return jsonify(response_data), 201