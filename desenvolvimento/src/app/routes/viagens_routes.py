import logging

from bson import json_util, ObjectId
import json
import uuid

from flask import Blueprint, jsonify, current_app, request
from app.utils.mongodb import get_mongo_db
from app.utils.redisdb import get_redis_connection
from app.routes.auth_routes import token_required
from app.models.viagem_model import ViagemModel

viagens_bp = Blueprint('viagens', __name__)

@viagens_bp.route('/viagens', methods=['GET'])
@token_required
def get_viagens(identity: str):
    db = get_mongo_db()

    data = db.viagens.find({})

    if not data:
        return jsonify({'message': 'Nenhuma viagem cadastrada'}), 404

    serialized_data = json.loads(json_util.dumps(data))

    return jsonify(serialized_data), 200

@viagens_bp.route('/viagens/<id>', methods=['GET'])
@token_required
def get_viagem(identity: str, id: str):
    db = get_mongo_db()

    try:
        object_id = ObjectId(id)
    except Exception as e:
        return jsonify({'message': 'Invalid ID format'}), 400

    data = db.viagens.find_one({'_id': object_id})

    if not data:
        return jsonify({'message': 'Viagem não encontrada'}), 404

    serialized_data = json.loads(json_util.dumps(data))

    return jsonify(serialized_data), 200

@viagens_bp.route('/viagens', methods=['POST'])
@token_required
def create_viagem(identity: str):
    db = get_mongo_db()

    data = request.get_json()

    if not data:
        return jsonify({'message': 'Nenhum dado fornecido'}), 400

    try:
        empresa_id = data.get('empresa_id')
        caminhao = data.get('caminhao')
        rota = data.get('rota')
        risco = data.get('risco')
        regra = data.get('regra')
        lastBrokenRuleTimestamp = data.get('lastBrokenRuleTimestamp')
        machineLearningPrediction = data.get('machineLearningPrediction')
        sinistro = data.get('sinistro')
    except Exception as e:
        return jsonify({'message': 'Dados inválidos'}), 400

    viagem = ViagemModel(empresa_id, caminhao, rota, risco, regra, lastBrokenRuleTimestamp, machineLearningPrediction, sinistro)
    inserted_document = viagem.criar_viagem()

    inserted_id = inserted_document.inserted_id

    current_app.logger.error(inserted_id)

    inserted_data = json.loads(json_util.dumps(db.viagens.find({"_id": inserted_id})))

    return jsonify(inserted_data), 201

@viagens_bp.route('/viagens/<id>', methods=['POST'])
@token_required
def marcar_sinistro(identity: str, id: str):
    db = get_mongo_db()

    try:
        object_id = ObjectId(id)
    except Exception as e:
        return jsonify({'message': 'Invalid ID format'}), 400

    data = request.get_json()

    if not data:
        return jsonify({'message': 'Nenhum dado fornecido'}), 400

    documento = db.viagens.find_one({'_id': object_id})

    if not data:
        return jsonify({'message': 'Viagem não encontrada'}), 404

    documento['sinistro'] = data.get('sinistro')

    documento = db.viagens.find_one_and_update({'_id': object_id}, {'$set': documento}, return_document=True)

    serialized_data = json.loads(json_util.dumps(documento))

    return jsonify(serialized_data), 200