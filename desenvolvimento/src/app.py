from flask import Flask

# Criar uma instância do Flask
app = Flask(__name__)

# Rota para a página inicial
@app.route('/')
def index():
    return 'Olá, mundo!'

# Executar a aplicação se este arquivo for executado diretamente
if __name__ == '__main__':
    app.run()
