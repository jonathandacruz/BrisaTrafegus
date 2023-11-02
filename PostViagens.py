import json
import requests

token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWlraUBtYWlraS5jb20iLCJpYXQiOjE2OTg5NDUxMTcsImV4cCI6MTY5OTIwNDMxN30.bVHJFwdu0gSR4-m4jMYIqkqArZkLNssF4-eHBZT3CUY"

# Carregando os dados do arquivo JSON
with open('viagens-id-e-placa-veiculo.json', 'r') as json_file:
    dados = json.load(json_file)

# Defina a URL para onde deseja enviar as solicitações POST
url = 'http://localhost:8080/api/viagens'

# Iterando pelos dados e fazendo solicitações POST
for item in dados:
    payload = {
        "empresaId": "621c93ce-e8b8-4255-876f-b39568bf48cd",
        "esis_viag_codigo": item["esis_viag_codigo"],
        "esis_oras_codigo": item["esis_oras_codigo"],
        "riscoAtualPorcentagem": 0,
        "riscoAtualTipoSinistro": "",
        "predicaoMachineLearning": 0,
        "sinistro": False,
        "regra": [],
        "inicioViagem": "2023-09-10T17:08:15.622384788"
    }

    header = {'Content-Type': 'application/json',
              'Authorization': f'Bearer {token}'
    }

    response = requests.post(url, json=payload, headers=header)

    if response.status_code == 200:
        print(f'Solicitação POST bem-sucedida para {url}')
    else:
        print(f'Erro na solicitação POST para {url}. Código de status: {response.status_code}')
