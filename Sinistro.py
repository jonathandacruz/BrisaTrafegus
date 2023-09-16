import pandas as pd
import matplotlib.pyplot as plt

# Carregue o arquivo CSV em um DataFrame
df = pd.read_csv('dados.csv')

# Lista de números que você deseja contar
numeros = ['6', '10', '13', '15', '16', '22', '25', '26', '28', '29', '30', '32', '37', '47', '50', '56']

# Use value_counts para contar os valores únicos na coluna 'esis_espa_codigo'
contagem_numeros = df['esis_espa_codigo'].astype(str).value_counts().reindex(numeros, fill_value=0)

# Exiba a contagem de cada número
for numero, contagem in contagem_numeros.items():
    print(f'O número {numero} aparece {contagem} vezes na coluna "esis_espa_codigo".')

# Crie um gráfico de barras
plt.figure(figsize=(10, 6))
contagem_numeros.plot(kind='bar', color='skyblue')
plt.xlabel('Números')
plt.ylabel('Frequência')
plt.title('Frequência dos Números na Coluna "esis_espa_codigo"')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()
