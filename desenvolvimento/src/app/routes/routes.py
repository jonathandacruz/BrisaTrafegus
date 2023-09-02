import json
from bson import json_util, ObjectId

from flask import Blueprint, jsonify, current_app, request
from app.utils.mongodb import get_mongo_db
from app.utils.redisdb import get_redis_connection
from app.routes.auth_routes import token_required

main_bp = Blueprint('main', __name__)

@main_bp.route('/get_cargas')
@token_required
def get_cargas(identity: str):
    current_app.logger.info(f'User {identity} is requesting cargas')
    db = get_mongo_db()
    sample_data = {'nome': 'Exemplo', 'idade': 25}
    db.cargas.insert_one(sample_data)

    data = db.cargas.find_one({'nome': 'Exemplo'})

    return f'Nome: {data["nome"]}, Idade: {data["idade"]}'

@main_bp.route('/get_redis_values')
def index():
    r = get_redis_connection()

    valores_da_lista = r.lrange("621c93ce-e8b8-4255-876f-b39568bf48cd", 0, -1)

    valores_da_lista = [valor.decode('utf-8') for valor in valores_da_lista]

    return jsonify({'empresa': "621c93ce-e8b8-4255-876f-b39568bf48cd",
                    "codigos_importantes" : valores_da_lista})

@main_bp.route('/reset_redis_values')
def reset():
    r = get_redis_connection()
    r.flushdb()
    return jsonify({'message': 'Redis resetado'})

@main_bp.route('/receber_log', methods=['POST'])
def receber_log():
    r = get_redis_connection()
    mongo = get_mongo_db()

    data = request.get_json()
    empresa_id = data.get('empresa_id')
    codigo = data.get('codigo')

    valores_da_lista = r.lrange(empresa_id, 0, -1)

    valores_da_lista = [valor.decode('utf-8') for valor in valores_da_lista]

    if str(codigo) in valores_da_lista:
        data = mongo.client_config.find({
            'empresa_id': empresa_id,
            'regras.codigos': codigo
        })

        serialized_data = json.loads(json_util.dumps(data))
        return jsonify(serialized_data), 200

    return jsonify({'message': 'Codigo de log nao importante para empresa, nao deveria processar o log.'})

