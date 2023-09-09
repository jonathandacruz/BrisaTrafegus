import uuid
from app.utils.mongodb import get_mongo_db
from app.models.regras_quebradas_model import RegrasQuebradasModel
from pymongo.collection import Collection


class ViagemModel:

    def __init__(self, empresa_id: uuid, caminhao: uuid, rota: uuid, risco: int,
                 regra: list[RegrasQuebradasModel], lastBrokenRuleTimestamp: str, machineLearningPrediction: int, sinistro: bool):
        self.empresa_id = empresa_id
        self.caminhao = caminhao
        self.rota = rota
        self.risco = risco
        self.regra = regra
        self.lastBrokenRuleTimestamp = lastBrokenRuleTimestamp
        self.machineLearningPrediction = machineLearningPrediction
        self.sinistro = sinistro
        self.collection = get_mongo_db().viagens

    def criar_viagem(self):
        config = {
            "empresa_id": self.empresa_id,
            "caminhao": self.caminhao,
            "rota": self.rota,
            "risco": self.risco,
            "regra": self.regra,
            "lastBrokenRuleTimestamp": self.lastBrokenRuleTimestamp,
            "machineLearningPrediction": self.machineLearningPrediction,
            "sinistro": self.sinistro
        }

        return self.collection.insert_one(config)
