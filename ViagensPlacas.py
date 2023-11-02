import json
import pandas as pd

# Carregando o arquivo CSV em um DataFrame
df = pd.read_csv('dataset.csv')

# Encontrando os valores únicos na coluna "esis_viag_codigo"
valores_unicos = df['esis_viag_codigo'].unique()

# Criando uma lista de dicionários para armazenar os resultados
resultado = []

# Iterando pelos valores únicos e encontrando os valores correspondentes em "esis_oras_codigo"
for valor_unico in valores_unicos:
    linhas_correspondentes = df[df['esis_viag_codigo'] == valor_unico]

    if not linhas_correspondentes.empty:
        linha = linhas_correspondentes.iloc[0]
        valor_oras = linha['esis_oras_codigo']
        resultado.append({"esis_viag_codigo": str(valor_unico).replace('.0', ''), "esis_oras_codigo": str(valor_oras).replace('.0', '')})

# Salvando o resultado em um arquivo JSON
with open('viagens-id-e-placa-veiculo.json', 'w') as json_file:
    json.dump(resultado, json_file)

print("Resultado salvo em viagens-id-e-placa-veiculo.json")
