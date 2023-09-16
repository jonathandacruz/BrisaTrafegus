import pandas as pd
import matplotlib.pyplot as plt

tabela = pd.read_csv('dados.csv')

# Filtrar os codigos para pegar somente um de cada
tabela_filtro = tabela['esis_espa_codigo'].unique()

# Criando o grafico de frequencia
coluna = 'esis_espa_codigo'
frequencia = tabela[coluna].value_counts()
frequencia = frequencia.sort_values(ascending=False)
plt.figure(figsize=(12,6))
frequencia.plot(kind='bar')
plt.xlabel('Código dos eventos')
plt.ylabel('Frequência')
plt.title('Gráfico de Frequência de eventos')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()
print(frequencia)
