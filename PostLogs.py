import pandas as pd
import requests

df = pd.read_csv('dataset.csv')

token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWlraUBtYWlraS5jb20iLCJpYXQiOjE2OTg5NDUxMTcsImV4cCI6MTY5OTIwNDMxN30.bVHJFwdu0gSR4-m4jMYIqkqArZkLNssF4-eHBZT3CUY"

url = 'http://localhost:8080/api/poc'

for _, row in df.iterrows():
    payload = {
        "empresaId": "621c93ce-e8b8-4255-876f-b39568bf48cd",
        "esis_oras_codigo": str(row['esis_oras_codigo']).replace('.0', ''),
        "esis_viag_codigo": str(row['esis_viag_codigo']).replace('.0', ''),
        "esis_espa_codigo": str(row['esis_espa_codigo']).replace('.0', '')
    }

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {token}'
    }

    response = requests.post(url, json=payload, headers=headers)

    if response.status_code == 200:
        print(f'Solicitação POST bem-sucedida para {url}')
    else:
        print(f'Erro na solicitação POST para {url}. Código de status: {response.status_code}')
