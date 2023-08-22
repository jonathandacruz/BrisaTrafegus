from flask import Flask, jsonify
from pymongo import MongoClient
from pymongo import errors as pymongo_errors
from utils.mongo_connection import MongoDBConnection
import redis
import base64
import json


# Criar uma instância do Flask
app = Flask(__name__)

# Crie uma conexão com o servidor Redis
r = redis.Redis(host='redis', port=6379)
clientMongo = MongoClient('mongodb://localhost:27017/')


# Configurações do MongoDB
MONGO_HOST = 'localhost'
MONGO_PORT = 27017
DB_NAME = 'trafegus'  # Nome do banco de dados
 

mongo_connection = MongoDBConnection(MONGO_HOST, MONGO_PORT)

# Rota para buscar dados na coleção 'cargas'
@app.route('/get_cargas')
def get_cargas():
    try:
        db = mongo_connection.get_database(DB_NAME)
        collection = db.cargas  # Nome da coleção 'cargas'
        data = list(collection.find())  # Recupera todos os documentos da coleção 'cargas'
        return jsonify(data)
    except pymongo_errors.ConnectionError as e:
        error_message = "Error connecting to MongoDB: " + str(e)
        print(error_message)
        return jsonify({"error": error_message})




# Rota para a página inicial
@app.route('/')
def index():
    lista_resultado = r.lrange('1111', 0, -1)
    lista_decodificada = [item.decode('utf-8') for item in lista_resultado]
    return jsonify({'resultado': lista_decodificada})

# Executar a aplicação se este arquivo for executado diretamente
if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5005)
