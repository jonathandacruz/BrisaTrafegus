from flask import Blueprint, jsonify, current_app
from app.utils.mongodb import get_mongo_db
from app.utils.redisdb import get_redis_connection

main_bp = Blueprint('main', __name__)

@main_bp.route('/get_cargas')
def get_cargas():
    db = get_mongo_db()
    sample_data = {'nome': 'Exemplo', 'idade': 25}
    db.collection.insert_one(sample_data)

    data = db.collection.find_one({'nome': 'Exemplo'})

    return f'Nome: {data["nome"]}, Idade: {data["idade"]}'

@main_bp.route('/')
def index():
    r = get_redis_connection()
    lista_resultado = r.lrange('1111', 0, -1)
    lista_decodificada = [item.decode('utf-8') for item in lista_resultado]
    return jsonify({'resultado': lista_decodificada})
