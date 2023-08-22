from flask import Flask, jsonify
import redis
import base64
import json

# Criar uma instância do Flask
app = Flask(__name__)

# Crie uma conexão com o servidor Redis
r = redis.Redis(host='redis', port=6379)


# Rota para a página inicial
@app.route('/')
def index():
    lista_resultado = r.lrange('1111', 0, -1)
    lista_decodificada = [item.decode('utf-8') for item in lista_resultado]
    return jsonify({'resultado': lista_decodificada})

# Executar a aplicação se este arquivo for executado diretamente
if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5005)
