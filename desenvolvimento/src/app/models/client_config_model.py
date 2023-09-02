import uuid
from app.utils.mongodb import get_mongo_db
from app.models.regras_model import RegrasModel
from pymongo.collection import Collection

class ClientConfigModel:

    def __init__(self, empresa_id: uuid, tipo: str, regras: list[RegrasModel]):
        self.empresa_id = empresa_id
        self.tipo = tipo
        self.regras = regras
        self.collection = get_mongo_db().client_config

    def find_by_id(self, id: uuid):
        return self.collection.find_one({"uuid": id})

    def criar_config(self):
        config = {
            "empresa_id": self.empresa_id,
            "tipo": self.tipo,
            "regras": self.regras
        }
        return self.collection.insert_one(config)
